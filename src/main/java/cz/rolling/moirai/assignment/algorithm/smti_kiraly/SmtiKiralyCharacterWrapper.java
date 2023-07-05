package cz.rolling.moirai.assignment.algorithm.smti_kiraly;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.enhancer.CharacterSolutionEnhancer;
import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.exception.NoSolutionException;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.result.DirectSolution;
import cz.rolling.moirai.model.common.result.NoSolution;
import cz.rolling.moirai.model.common.result.Solution;
import cz.rolling.moirai.model.content.ContentConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Modified SMTI Kirali algorithm to try to find matching with maximum size.
 */
public class SmtiKiralyCharacterWrapper implements Algorithm {

    private final CharacterPreferenceResolver preferenceResolver;
    private final ContentConfiguration configuration;

    private static final Comparator<AssignmentWithRank> RANK_COMPARATOR = Comparator.comparingInt(AssignmentWithRank::getRank);

    public SmtiKiralyCharacterWrapper(CharacterPreferenceResolver preferenceResolver, ContentConfiguration configuration) {
        this.preferenceResolver = preferenceResolver;
        this.configuration = configuration;
    }

    @Override
    public SolutionHolder findBestAssignment() {
        CharacterSolutionEnhancer enhancer = new CharacterSolutionEnhancer(configuration.getPreferencesPerUser(), preferenceResolver);
        SolutionHolder solutionHolder = new SolutionHolder(enhancer, configuration.getNumberOfBestSolutions());

        Solution completeSolution;
        try {
            completeSolution = calculateDirectSolution();
        } catch (NoSolutionException e) {
            completeSolution = new NoSolution(e.getMessage());
        }

        solutionHolder.saveSolution(completeSolution);
        return solutionHolder;
    }

    private DirectSolution calculateDirectSolution() {
        Map<Integer, List<Integer>> userMapPreferences = collectUserPreferences();
        Map<Integer, List<Integer>> charMapPreferences = collectCharPreferences();

        // init SMI characters as men and players as women -> to allow fewer players than characters
        SmtiKiralyProcessor processor = new SmtiKiralyProcessor(
                mapToList(userMapPreferences),
                mapToList(charMapPreferences)
        );

        List<Assignment> assignments = transformCouplesToAssignments(processor.process());
        return new DirectSolution(preferenceResolver.calculateRating(assignments), assignments);
    }

    protected Map<Integer, List<Integer>> collectUserPreferences() {
        Map<Integer, List<AssignmentWithRank>> prefs = new HashMap<>();
        preferenceResolver.getUserList().forEach(user -> {
            List<AssignmentWithRank> userPrefs = new ArrayList<>(user.getPreferences());
            userPrefs.sort(RANK_COMPARATOR);
            prefs.put(user.getId(), userPrefs);
        });

        return prefs.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().map(a -> a.getAssignment().getCharId()).collect(Collectors.toList())
        ));
    }

    protected Map<Integer, List<Integer>> collectCharPreferences() {
        Map<Integer, List<AssignmentWithRank>> prefs = new HashMap<>();
        IntStream.range(0, configuration.getCharacterCount()).forEach(charId ->
                prefs.put(charId, new ArrayList<>())
        );
        preferenceResolver.getUserList().forEach(user ->
            user.getPreferences().forEach(assignment ->
                    prefs.get(assignment.getAssignment().getCharId()).add(assignment)
            )
        );

        prefs.values().forEach(p -> p.sort(RANK_COMPARATOR));
        return prefs.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().map(a -> a.getAssignment().getUserId()).collect(Collectors.toList())
        ));
    }

    private List<List<Integer>> mapToList(Map<Integer, List<Integer>> originMap) {
        List<List<Integer>> list = new ArrayList<>();

        IntStream.range(0, originMap.size())
                .forEach(charId -> list.add(originMap.get(charId)));

        return list;
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
}
