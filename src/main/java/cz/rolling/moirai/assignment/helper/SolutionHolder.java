package cz.rolling.moirai.assignment.helper;

import cz.rolling.moirai.assignment.algorithm.content_dfs.AssignmentTask;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.UnwantedAssignmentType;
import cz.rolling.moirai.model.common.VerboseSolution;
import cz.rolling.moirai.model.content.ContentConfiguration;
import org.apache.solr.util.BoundedTreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SolutionHolder {

    private final PreferencesHolder preferencesHolder;
    private final ContentConfiguration configuration;
    private final BoundedTreeSet<Solution> solutionSet;
    private int triedSolutionCounter = 0;

    private final Logger logger = LoggerFactory.getLogger(SolutionHolder.class);

    public SolutionHolder(PreferencesHolder preferencesHolder, ContentConfiguration configuration) {
        solutionSet = new BoundedTreeSet<>(configuration.getNumberOfBestSolutions(), Comparator.reverseOrder());
        this.preferencesHolder = preferencesHolder;
        this.configuration = configuration;
    }

    public Solution getBestSolution() {
        if (!solutionSet.isEmpty()) {
            return solutionSet.first();
        } else {
            return Solution.EMPTY;
        }
    }

    public Solution getWorstSolution() {
        if (!solutionSet.isEmpty()) {
            return solutionSet.last();
        } else {
            return Solution.EMPTY;
        }
    }

    public void saveSolution(AssignmentTask task) {
        List<Assignment> assignments = task.getAssignmentList();
        Integer rank = preferencesHolder.rankAssignmentList(assignments);
        solutionSet.add(new Solution(rank, assignments));

        triedSolutionCounter++;
        if (triedSolutionCounter % 10000 == 0) {
            logger.debug("Solution tried: " + triedSolutionCounter + ", best rank: " + getBestSolution().getRating());
        }
    }

    public int getTriedSolutionCounter() {
        return triedSolutionCounter;
    }

    public void saveFailedSolution(AssignmentTask task) {
        triedSolutionCounter++;
        if (triedSolutionCounter % 10000 == 0) {
            logger.debug("Solution tried: " + triedSolutionCounter + ", best rank: " + getBestSolution().getRating());
        }
    }

    public List<VerboseSolution> getSolutions() {
        Iterator<Solution> iterator = solutionSet.iterator();
        List<VerboseSolution> solutionList = new ArrayList<>();
        while (iterator.hasNext()) {
            solutionList.add(mapDistribution(iterator.next()));
        }
        return solutionList;
    }

    private VerboseSolution mapDistribution(Solution solution) {
        Map<String, Counter> goodAssignments = new HashMap<>();
        IntStream.rangeClosed(1, configuration.getPreferencesPerUser()).forEach(i ->
                goodAssignments.put(String.valueOf(i), new Counter())
        );
        Map<String, Counter> badAssignments = new HashMap<>();
        for (UnwantedAssignmentType type : UnwantedAssignmentType.values()) {
            badAssignments.put(type.toString(), new Counter());
        }

        solution.getAssignmentList().forEach(a -> {
            Integer rank = preferencesHolder.getAssignmentRank(a);
            if (rank != null) {
                Counter counter = goodAssignments.get(String.valueOf(rank));
                counter.add();
            } else {
                UnwantedAssignmentType type = preferencesHolder.getUnwantedAssignmentType(a);
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
