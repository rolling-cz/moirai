package cz.rolling.moirai.assignment.helper;

import cz.rolling.moirai.assignment.algorithm.content_dfs.AssignmentTask;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.content.ContentConfiguration;
import org.apache.solr.util.BoundedTreeSet;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SolutionHolder {

    private final PreferencesHolder preferencesHolder;
    private final BoundedTreeSet<Solution> solutionSet;
    private int triedSolutionCounter = 0;

    public SolutionHolder(PreferencesHolder preferencesHolder, ContentConfiguration configuration) {
        solutionSet = new BoundedTreeSet<>(configuration.getNumberOfBestSolutions(), Comparator.reverseOrder());
        this.preferencesHolder = preferencesHolder;
    }

    public Iterator<Solution> getSolutions() {
        return solutionSet.iterator();
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
            System.out.println("Solution tried: " + triedSolutionCounter + ", best rank: " + getBestSolution().getRank());
        }
    }

    public int getTriedSolutionCounter() {
        return triedSolutionCounter;
    }

    public void saveFailedSolution(AssignmentTask task) {
        triedSolutionCounter++;
        if (triedSolutionCounter % 10000 == 0) {
            System.out.println("Solution tried: " + triedSolutionCounter + ", best rank: " + getBestSolution().getRank());
        }
    }


}
