package cz.rolling.moirai.assignment.distribution;

import cz.rolling.moirai.assignment.helper.Counter;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.VerboseSolution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ContentDistributionEnhancer implements DistributionEnhancer {

    private static final int NUMBER_OF_BUCKETS = 10;
    private static final int PERCENT_PER_BUCKET = 100 / NUMBER_OF_BUCKETS;
    private final ContentPreferenceResolver preferenceResolver;

    public ContentDistributionEnhancer(ContentPreferenceResolver preferenceResolver) {
        this.preferenceResolver = preferenceResolver;
    }

    @Override
    public VerboseSolution addDistribution(Solution solution) {
        Map<Integer, Counter> goodAssignments = new HashMap<>();
        IntStream.range(0, NUMBER_OF_BUCKETS).forEach(i ->
                goodAssignments.put(i, new Counter())
        );

        solution.getAssignmentList().forEach(a -> {
            Integer rank = preferenceResolver.getRating(a);
            Counter counter = goodAssignments.get(getBucketNumber(rank));
            counter.add();
        });

        return new VerboseSolution(solution, mapMapToNumbers(goodAssignments), Collections.emptyMap());
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
        return originalMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> (e.getKey() * PERCENT_PER_BUCKET) + "% - " + ((e.getKey() + 1) * PERCENT_PER_BUCKET) + "%",
                        e -> e.getValue().getNumber()
                ));
    }
}
