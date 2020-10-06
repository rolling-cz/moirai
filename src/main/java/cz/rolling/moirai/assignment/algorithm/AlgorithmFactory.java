package cz.rolling.moirai.assignment.algorithm;

import cz.rolling.moirai.model.common.AlgorithmSpecificParameter;
import cz.rolling.moirai.model.form.WizardState;

import java.util.Set;

public interface AlgorithmFactory {
    Algorithm createAlgorithmManager(WizardState wizardState);

    String getName();

    Class<?> getFactoryClass();

    Set<AlgorithmFeature> getSupportedFeatures();

    Set<AlgorithmSpecificParameter<?>> getParameterList();
}
