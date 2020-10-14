package cz.rolling.moirai.assignment.distribution;

import cz.rolling.moirai.assignment.helper.Counter;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.UnwantedAssignmentType;
import cz.rolling.moirai.model.common.VerboseSolution;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CharacterDistributionEnhancer implements DistributionEnhancer{

    private final int numberOfPreferences;
    private final CharacterPreferenceResolver preferenceResolver;

    public CharacterDistributionEnhancer(int numberOfPreferences, CharacterPreferenceResolver preferenceResolver) {
        this.numberOfPreferences = numberOfPreferences;
        this.preferenceResolver = preferenceResolver;
    }

    @Override
    public VerboseSolution addDistribution(Solution solution) {
        Map<String, Counter> goodAssignments = new HashMap<>();
        IntStream.rangeClosed(1, numberOfPreferences).forEach(i ->
                goodAssignments.put(String.valueOf(i), new Counter())
        );
        Map<String, Counter> badAssignments = new HashMap<>();
        for (UnwantedAssignmentType type : UnwantedAssignmentType.values()) {
            badAssignments.put(type.toString(), new Counter());
        }

        solution.getAssignmentList().forEach(a -> {
            Integer rank = preferenceResolver.getAssignmentType(a);
            if (rank != null) {
                Counter counter = goodAssignments.get(String.valueOf(rank));
                counter.add();
            } else {
                UnwantedAssignmentType type = preferenceResolver.getUnwantedAssignmentType(a);
                Counter counter = badAssignments.get(type.toString());
                counter.add();
            }
        });

        return new VerboseSolution(solution, mapMapToNumbers(goodAssignments), mapMapToNumbers(badAssignments));
    }

    private Map<String, Integer> mapMapToNumbers(Map<String, Counter> originalMap) {
        return originalMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getNumber()));
    }
}
