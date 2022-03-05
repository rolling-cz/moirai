package cz.rolling.moirai.model.common.result;


import cz.rolling.moirai.assignment.enhancer.SolutionEnhancer;
import cz.rolling.moirai.model.common.Assignment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class MetaSolution implements Solution{
    private final List<Solution> solutionList = new ArrayList<>();

    @Override
    public Integer getRating() {
        int rating = 0;
        for (Solution s : solutionList) {
            rating += s.getRating();
        }
        return rating;
    }

    @Override
    public List<Assignment> getAssignmentList() {
        if (!solutionList.isEmpty()) {
            return solutionList.get(0).getAssignmentList();
        } else {
            return Collections.emptyList();
        }
    }

    public void addSolution(Solution solution) {
        solutionList.add(solution);
    }

    @Override
    public int compareTo(Solution o) {
        return getRating().compareTo(o.getRating());
    }

    @Override
    public ResultSummary enhanceBy(SolutionEnhancer solutionEnhancer) {
        return solutionEnhancer.enhance(this);
    }
}
