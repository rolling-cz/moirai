package cz.rolling.moirai.assignment.preference;

import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.User;

public final class PreferenceUtils {

    public static boolean isCorrectGender(User user, Character character) {
        return user.getWantsPlayGender() == character.getGender() ||
                user.getWantsPlayGender() == Gender.AMBIGUOUS ||
                character.getGender() == Gender.AMBIGUOUS;
    }
}
