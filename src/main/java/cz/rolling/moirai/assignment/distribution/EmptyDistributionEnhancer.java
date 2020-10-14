package cz.rolling.moirai.assignment.distribution;

import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.VerboseSolution;

import java.util.Collections;

public class EmptyDistributionEnhancer implements DistributionEnhancer{
    @Override
    public VerboseSolution addDistribution(Solution solution) {
        return new VerboseSolution(solution, Collections.emptyMap(), Collections.emptyMap());
    }
}
