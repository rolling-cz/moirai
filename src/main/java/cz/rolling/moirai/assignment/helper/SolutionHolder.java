package cz.rolling.moirai.assignment.helper;

import cz.rolling.moirai.assignment.enhancer.SolutionEnhancer;
import cz.rolling.moirai.model.common.DistributionHeader;
import cz.rolling.moirai.model.common.result.DirectSolution;
import cz.rolling.moirai.model.common.result.Solution;
import cz.rolling.moirai.model.common.result.VerboseSolution;
import org.apache.solr.util.BoundedTreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SolutionHolder {

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
            return DirectSolution.EMPTY;
        }
    }

    public Solution getWorstSolution() {
        if (!solutionSet.isEmpty()) {
            return solutionSet.last();
        } else {
            return DirectSolution.EMPTY;
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
