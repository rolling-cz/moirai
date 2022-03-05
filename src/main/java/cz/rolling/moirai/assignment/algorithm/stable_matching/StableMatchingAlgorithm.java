package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.enhancer.ContentSolutionEnhancer;
import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.exception.NoSolutionException;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.result.DirectSolution;
import cz.rolling.moirai.model.common.result.MetaSolution;
import cz.rolling.moirai.model.common.result.NoSolution;
import cz.rolling.moirai.model.common.result.Solution;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

public class StableMatchingAlgorithm implements Algorithm {

    private static final IdWithRatingComparator RATING_COMPARATOR = new IdWithRatingComparator();
    private final ContentPreferenceResolver preferenceResolver;
    private final StableMatchingProcessor processor;
    private final int numberOfCharacters;
    private final int multiSelect;

    public StableMatchingAlgorithm(
            ContentPreferenceResolver preferenceResolver,
            StableMatchingProcessor processor,
            int numberOfCharacters,
            int multiSelect
    ) {
        this.preferenceResolver = preferenceResolver;
        this.processor = processor;
        this.numberOfCharacters = numberOfCharacters;
        this.multiSelect = multiSelect;
    }

    @Override
    public SolutionHolder findBestAssignment() {
        SolutionHolder solutionHolder = new SolutionHolder(new ContentSolutionEnhancer(preferenceResolver), 1);

        Solution completeSolution;
        try {
            if (multiSelect > 1) {
                completeSolution = new MetaSolution();
                Set<Assignment> forbiddenAssignments = new HashSet<>();
                for (int i = 0; i < multiSelect; i++) {
                    DirectSolution solution = calculateDirectSolution(forbiddenAssignments);
                    ((MetaSolution) completeSolution).addSolution(solution);
                    forbiddenAssignments.addAll(solution.getAssignmentList());
                }
            } else {
                completeSolution = calculateDirectSolution(Collections.emptySet());
            }
        } catch (NoSolutionException e) {
            completeSolution = new NoSolution(e.getMessage());
        }

        solutionHolder.saveSolution(completeSolution);
        return solutionHolder;
    }

    private DirectSolution calculateDirectSolution(Set<Assignment> forbiddenAssignments) {
        processor.init(
                transformPreferences(Assignment::getUserId, Assignment::getCharId),
                transformPreferences(Assignment::getCharId, Assignment::getUserId),
                transformForbiddenAssignments(forbiddenAssignments)
        );

        List<Assignment> assignments = transformCouplesToAssignments(processor.process());
        return new DirectSolution(preferenceResolver.calculateRating(assignments), assignments);
    }

    private boolean[][] transformForbiddenAssignments(Set<Assignment> forbiddenAssignments) {
        boolean[][] forbidden = new boolean[numberOfCharacters][numberOfCharacters];
        for (int d1 = 0; d1 < numberOfCharacters; d1++) {
            for (int d2 = 0; d2 < numberOfCharacters; d2++) {
                forbidden[d1][d2] = false;
            }
        }

        forbiddenAssignments.forEach(assignment ->
            forbidden[assignment.getUserId()][assignment.getCharId()] = true
        );

        return forbidden;
    }

    protected int[][] transformPreferences(Function<Assignment, Integer> getKeyFn,
                                           Function<Assignment, Integer> getValueFn) {
        List<List<IdWithRating>> preference2dList = init2dList(numberOfCharacters);
        preferenceResolver.getPreferenceMap().forEach(((assignment, rating) ->
                preference2dList.get(getKeyFn.apply(assignment)).add(
                        new IdWithRating(getValueFn.apply(assignment), rating)
                )
        ));
        preference2dList.forEach(list -> list.sort(RATING_COMPARATOR));

        int[][] preferences = new int[numberOfCharacters][numberOfCharacters];
        for (int d1 = 0; d1 < numberOfCharacters; d1++) {
            for (int d2 = 0; d2 < numberOfCharacters; d2++) {
                preferences[d1][d2] = preference2dList.get(d1).get(d2).getId();
            }
        }
        return preferences;
    }

    protected List<List<IdWithRating>> init2dList(int numberOfElements) {
        List<List<IdWithRating>> element2dList = new ArrayList<>();
        IntStream.range(0, numberOfElements).forEach(i -> element2dList.add(new ArrayList<>()));
        return element2dList;
    }

    protected List<Assignment> transformCouplesToAssignments(Map<Integer, Integer> couples) {
        List<Assignment> assignmentList = new ArrayList<>();
        couples.forEach((charId, userId) -> assignmentList.add(new Assignment(userId != null ? userId : -1, charId)));
        return assignmentList;
    }

    @Getter
    @AllArgsConstructor
    protected static class IdWithRating {
        private final int id;
        private final int rating;
    }

    private static class IdWithRatingComparator implements Comparator<IdWithRating> {
        @Override
        public int compare(IdWithRating o1, IdWithRating o2) {
            return Integer.compare(o2.getRating(), o1.getRating());
        }
    }
}
