package cz.rolling.moirai.service;

import cz.rolling.moirai.exception.WrongPlayersNumberException;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Service;

@Service
public class WizardValidator {

    public void validate(WizardState wizardState) {
        int characters = wizardState.getCharactersConfiguration().getCharacterList().size();
        int players = wizardState.getAlgorithmConfiguration().getUserList().size();
        if (characters != players) {
            throw new WrongPlayersNumberException(players, characters);
        }
    }
}
