package cz.rolling.moirai.model.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Getter
@Setter
@NoArgsConstructor
public class WizardState {

    private MainConfiguration mainConfiguration = new MainConfiguration();

    private CharactersConfiguration charactersConfiguration = new CharactersConfiguration();

    private AlgorithmConfiguration algorithmConfiguration = new AlgorithmConfiguration();
}
