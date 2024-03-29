package cz.rolling.moirai.assignment.enhancer;

import cz.rolling.moirai.assignment.helper.Counter;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.model.common.AssignmentDetail;
import cz.rolling.moirai.model.common.AssignmentDetailCharacters;
import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.MessageWithParams;
import cz.rolling.moirai.model.common.UnwantedAssignmentType;
import cz.rolling.moirai.model.common.result.DirectSolution;
import cz.rolling.moirai.model.common.result.MetaSolution;
import cz.rolling.moirai.model.common.result.NoSolution;
import cz.rolling.moirai.model.common.result.ResultSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CharacterSolutionEnhancer implements SolutionEnhancer {

    private final static String WANTED_MESSAGE = "execution-results.header.preferredAs";

    private final int numberOfPreferences;
    private final CharacterPreferenceResolver preferenceResolver;

    public CharacterSolutionEnhancer(int numberOfPreferences, CharacterPreferenceResolver preferenceResolver) {
        this.numberOfPreferences = numberOfPreferences;
        this.preferenceResolver = preferenceResolver;
    }

    @Override
    public ResultSummary enhance(DirectSolution solution) {
        Map<Integer, Counter> goodAssignments = new HashMap<>();
        IntStream.rangeClosed(1, numberOfPreferences).forEach(i ->
                goodAssignments.put(i, new Counter())
        );
        Map<UnwantedAssignmentType, Counter> badAssignments = new HashMap<>();
        for (UnwantedAssignmentType type : preferenceResolver.getPossibleUnwantedTypes()) {
            badAssignments.put(type, new Counter());
        }

        solution.getAssignmentList().forEach(a -> {
            Integer rank = preferenceResolver.getRating(a);
            if (rank != null) {
                Counter counter = goodAssignments.get(rank);
                counter.add();
            } else {
                UnwantedAssignmentType type = preferenceResolver.getUnwantedAssignmentType(a);
                Counter counter = badAssignments.get(type);
                counter.add();
            }
        });

        List<AssignmentDetail> assignmentList = solution.getAssignmentList().stream()
                .map(assignment -> new AssignmentDetailCharacters(
                        assignment,
                        preferenceResolver.calcRatingOfAssignment(assignment),
                        preferenceResolver.evaluateGenderAssignment(assignment),
                        preferenceResolver.calcCharacterAssignmentType(assignment)
                )).collect(Collectors.toList());

        Map<String, Integer> distributionMap = mapMapToNumbers(goodAssignments, badAssignments);
        return ResultSummary.createSummaryWithSolution(solution.getRating(), assignmentList, distributionMap);
    }

    @Override
    public ResultSummary enhance(MetaSolution solution) {
        throw new RuntimeException("The character solution doesn't support meta solutions.");
    }

    @Override
    public ResultSummary enhance(NoSolution solution) {
        return ResultSummary.createSummaryWithoutSolution(solution.getReasonCode());
    }

    @Override
    public List<DistributionHeader> getHeaderList() {
        List<DistributionHeader> headerList = new ArrayList<>();
        IntStream.rangeClosed(1, numberOfPreferences).forEach(i -> {
            MessageWithParams message = new MessageWithParams(WANTED_MESSAGE, new Object[]{i});
            headerList.add(new DistributionHeader(message, String.valueOf(i), true));
        });
        for (UnwantedAssignmentType type : preferenceResolver.getPossibleUnwantedTypes()) {
            MessageWithParams message = new MessageWithParams(type.getKey(), new Object[0]);
            headerList.add(new DistributionHeader(message, type.toString(), false));
        }
        return headerList;
    }

    private Map<String, Integer> mapMapToNumbers(Map<Integer, Counter> goodAssignments,
                                                 Map<UnwantedAssignmentType, Counter> badAssignments) {
        Map<String, Integer> resultMap = new HashMap<>();
        goodAssignments.forEach((key, value) -> resultMap.put(String.valueOf(key), value.getNumber()));
        badAssignments.forEach((key, value) -> resultMap.put(key.toString(), value.getNumber()));

        return resultMap;
    }
}
