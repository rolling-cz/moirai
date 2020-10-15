package cz.rolling.moirai.assignment.enhancer;

import cz.rolling.moirai.assignment.helper.Counter;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.MessageWithParams;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.VerboseSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
    public VerboseSolution enhance(Solution solution) {
        Map<Integer, Counter> goodAssignments = new HashMap<>();
        IntStream.range(0, NUMBER_OF_BUCKETS).forEach(i ->
                goodAssignments.put(i, new Counter())
        );

        List<AssignmentWithRank> assignmentList = new ArrayList<>();
        solution.getAssignmentList().forEach(a -> {
            Integer rating = preferenceResolver.getRating(a);
            assignmentList.add(new AssignmentWithRank(a, rating));
            Counter counter = goodAssignments.get(getBucketNumber(rating));
            counter.add();
        });

        return new VerboseSolution(solution.getRating(), assignmentList, mapMapToNumbers(goodAssignments));
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
