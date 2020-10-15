package cz.rolling.moirai.assignment.distribution;

import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.VerboseSolution;

import java.util.List;

public interface DistributionEnhancer {
    VerboseSolution addDistribution(Solution solution);

    List<DistributionHeader> getHeaderList();
}
