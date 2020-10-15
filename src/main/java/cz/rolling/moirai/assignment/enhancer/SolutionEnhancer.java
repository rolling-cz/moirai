package cz.rolling.moirai.assignment.enhancer;

import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.VerboseSolution;

import java.util.List;

public interface SolutionEnhancer {
    VerboseSolution enhance(Solution solution);

    List<DistributionHeader> getHeaderList();
}
