package cz.rolling.moirai.assignment.preference;

import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.form.WizardState;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentPreferenceResolver implements PreferenceResolver {

    private final Map<Assignment, Integer> preferenceMap;

    private final int maximumRating;

    public ContentPreferenceResolver(WizardState wizardState) {
        maximumRating = calculateMaximumRating(wizardState);
        preferenceMap = collectPreferences(maximumRating, wizardState);
    }

    private static int calculateMaximumRating(WizardState wizardState) {
        int rating = wizardState.getMainConfiguration().getRatingForGender();
        for (CharacterAttribute attr : wizardState.getMainConfiguration().getAttributeList()) {
            int maxDelta = attr.getMax() - attr.getMin();
            rating += attr.getFunction().getRating(attr.getRating(), maxDelta);
        }
        return -1 * rating;
    }

    private static Map<Assignment, Integer> collectPreferences(int maximumRating, WizardState wizardState) {
        Map<Assignment, Integer> prefMap = new HashMap<>();
        int ratingForGender = wizardState.getMainConfiguration().getRatingForGender();
        List<CharacterAttribute> attributeList = wizardState.getMainConfiguration().getAttributeList();

        for (User user : wizardState.getAlgorithmConfiguration().getUserList()) {
            for (Character character : wizardState.getCharactersConfiguration().getCharacterList()) {
                Integer rating = calculateRating(maximumRating, ratingForGender, user, character, attributeList);
                prefMap.put(new Assignment(user.getId(), character.getId()), rating);
            }
        }

        return prefMap;
    }

    private static int calculateRating(int maximumRating,
                                       int ratingForGender,
                                       User user,
                                       Character character,
                                       List<CharacterAttribute> attributeList) {
        int rating = maximumRating;

        Map<String, Integer> userAttrMap = user.getAttributeMap();
        Map<String, Integer> charAttrMap = character.getAttributeMap();

        for (CharacterAttribute attr : attributeList) {
            Integer userAttrValue = userAttrMap.get(attr.getName());
            Integer charAttrValue = charAttrMap.get(attr.getName());
            if (userAttrValue == null) {
                throw new IllegalArgumentException("User " + user.getName() + " doesn't have attribute " + attr.getName());
            }
            if (charAttrValue == null) {
                throw new IllegalArgumentException("Char " + character.getName() + " doesn't have attribute " + attr.getName());
            }

            int delta = Math.abs(userAttrValue - charAttrValue);
            rating += attr.getFunction().getRating(attr.getRating(), delta);
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
}
