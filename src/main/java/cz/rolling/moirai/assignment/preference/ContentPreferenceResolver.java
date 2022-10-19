package cz.rolling.moirai.assignment.preference;

import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AttributeAssignment;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.form.WizardState;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentPreferenceResolver extends AbstractPreferenceResolver {

    private final Map<Assignment, Integer> preferenceMap;
    private final int ratingForGender;
    private final int maximumRating;
    private final List<CharacterAttribute> attributeList;

    public ContentPreferenceResolver(WizardState wizardState) {
        super(wizardState.getCharactersConfiguration().getCharacterList(), wizardState.getAlgorithmConfiguration().getUserList());
        ratingForGender = wizardState.getMainConfiguration().getRatingForGender();
        attributeList = wizardState.getMainConfiguration().getAttributeList();

        maximumRating = calculateMaximumRating();
        preferenceMap = collectPreferences(maximumRating);
    }

    private static double getDeltaRating(User user, Character character, CharacterAttribute attr) {
        Integer userAttrValue = user.getAttributeMap().get(attr.getName());
        Integer charAttrValue = character.getAttributeMap().get(attr.getName());
        if (userAttrValue == null) {
            throw new IllegalArgumentException("User " + user.getName() + " doesn't have attribute " + attr.getName());
        }
        if (charAttrValue == null) {
            throw new IllegalArgumentException("Char " + character.getName() + " doesn't have attribute " + attr.getName());
        }

        int deltaAttr = attr.getDeltaFunction().getDelta(charAttrValue, userAttrValue, attr.getMin(), attr.getMax());
        return attr.getRatingFunction().getRating(attr.getRating(), deltaAttr);
    }

    @Override
    protected int getRatingForGender() {
        return ratingForGender;
    }

    private int calculateMaximumRating() {
        int rating = ratingForGender;
        for (CharacterAttribute attr : attributeList) {
            int maxDelta = attr.getDeltaFunction().getMaxDelta(attr.getMin(), attr.getMax());
            rating += attr.getRatingFunction().getRating(attr.getRating(), maxDelta);
        }
        return -1 * rating;
    }

    private Map<Assignment, Integer> collectPreferences(int maximumRating) {
        Map<Assignment, Integer> prefMap = new HashMap<>();

        for (User user : getUserList()) {
            for (Character character : getCharacterList()) {
                Integer rating = calculateRating(maximumRating, user, character);
                prefMap.put(new Assignment(user.getId(), character.getId()), rating);
            }
        }

        return prefMap;
    }

    private int calculateRating(int maximumRating, User user, Character character) {
        int rating = maximumRating;

        for (CharacterAttribute attr : attributeList) {
            rating += getDeltaRating(user, character, attr);
        }

        if (!PreferenceUtils.isCorrectGender(user, character)) {
            rating += ratingForGender;
        }

        return rating;
    }

    public int getMaximumRating() {
        return maximumRating;
    }

    @Override
    public int calculateRating(List<Assignment> assignmentList) {
        int rating = 0;
        for (Assignment assignment : assignmentList) {
            rating += preferenceMap.get(assignment);
        }
        return rating;
    }

    @Override
    public Integer getRating(Assignment a) {
        return preferenceMap.get(a);
    }

    public Map<Assignment, Integer> getPreferenceMap() {
        return Collections.unmodifiableMap(preferenceMap);
    }

    public List<AttributeAssignment> evaluateAssignmentAttributes(Assignment assignment) {
        User user = getUserList().get(assignment.getUserId());
        Character character = getCharacterList().get(assignment.getCharId());

        return attributeList.stream().map(attribute -> new AttributeAssignment(
                attribute.getName(),
                user.getAttributeMap().get(attribute.getName()),
                character.getAttributeMap().get(attribute.getName()),
                Math.round(getDeltaRating(user, character, attribute))
        )).collect(Collectors.toList());
    }
}
