package cz.rolling.moirai.assignment.algorithm.smti_kiraly;

import cz.rolling.moirai.exception.NoSolutionException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Modified SMTI Kirali algorithm to try to find matching with maximum size.
 */
public class SmtiKiralyProcessor {
    private final Logger logger = LoggerFactory.getLogger(SmtiKiralyProcessor.class);
    private final static double EPSILON_SCORE = 1.5d;

    private final List<List<Integer>> men;
    private final List<List<Integer>> women;
    private final List<Counter> next;
    private final Map<Integer, Double> priorityBonusMap = new HashMap<>();

    private final Set<Integer> unassignedMen = new HashSet<>();

    public SmtiKiralyProcessor(List<List<Integer>> men, List<List<Integer>> women) {
        this.men = men;
        this.women = women;

        // prepare index of next woman to ask
        next = new ArrayList<>();
        for (int i = 0; i < men.size(); i++) {
            next.add(new Counter());
        }

        // set score bonus
        for (int i = 0; i < men.size(); i++) {
            priorityBonusMap.put(i, 0D);
        }
    }

    public Map<Integer, Integer> process() throws NoSolutionException {
        // couples map will contain all the matches, with women as key and her match (men) as value
        Map<Integer, Integer> couples = new HashMap<>();

        //add all the women to couples map with their matches as NULL (initially)
        for (int i = 0; i < women.size(); i++) {
            couples.put(i, null);
        }

        //create a queue of all men
        Deque<Integer> manToProcess = new LinkedList<>();
        for (int i = 0; i < men.size(); i++) {
            manToProcess.add(i);
        }

        // process till there is bachelors to process
        while (!manToProcess.isEmpty()) {
            processBachelors(couples, manToProcess);

            unassignedMen.stream()
                    .filter(m -> priorityBonusMap.get(m) == 0)
                    .forEach(m -> {
                        manToProcess.add(m);
                        priorityBonusMap.put(m, EPSILON_SCORE);
                        next.get(m).reset();
                    }
            );
            unassignedMen.clear();
        }

        if (!isMatchingComplete(couples)) {
            throw new NoSolutionException("no-solution.no-stable-matching");
        }

        return couples;
    }

    private boolean isMatchingComplete(Map<Integer, Integer> couples) {
        Set<Integer> assignedMen = couples.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Integer> allMen = IntStream.range(0, men.size()).boxed().collect(Collectors.toSet());
        return allMen.equals(assignedMen);
    }


    private void processBachelors(Map<Integer, Integer> couples, Deque<Integer> bachelors) {
        //do till all the bachelors are nor engaged
        while (!bachelors.isEmpty()) {

            int currentBachelor = bachelors.pollFirst();
            logger.trace("\nMan " + currentBachelor + " is looking for a woman now-");

            // check for all the women preferences of current bachelor who he not yet asked
            boolean stillSingle = true;
            while(next.get(currentBachelor).getIndex() < men.get(currentBachelor).size()) {
                int currentWoman = men.get(currentBachelor).get(next.get(currentBachelor).getIndex());
                next.get(currentBachelor).increase();

                //check if current woman is available for current bachelor
                if (couples.get(currentWoman) == null) {
                    //this woman is available for this man, make the match
                    couples.put(currentWoman, currentBachelor);
                    logger.trace("Women " + currentWoman + " has ACCEPTED the man: " + currentBachelor);
                    stillSingle = false;
                    break;
                } else {
                    //current woman had already accepted the proposal from some other man
                    //check if women is interested accepting current bachelor
                    // and dumping the man which she had accepted earlier
                    int alreadyAcceptedMan = couples.get(currentWoman);
                    if (willChangePartner(currentBachelor, alreadyAcceptedMan, currentWoman)) {

                        //current women will accept
                        couples.put(currentWoman, currentBachelor);
                        // add the dumped man in bachelor list
                        bachelors.addLast(alreadyAcceptedMan);
                        bachelors.remove(currentBachelor);
                        logger.trace("Women " + currentWoman + " has DUMPED the man: " + alreadyAcceptedMan);
                        logger.trace("Women " + currentWoman + " has ACCEPTED the man: " + currentBachelor);
                        stillSingle = false;
                        break;
                    } else {
                        logger.trace("Women " + currentWoman + " has better man: " + alreadyAcceptedMan);
                    }
                }
            }
            if (stillSingle) {
                unassignedMen.add(currentBachelor);
            }
        }
    }

    boolean willChangePartner(int currentBachelor, int alreadyAcceptedMan, int currentWomen) {
        double priorityCurrentMan = calcPriority(currentBachelor, currentWomen);
        double priorityAlreadyAcceptedMan = calcPriority(alreadyAcceptedMan, currentWomen);
        return priorityCurrentMan > priorityAlreadyAcceptedMan;
    }

    double calcPriority(int man, int woman) {
        List<Integer> womanPreferences = women.get(woman);
        int index = womanPreferences.indexOf(man);
        if (index == -1) {
            return index;
        }

        return men.size() - index + priorityBonusMap.get(man);
    }

    @Getter
    private static class Counter {
        private int index = 0;

        public void increase() {
            index++;
        }

        public void reset() {
            index = 0;
        }
    }
}

