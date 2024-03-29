package cz.rolling.moirai.assignment.enhancer;

import cz.rolling.moirai.assignment.helper.Counter;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AssignmentDetail;
import cz.rolling.moirai.model.common.AssignmentDetailContent;
import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.MessageWithParams;
import cz.rolling.moirai.model.common.result.DirectSolution;
import cz.rolling.moirai.model.common.result.MetaSolution;
import cz.rolling.moirai.model.common.result.NoSolution;
import cz.rolling.moirai.model.common.result.ResultSummary;
import cz.rolling.moirai.model.common.result.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ContentSolutionEnhancer implements SolutionEnhancer {

    private static final int NUMBER_OF_BUCKETS = 10;
    private static final int PERCENT_PER_BUCKET = 100 / NUMBER_OF_BUCKETS;
    private static final String HEADER_BUCKET = "execution-results.header.bucket";
    private final ContentPreferenceResolver preferenceResolver;

    public ContentSolutionEnhancer(ContentPreferenceResolver preferenceResolver) {
        this.preferenceResolver = preferenceResolver;
    }

    @Override
    public ResultSummary enhance(MetaSolution solution) {
        List<Assignment> assignments = solution.getSolutionList().stream()
                .map(Solution::getAssignmentList)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(Assignment::getUserId))
                .collect(Collectors.toList());
        return enhanceInner(assignments, solution.getRating());
    }

    public ResultSummary enhance(DirectSolution solution) {
        return enhanceInner(solution.getAssignmentList(), solution.getRating());
    }

    public ResultSummary enhance(NoSolution solution) {
        return ResultSummary.createSummaryWithoutSolution(solution.getReasonCode());
    }

    private ResultSummary enhanceInner(List<Assignment> assignmentList, int rating) {
        Map<Integer, Counter> goodAssignments = new HashMap<>();
        IntStream.range(0, NUMBER_OF_BUCKETS).forEach(i ->
                goodAssignments.put(i, new Counter())
        );

        Map<Assignment, Counter> duplicates = duplicationCount(assignmentList);

        List<AssignmentDetail> assignmentDetailList = assignmentList.stream()
                .map(assignment -> new AssignmentDetailContent(
                        assignment,
                        preferenceResolver.getRating(assignment),
                        preferenceResolver.evaluateGenderAssignment(assignment),
                        preferenceResolver.evaluateAssignmentAttributes(assignment),
                        preferenceResolver.evaluateLabelsAssignment(assignment),
                        duplicates.get(assignment).getNumber() > 1,
                        preferenceResolver.isAssignmentBlocked(assignment)
                )).collect(Collectors.toList());

        assignmentDetailList.forEach(a -> {
            Counter counter = goodAssignments.get(getBucketNumber(a.getRating()));
            counter.add();
        });

        return ResultSummary.createSummaryWithSolution(rating, assignmentDetailList, mapMapToNumbers(goodAssignments));
    }

    private Map<Assignment, Counter> duplicationCount(List<Assignment> assignmentList) {
        Map<Assignment, Counter> duplicates = new HashMap<>();
        assignmentList.forEach(assignment -> {
            Counter counter = duplicates.get(assignment);
            if (counter == null) {
                counter = new Counter();
                duplicates.put(assignment, counter);
            }
            counter.add();
        });
        return duplicates;
    }

    @Override
    public List<DistributionHeader> getHeaderList() {
        List<DistributionHeader> headerList = new ArrayList<>();
        IntStream.range(0, NUMBER_OF_BUCKETS).forEach(i -> {
            MessageWithParams message = new MessageWithParams(HEADER_BUCKET, getBucketParams(i));
            headerList.add(new DistributionHeader(message, String.valueOf(i), true));
        });
        return headerList;
    }

    private int getBucketNumber(int rating) {
        int ratio;
        if (rating == preferenceResolver.getMaximumRating()) {
            ratio = 99;
        } else {
            ratio = (int) (((double) rating / preferenceResolver.getMaximumRating()) * 100);
        }
        return ratio / PERCENT_PER_BUCKET;
    }

    private Map<String, Integer> mapMapToNumbers(Map<Integer, Counter> originalMap) {
        Map<String, Integer> resultMap = new TreeMap<>();
        originalMap.forEach((key, value) -> resultMap.put(key.toString(), value.getNumber()));
        return resultMap;
    }

    private Object[] getBucketParams(int bucket) {
        return new Object[]{bucket * PERCENT_PER_BUCKET, (bucket + 1) * PERCENT_PER_BUCKET};
    }
}
