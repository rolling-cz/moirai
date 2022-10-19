package cz.rolling.moirai.service;

import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.exception.WizardValidationException;
import cz.rolling.moirai.exception.WrongPlayersNumberException;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.form.MainConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WizardValidator {
    private final Logger logger = LoggerFactory.getLogger(WizardValidator.class);

    private final AlgorithmFeatureService algorithmFeatureService;

    private final ApplicationContext context;

    @Autowired
    public WizardValidator(AlgorithmFeatureService algorithmFeatureService,
                           ApplicationContext context
    ) {
        this.algorithmFeatureService = algorithmFeatureService;
        this.context = context;
    }

    public void validate(WizardState wizardState) {
        if (wizardState.getAlgorithmConfiguration().getAlgorithmFactoryName() == null) {
            throw new WizardValidationException("exception.algorithm-not-set");
        }

        Set<AlgorithmFeature> requiredFeatureSet = algorithmFeatureService.determineRequiredFeatureSet(wizardState);
        AlgorithmFactory algorithmFactory;
        try {
            algorithmFactory = (AlgorithmFactory) context.getBean(
                    Class.forName(wizardState.getAlgorithmConfiguration().getAlgorithmFactoryName())
            );
        } catch (ClassNotFoundException e) {
            logger.warn("Selected non-existent algorithm {}.", wizardState.getAlgorithmConfiguration().getAlgorithmFactoryName());
            throw new WizardValidationException("exception.algorithm-not-exist");
        }
        if (!algorithmFactory.getSupportedFeatures().containsAll(requiredFeatureSet)) {
            logger.warn("Selected algorithm {} which is missing features {}.",
                    wizardState.getAlgorithmConfiguration().getAlgorithmFactoryName(),
                    requiredFeatureSet);
            throw new WizardValidationException("exception.algorithm-not-supporting-all-features");
        }

        int characters = wizardState.getCharactersConfiguration().getCharacterList().size();
        int players = wizardState.getAlgorithmConfiguration().getUserList().size();
        if (characters != players && !algorithmFactory.getSupportedFeatures().contains(AlgorithmFeature.NOT_ENOUGH_PLAYERS)) {
            throw new WrongPlayersNumberException(players, characters);
        }
    }

    public void validateMainConfig(MainConfiguration mainConfiguration, BindingResult bindingResult) {
        // validate unique attr names
        List<String> names = mainConfiguration.getAttributeList().stream()
                .map(CharacterAttribute::getName)
                .collect(Collectors.toList());

        Set<String> alreadyExisted = new HashSet<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (alreadyExisted.contains(name)) {
                bindingResult.rejectValue("attributeList["+ i +"].name", "validator-error.attr-duplicate-name");
                break;
            }
            alreadyExisted.add(name);
        }

        // validate attribute interval
        for (int i = 0; i < mainConfiguration.getAttributeList().size(); i++) {
            CharacterAttribute attr = mainConfiguration.getAttributeList().get(i);
            if (attr.getMax() == null || attr.getMin() == null) {
                continue;
            }

            if (attr.getMax() < attr.getMin()) {
                bindingResult.rejectValue("attributeList["+ i +"].max", "validator-error.attr-wrong-interval");
                break;
            }
        }
    }
}
