package cz.rolling.moirai.assignment.enhancer;

import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.result.DirectSolution;
import cz.rolling.moirai.model.common.result.MetaSolution;
import cz.rolling.moirai.model.common.result.NoSolution;
import cz.rolling.moirai.model.common.result.ResultSummary;

import java.util.List;

public interface SolutionEnhancer {
    ResultSummary enhance(NoSolution solution);

    ResultSummary enhance(DirectSolution solution);

    ResultSummary enhance(MetaSolution solution);

    List<DistributionHeader> getHeaderList();
}
