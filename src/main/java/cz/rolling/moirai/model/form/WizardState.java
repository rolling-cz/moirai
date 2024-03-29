package cz.rolling.moirai.model.form;

import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.UiStyle;
import cz.rolling.moirai.model.common.result.ResultSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Collections;
import java.util.List;

@Component
@SessionScope
@Getter
@Setter
@NoArgsConstructor
public class WizardState {

    private UiStyle uiStyle = UiStyle.BASIC;

    private MainConfiguration mainConfiguration = new MainConfiguration();

    private CharactersConfiguration charactersConfiguration = new CharactersConfiguration();

    private AlgorithmConfiguration algorithmConfiguration = new AlgorithmConfiguration();

    private List<ResultSummary> solutionList = Collections.singletonList(ResultSummary.createSummaryWithoutSolution("no-solution.no-input"));

    private List<DistributionHeader> distributionHeaderList;
}
