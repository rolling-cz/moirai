package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.AlgorithmSpecificParameter;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class StableMatchingFactory implements AlgorithmFactory {

    private static final HashSet<AlgorithmFeature> ALGORITHM_FEATURES = new HashSet<>(Arrays.asList(
            AlgorithmFeature.CONTENT_APPROACH,
            AlgorithmFeature.MULTI_SELECT
    ));

    private static final Set<AlgorithmSpecificParameter<?>> PARAMETER_SET = new HashSet<>();

    @Override
    public String getName() {
        return "stable-matching";
    }

    @Override
    public Class<?> getFactoryClass() {
        return this.getClass();
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
    public Algorithm createAlgorithmManager(WizardState wizardState) {
        ContentPreferenceResolver preferenceResolver = new ContentPreferenceResolver(wizardState);
        return new StableMatchingAlgorithm(
                preferenceResolver,
                new StableMatchingProcessorVar1(),
                wizardState.getCharactersConfiguration().getNumberOfCharacters(),
                wizardState.getMainConfiguration().getMultiSelect(),
                false
        );
    }
}
