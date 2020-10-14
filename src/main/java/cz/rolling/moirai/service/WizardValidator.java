package cz.rolling.moirai.service;

import cz.rolling.moirai.exception.WrongPlayersNumberException;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.form.MainConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WizardValidator {

    public void validate(WizardState wizardState) {
        int characters = wizardState.getCharactersConfiguration().getCharacterList().size();
        int players = wizardState.getAlgorithmConfiguration().getUserList().size();
        if (characters != players) {
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
