package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.exception.NoSolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Variant with skipping forbidden.
 */
public class StableMatchingProcessorVar1 implements StableMatchingProcessor {
    private final Logger logger = LoggerFactory.getLogger(StableMatchingProcessorVar1.class);

    private int[][] men;
    private int[][] women;
    private boolean[][] forbidden;
    private int[] next;

    @Override
    public void init(int[][] men, int[][] women, boolean[][] forbidden) {
        this.men = men;
        this.women = women;
        this.forbidden = forbidden;

        // prepare index of next woman to ask
        next = new int[men.length];
        for (int i = 0; i < men.length; i++) {
            next[i] = 0;
        }
    }

    @Override
    public Map<Integer, Integer> process() throws NoSolutionException {
        //couples map will contain all the matches, with women as key and her match (men) as value
        Map<Integer, Integer> couples = new HashMap<>();

        //add all the women to couples map with their matches as NULL (initially)
        for (int i = 0; i < women.length; i++) {
            couples.put(i, null);
        }

        //create a queue of all bachelors
        Deque<Integer> bachelors = new LinkedList<>();
        for (int i = 0; i < men.length; i++) {
            bachelors.add(i);
        }

        processBachelors(couples, bachelors);
        if (!isMatchingComplete(couples)) {
            throw new NoSolutionException("no-solution.no-stable-matching");
        }

        if (logger.isTraceEnabled()) {
            System.out.println("Men");
            printMulti(men);
            System.out.println(transpose(couples));
            System.out.println("Women");
            printMulti(women);
            System.out.println(couples);
            System.out.println("Next");
            printSingle(next);
        }

        return couples;
    }

    private boolean isMatchingComplete(Map<Integer, Integer> couples) {
        return !couples.containsValue(null);
    }


    private void processBachelors(Map<Integer, Integer> couples, Deque<Integer> bachelors) {
        //do till all the bachelors are nor engaged
        while (!bachelors.isEmpty()) {

            int currentBachelor = bachelors.pollFirst();
            logger.trace("\nMan " + currentBachelor + " is looking for a woman now-");

            // check for all the women preferences of current bachelor who he not yet asked
            while(next[currentBachelor] < women.length) {
                int currentWoman = men[currentBachelor][next[currentBachelor]];
                next[currentBachelor]++;

                if (forbidden[currentBachelor][currentWoman]) {
                    continue;
                }

                //check if current woman is available for current bachelor
                if (couples.get(currentWoman) == null) {
                    //this woman is available for this man, make the match
                    couples.put(currentWoman, currentBachelor);
                    logger.trace("Women " + currentWoman + " has ACCEPTED the man: " + currentBachelor);
                    break;
                } else {
                    //current woman had already accepted the proposal from some other man
                    //check if women is interested accepting current bachelor
                    // and dumping the man which she had accepted earlier
                    int alreadyAcceptedMan = couples.get(currentWoman);
                    if (willChangePartner(currentBachelor, alreadyAcceptedMan, currentWoman, women)) {

                        //current women will accept
                        couples.put(currentWoman, currentBachelor);
                        // add the dumped man in bachelor list
                        bachelors.addLast(alreadyAcceptedMan);
                        bachelors.remove(currentBachelor);
                        logger.trace("Women " + currentWoman + " has DUMPED the man: " + alreadyAcceptedMan);
                        logger.trace("Women " + currentWoman + " has ACCEPTED the man: " + currentBachelor);
                        break;
                    } else {
                        logger.trace("Women " + currentWoman + " has better man: " + alreadyAcceptedMan);
                    }
                }
            }
        }
    }

    private void printSingle(int[] array) {
        for (int i : array) {
            System.out.print(i + ", ");
        }
        System.out.println();
    }

    private Map<Integer, Integer> transpose(Map<Integer, Integer> couples) {
        Map<Integer, Integer> transposed = new HashMap<>();
        couples.forEach((key, value) -> transposed.put(value, key));
        return transposed;
    }

    private void printMulti(int[][] array) {
        for (int d1 = 0; d1 < array.length; d1++) {
            System.out.print(d1 + ": ");
            for (int d2 = 0; d2 < array[d1].length; d2++) {
                System.out.print(array[d1][d2] + ", ");
            }
            System.out.println();
        }
    }

    boolean willChangePartner(int currentBachelor, int alreadyAcceptedMan, int currentWomen, int[][] women) {

        int pref_currentBachelor = -1;
        int pref_alreadyAcceptedMan = -1;

        //get the preferences of both the men
        for (int i = 0; i < women[currentWomen].length; i++) {

            if (women[currentWomen][i] == currentBachelor) {
                pref_currentBachelor = i;
            }

            if (women[currentWomen][i] != alreadyAcceptedMan) {
                continue;
            }
            pref_alreadyAcceptedMan = i;
        }

        //women will accept the current bachelor only if he has higher preference
        //than the man she had accepted earlier
        return pref_currentBachelor < pref_alreadyAcceptedMan;
    }
}

