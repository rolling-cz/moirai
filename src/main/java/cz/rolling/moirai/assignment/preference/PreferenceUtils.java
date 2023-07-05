package cz.rolling.moirai.assignment.preference;

import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.content.ContentConfiguration;
import cz.rolling.moirai.model.form.AlgorithmConfiguration;
import cz.rolling.moirai.model.form.CharactersConfiguration;
import cz.rolling.moirai.model.form.MainConfiguration;
import cz.rolling.moirai.model.form.WizardState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class PreferenceUtils {

    public static boolean isCorrectGender(User user, Character character) {
        return user.getWantsPlayGender() == character.getGender() ||
                user.getWantsPlayGender() == Gender.AMBIGUOUS ||
                character.getGender() == Gender.AMBIGUOUS;
    }

    public static ContentConfiguration createConfiguration(WizardState wizardState) {
        MainConfiguration mainConf = wizardState.getMainConfiguration();
        CharactersConfiguration charConf = wizardState.getCharactersConfiguration();
        ContentConfiguration configuration = new ContentConfiguration();
        AlgorithmConfiguration algConf = wizardState.getAlgorithmConfiguration();

        configuration.setCharacterCount(charConf.getNumberOfCharacters());
        configuration.setUserCount(algConf.getUserList().size());
        configuration.setPreferencesPerUser(mainConf.getNumberOfPreferredCharacters());
        configuration.setRatings(getRatings(mainConf.getRatingForPreferredCharacters()));

        configuration.setUnwantedCharPreference(mainConf.getRatingForNotSpecifiedChar());
        configuration.setUnwantedCharGender(mainConf.getRatingForGender());
        return configuration;
    }

    private static List<Integer> getRatings(String preferencesAsString) {
        String[] prefString = preferencesAsString.split(",");
        return Arrays.stream(prefString).map(Integer::parseInt).collect(Collectors.toList());
    }

    public static CharacterPreferenceResolver createPreferenceHolder(WizardState wizardState, ContentConfiguration configuration) {
        Set<AssignmentWithRank> preferenceSet = new HashSet<>();
        wizardState.getAlgorithmConfiguration().getUserList().forEach(user -> {
            preferenceSet.addAll(user.getPreferences());
        });
        return new CharacterPreferenceResolver(
                configuration,
                preferenceSet,
                wizardState.getCharactersConfiguration().getCharacterList(),
                wizardState.getAlgorithmConfiguration().getUserList());
    }
}
