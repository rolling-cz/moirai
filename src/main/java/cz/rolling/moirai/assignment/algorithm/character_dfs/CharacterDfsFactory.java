package cz.rolling.moirai.assignment.algorithm.character_dfs;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.assignment.preference.PreferenceUtils;
import cz.rolling.moirai.model.common.AlgorithmSpecificParameter;
import cz.rolling.moirai.model.content.ContentConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class CharacterDfsFactory implements AlgorithmFactory {

    private static final HashSet<AlgorithmFeature> ALGORITHM_FEATURES = new HashSet<>(Arrays.asList(
            AlgorithmFeature.CHARACTER_APPROACH,
            AlgorithmFeature.HALF_GAME_CHARACTERS,
            AlgorithmFeature.NOT_ENOUGH_PLAYERS
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
        ContentConfiguration configuration = PreferenceUtils.createConfiguration(wizardState);
        CharacterPreferenceResolver preferenceHolder = PreferenceUtils.createPreferenceHolder(wizardState, configuration);
        return new CharacterDfsAlgorithm(preferenceHolder, configuration);
    }
}
