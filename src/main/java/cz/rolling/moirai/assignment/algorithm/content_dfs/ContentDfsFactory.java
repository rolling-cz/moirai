package cz.rolling.moirai.assignment.algorithm.content_dfs;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.assignment.helper.PreferencesHolder;
import cz.rolling.moirai.model.common.AlgorithmSpecificParameter;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.content.ContentConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class ContentDfsFactory implements AlgorithmFactory {

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
        PreferencesHolder preferenceHolder = createPreferenceHolder(wizardState, configuration);
        return new ContentDfsAlgorithm(preferenceHolder, configuration);
    }

    private ContentConfiguration createConfiguration(WizardState wizardState) {
        ContentConfiguration configuration = new ContentConfiguration();
        configuration.setCharacterCount(wizardState.getCharactersConfiguration().getNumberOfCharacters());
        configuration.setUserCount(wizardState.getAlgorithmConfiguration().getUserList().size());
        // TODO map configuration

        return configuration;
    }

    private PreferencesHolder createPreferenceHolder(WizardState wizardState, ContentConfiguration configuration) {
        Set<AssignmentWithRank> preferenceSet = new HashSet<>();
        wizardState.getAlgorithmConfiguration().getUserList().forEach(user -> {
            preferenceSet.addAll(user.getPreferences());
        });
        return new PreferencesHolder(
                configuration,
                preferenceSet,
                wizardState.getCharactersConfiguration().getCharacterList(),
                wizardState.getAlgorithmConfiguration().getUserList());
    }
}
