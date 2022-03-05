package cz.rolling.moirai.model.common.result;

import cz.rolling.moirai.assignment.enhancer.SolutionEnhancer;
import cz.rolling.moirai.model.common.Assignment;

import java.util.List;

public interface Solution extends Comparable<Solution> {
    Integer getRating();

    List<Assignment> getAssignmentList();

    ResultSummary enhanceBy(SolutionEnhancer solutionEnhancer);
}
