package cz.rolling.moirai.assignment.algorithm.smti_kiraly;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.enhancer.ContentSolutionEnhancer;
import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.assignment.preference.PreferenceUtils;
import cz.rolling.moirai.exception.NoSolutionException;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.common.result.DirectSolution;
import cz.rolling.moirai.model.common.result.MetaSolution;
import cz.rolling.moirai.model.common.result.NoSolution;
import cz.rolling.moirai.model.common.result.Solution;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Modified SMTI Kirali algorithm to try to find matching with maximum size.
 */
public class SmtiKiralyContentWrapper implements Algorithm {

    private static final IdWithRatingComparator RATING_COMPARATOR = new IdWithRatingComparator();
    private final ContentPreferenceResolver preferenceResolver;
    private final int numberOfUsers;
    private final int numberOfCharacters;
    private final int multiSelect;
    private final List<Assignment> blockedAssignmentList;

    private final boolean isWrongGenderForbidden = true;

    public SmtiKiralyContentWrapper(
            ContentPreferenceResolver preferenceResolver,
            int numberOfUsers,
            int numberOfCharacters,
            int multiSelect,
            List<Assignment> blockedAssignmentList
    ) {
        this.preferenceResolver = preferenceResolver;
        this.numberOfUsers = numberOfUsers;
        this.numberOfCharacters = numberOfCharacters;
        this.multiSelect = multiSelect;
        this.blockedAssignmentList = blockedAssignmentList;
    }

    @Override
    public SolutionHolder findBestAssignment() {
        SolutionHolder solutionHolder = new SolutionHolder(new ContentSolutionEnhancer(preferenceResolver), 1);

        Solution completeSolution;
        try {
            if (multiSelect > 1) {
                completeSolution = new MetaSolution();
                Set<Assignment> forbiddenAssignments = new HashSet<>(blockedAssignmentList);
                for (int i = 0; i < multiSelect; i++) {
                    DirectSolution solution = calculateDirectSolution(forbiddenAssignments);
                    ((MetaSolution) completeSolution).addSolution(solution);
                    forbiddenAssignments.addAll(solution.getAssignmentList());
                }
            } else {
                completeSolution = calculateDirectSolution(new HashSet<>(blockedAssignmentList));
            }
        } catch (NoSolutionException e) {
            completeSolution = new NoSolution(e.getMessage());
        }

        solutionHolder.saveSolution(completeSolution);
        return solutionHolder;
    }

    private DirectSolution calculateDirectSolution(Set<Assignment> forbiddenAssignmentsParam) {
        SmtiKiralyProcessor processor = new SmtiKiralyProcessor(
                transformPreferences(Assignment::getUserId, Assignment::getCharId, numberOfUsers, forbiddenAssignmentsParam),
                transformPreferences(Assignment::getCharId, Assignment::getUserId, numberOfCharacters, forbiddenAssignmentsParam)
        );

        List<Assignment> assignments = transformCouplesToAssignments(processor.process());
        return new DirectSolution(preferenceResolver.calculateRating(assignments), assignments);
    }

    protected List<List<Integer>> transformPreferences(Function<Assignment, Integer> getKeyFn,
                                           Function<Assignment, Integer> getValueFn,
                                           int numberOfElements,
                                           Set<Assignment> forbiddenAssignments) {
        List<List<IdWithRating>> preference2dList = init2dList(numberOfElements);
        preferenceResolver.getPreferenceMap().forEach(((assignment, rating) -> {
            if (isWrongGenderForbidden) {
                User user = preferenceResolver.getUserList().get(assignment.getUserId());
                Character character = preferenceResolver.getCharacterList().get(assignment.getCharId());
                if (!PreferenceUtils.isCorrectGender(user, character)) {
                    return;
                }
            }

            if (forbiddenAssignments.contains(assignment)) {
                return;
            }

            IdWithRating idWithRating = new IdWithRating(getValueFn.apply(assignment), rating);
            preference2dList.get(getKeyFn.apply(assignment)).add(idWithRating);
        }));
        preference2dList.forEach(list -> list.sort(RATING_COMPARATOR));

        return preference2dList.stream().map(
                list -> list.stream()
                        .map(IdWithRating::getId)
                        .collect(Collectors.toList())
        ).collect(Collectors.toList());
    }

    protected List<List<IdWithRating>> init2dList(int numberOfElements) {
        List<List<IdWithRating>> element2dList = new ArrayList<>();
        IntStream.range(0, numberOfElements).forEach(i -> element2dList.add(new ArrayList<>()));
        return element2dList;
    }

    protected List<Assignment> transformCouplesToAssignments(Map<Integer, Integer> couples) {
        List<Assignment> assignmentList = new ArrayList<>();
        couples.forEach((charId, userId) -> {
            if (userId != null) {
                assignmentList.add(new Assignment(userId, charId));
            }
        });
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
