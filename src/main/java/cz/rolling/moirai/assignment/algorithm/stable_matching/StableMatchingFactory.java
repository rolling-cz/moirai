package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.AlgorithmSpecificParameter;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class StableMatchingFactory implements AlgorithmFactory {

    private static final HashSet<AlgorithmFeature> ALGORITHM_FEATURES = new HashSet<>(Collections.singletonList(
            AlgorithmFeature.CONTENT_APPROACH
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
        return new StableMatchingAlgorithm(preferenceResolver, wizardState.getCharactersConfiguration().getNumberOfCharacters());
    }
}
