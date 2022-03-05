package cz.rolling.moirai.model.common.result;

import cz.rolling.moirai.assignment.enhancer.SolutionEnhancer;
import cz.rolling.moirai.model.common.Assignment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DirectSolution implements Solution {

    private final Integer rating;
    private final List<Assignment> assignmentList;

    @Override
    public String toString() {
        return "Solution{" +
                "rank=" + rating +
                ", assignmentList=" + assignmentList +
                '}';
    }

    @Override
    public int compareTo(Solution o) {
        return rating.compareTo(o.getRating());
    }

    @Override
    public ResultSummary enhanceBy(SolutionEnhancer solutionEnhancer) {
        return solutionEnhancer.enhance(this);
    }
}
