package cz.rolling.moirai.assignment.enhancer;

import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.result.Solution;
import cz.rolling.moirai.model.common.result.VerboseSolution;

import java.util.List;

public interface SolutionEnhancer {
    VerboseSolution enhance(Solution solution);

    List<DistributionHeader> getHeaderList();
}
