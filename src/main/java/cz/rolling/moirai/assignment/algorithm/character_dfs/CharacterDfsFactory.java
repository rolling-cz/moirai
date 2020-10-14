package cz.rolling.moirai.assignment.algorithm.character_dfs;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.model.common.AlgorithmSpecificParameter;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.content.ContentConfiguration;
import cz.rolling.moirai.model.form.AlgorithmConfiguration;
import cz.rolling.moirai.model.form.CharactersConfiguration;
import cz.rolling.moirai.model.form.MainConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CharacterDfsFactory implements AlgorithmFactory {

    private static final HashSet<AlgorithmFeature> ALGORITHM_FEATURES = new HashSet<>(Arrays.asList(
            AlgorithmFeature.CHARACTER_APPROACH,
            AlgorithmFeature.HALF_GAME_CHARACTERS
    ));

    private static final Set<AlgorithmSpecificParameter<?>> PARAMETER_SET = new HashSet<>(Arrays.asList(
            new AlgorithmSpecificParameter<>("maximumTriedSolution", Integer.class, 100000),
            new AlgorithmSpecificParameter<>("searchWide", Integer.class, 2)
    ));

    @Override
    public String getName() {
        return "best-find-dfs";
    }

    @Override
    public Set<AlgorithmFeature> getSupportedFeatures() {
        return ALGORITHM_FEATURES;
    }

    @Override
    public Set<AlgorithmSpecificParameter<?>> getParameterList() {
        return PARAMETER_SET;
    }

    @Override
    public Class<?> getFactoryClass() {
        return this.getClass();
    }

    @Override
    public Algorithm createAlgorithmManager(WizardState wizardState) {
        ContentConfiguration configuration = createConfiguration(wizardState);
        CharacterPreferenceResolver preferenceHolder = createPreferenceHolder(wizardState, configuration);
        return new CharacterDfsAlgorithm(preferenceHolder, configuration);
    }

    private ContentConfiguration createConfiguration(WizardState wizardState) {
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

    private List<Integer> getRatings(String preferencesAsString) {
        String[] prefString = preferencesAsString.split(",");
        return Arrays.stream(prefString).map(Integer::parseInt).collect(Collectors.toList());
    }

    private CharacterPreferenceResolver createPreferenceHolder(WizardState wizardState, ContentConfiguration configuration) {
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
