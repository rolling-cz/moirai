package cz.rolling.moirai.model.common.result;

import cz.rolling.moirai.model.common.AssignmentDetail;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ResultSummary {
    private final boolean hasSolution;

    private final Integer rating;

    private final List<AssignmentDetail> assignmentList;

    private final Map<String, Integer> distributionMap;

    private final String problemCode;

    private ResultSummary(
            boolean hasSolution,
            Integer rating,
            List<AssignmentDetail> assignmentList,
            Map<String, Integer> distributionMap,
            String problemCode
    ) {
        this.hasSolution = hasSolution;
        this.rating = rating;
        this.assignmentList = assignmentList;
        this.distributionMap = distributionMap;
        this.problemCode = problemCode;
    }

    public static ResultSummary createSummaryWithSolution(
            Integer rating,
            List<AssignmentDetail> assignmentList,
            Map<String, Integer> distributionMap
    ) {
        return new ResultSummary(true, rating, assignmentList, distributionMap, null);
    }

    public static ResultSummary createSummaryWithoutSolution(String problemCode) {
        return new ResultSummary(false, null, null, null, problemCode);
    }
}
