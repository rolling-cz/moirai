package cz.rolling.moirai;

import cz.rolling.moirai.model.AlgorithmConfiguration;
import cz.rolling.moirai.model.AssignmentConfiguration;
import cz.rolling.moirai.model.CharactersConfiguration;
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

    private AssignmentConfiguration assignmentConfiguration = new AssignmentConfiguration();

    private CharactersConfiguration charactersConfiguration = new CharactersConfiguration();

    private AlgorithmConfiguration algorithmConfiguration = new AlgorithmConfiguration();
}
