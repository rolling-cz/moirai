package cz.rolling.moirai.assignment.algorithm.smti_kiraly;

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
public class SmtiKiralyFactory implements AlgorithmFactory {

    private static final HashSet<AlgorithmFeature> ALGORITHM_FEATURES = new HashSet<>(Arrays.asList(
            AlgorithmFeature.CONTENT_APPROACH,
            AlgorithmFeature.MULTI_SELECT,
            AlgorithmFeature.NOT_ENOUGH_PLAYERS,
            AlgorithmFeature.BLOCKED_ASSIGNMENTS
    ));

    private static final Set<AlgorithmSpecificParameter<?>> PARAMETER_SET = new HashSet<>();

    @Override
    public String getName() {
        return "smti-kiraly";
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
        int missingPlayers = wizardState.getCharactersConfiguration().getNumberOfCharacters() -
                wizardState.getAlgorithmConfiguration().getNumberOfUsers();

        ContentPreferenceResolver preferenceResolver = new ContentPreferenceResolver(wizardState);
        return new SmtiKiralyAlgorithm(
                preferenceResolver,
                wizardState.getAlgorithmConfiguration().getNumberOfUsers(),
                wizardState.getCharactersConfiguration().getNumberOfCharacters(),
                wizardState.getMainConfiguration().getMultiSelect(),
                wizardState.getAlgorithmConfiguration().getBlockedAssignmentList()
        );
    }
}
