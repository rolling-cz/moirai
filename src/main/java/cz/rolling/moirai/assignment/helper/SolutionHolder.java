package cz.rolling.moirai.assignment.helper;

import cz.rolling.moirai.assignment.enhancer.SolutionEnhancer;
import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.result.NoSolution;
import cz.rolling.moirai.model.common.result.ResultSummary;
import cz.rolling.moirai.model.common.result.Solution;
import org.apache.solr.util.BoundedTreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SolutionHolder {

    private static final NoSolution EMPTY_SOLUTION = new NoSolution("no-solution.no-input");
    private final BoundedTreeSet<Solution> solutionSet;
    private final SolutionEnhancer solutionEnhancer;
    private final Logger logger = LoggerFactory.getLogger(SolutionHolder.class);
    private int triedSolutionCounter = 0;

    public SolutionHolder(SolutionEnhancer solutionEnhancer,
                          int numberOfSolutions) {
        this.solutionEnhancer = solutionEnhancer;
        solutionSet = new BoundedTreeSet<>(numberOfSolutions, Comparator.reverseOrder());
    }

    public Solution getBestSolution() {
        if (!solutionSet.isEmpty()) {
            return solutionSet.first();
        } else {
            return EMPTY_SOLUTION;
        }
    }

    public Solution getWorstSolution() {
        if (!solutionSet.isEmpty()) {
            return solutionSet.last();
        } else {
            return EMPTY_SOLUTION;
        }
    }

    public void saveSolution(Solution solution) {
        solutionSet.add(solution);
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

    public List<ResultSummary> getSolutions() {
        return solutionSet.stream()
                .map(solution -> solution.enhanceBy(solutionEnhancer))
                .collect(Collectors.toList());
    }

    public List<DistributionHeader> getDistributionHeaderList() {
        return solutionEnhancer.getHeaderList();
    }
}
