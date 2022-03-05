package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.exception.NoSolutionException;

import java.util.Map;

public interface StableMatchingProcessor {
    void init(int[][] men, int[][] women, boolean[][] forbidden);

    Map<Integer, Integer> process() throws NoSolutionException;
}
