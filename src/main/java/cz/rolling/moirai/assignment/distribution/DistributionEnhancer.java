package cz.rolling.moirai.assignment.distribution;

import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.VerboseSolution;

public interface DistributionEnhancer {
    VerboseSolution addDistribution(Solution solution);
}
