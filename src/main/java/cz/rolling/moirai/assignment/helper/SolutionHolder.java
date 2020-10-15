package cz.rolling.moirai.assignment.helper;

import cz.rolling.moirai.assignment.enhancer.SolutionEnhancer;
import cz.rolling.moirai.assignment.preference.PreferenceResolver;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.VerboseSolution;
import org.apache.solr.util.BoundedTreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SolutionHolder {

    private final PreferenceResolver preferenceResolver;
    private final BoundedTreeSet<Solution> solutionSet;
    private final SolutionEnhancer solutionEnhancer;
    private final Logger logger = LoggerFactory.getLogger(SolutionHolder.class);
    private int triedSolutionCounter = 0;

    public SolutionHolder(PreferenceResolver preferenceResolver,
                          SolutionEnhancer solutionEnhancer,
                          int numberOfSolutions) {
        this.solutionEnhancer = solutionEnhancer;
        solutionSet = new BoundedTreeSet<>(numberOfSolutions, Comparator.reverseOrder());
        this.preferenceResolver = preferenceResolver;
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

    public void saveSolution(List<Assignment> assignmentList) {
        Integer rank = preferenceResolver.calculateRating(assignmentList);
        solutionSet.add(new Solution(rank, assignmentList));

        triedSolutionCounter++;
        if (triedSolutionCounter % 10000 == 0) {
            logger.debug("Solution tried: " + triedSolutionCounter + ", best rank: " + getBestSolution().getRating());
        }
    }

    public int getTriedSolutionCounter() {
        return triedSolutionCounter;
    }

    public void saveFailedSolution() {
        triedSolutionCounter++;
        if (triedSolutionCounter % 10000 == 0) {
            logger.debug("Solution tried: " + triedSolutionCounter + ", best rank: " + getBestSolution().getRating());
        }
    }

    public List<VerboseSolution> getSolutions() {
        Iterator<Solution> iterator = solutionSet.iterator();
        List<VerboseSolution> solutionList = new ArrayList<>();
        while (iterator.hasNext()) {
            solutionList.add(solutionEnhancer.enhance(iterator.next()));
        }
        return solutionList;
    }

    public List<DistributionHeader> getDistributionHeaderList() {
        return solutionEnhancer.getHeaderList();
    }
}
