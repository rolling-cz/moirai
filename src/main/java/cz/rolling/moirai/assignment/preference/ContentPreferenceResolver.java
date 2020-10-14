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

    public ContentPreferenceResolver(WizardState wizardState){
        preferenceMap = collectPreferences(wizardState);
    }

    private static Map<Assignment, Integer> collectPreferences(WizardState wizardState) {
        Map<Assignment, Integer> prefMap = new HashMap<>();

        for (User user: wizardState.getAlgorithmConfiguration().getUserList()) {
            for (Character character: wizardState.getCharactersConfiguration().getCharacterList()) {
                Integer rating = calculateRating(user, character, wizardState.getMainConfiguration().getAttributeList());
                prefMap.put(new Assignment(user.getId(), character.getId()), rating);
            }
        }

        return prefMap;
    }

    private static int calculateRating(User user, Character character, List<CharacterAttribute> attributeList) {
        int rating = 0;

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

        return rating;
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
    public Integer getAssignmentType(Assignment a) {
        // TODO translate to buckets
        return preferenceMap.get(a);
    }

    public Map<Assignment, Integer> getPreferenceMap() {
        return Collections.unmodifiableMap(preferenceMap);
    }
}
