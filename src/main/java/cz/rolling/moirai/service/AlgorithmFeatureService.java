package cz.rolling.moirai.service;

import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AlgorithmFeatureService {

    public Set<AlgorithmFeature> determineRequiredFeatureSet(WizardState wizardState) {
        Set<AlgorithmFeature> featureSet = new HashSet<>();

        switch(wizardState.getMainConfiguration().getApproachType()) {
            case CHARACTERS:
                featureSet.add(AlgorithmFeature.CHARACTER_APPROACH);
                break;
            case CONTENT:
                featureSet.add(AlgorithmFeature.CONTENT_APPROACH);
                break;
            default:
                throw new IllegalArgumentException("Unknown approach " + wizardState.getMainConfiguration().getApproachType());
        }

        if (wizardState.getMainConfiguration().getMultiSelect() > 1) {
            featureSet.add(AlgorithmFeature.MULTI_SELECT);
        }

        if (wizardState.getCharactersConfiguration().getNumberOfCharacters() > wizardState.getAlgorithmConfiguration().getNumberOfUsers()) {
            featureSet.add(AlgorithmFeature.NOT_ENOUGH_PLAYERS);
        }

        return featureSet;
    }
}
