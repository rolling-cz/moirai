package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.enhancer.ContentSolutionEnhancer;
import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.Assignment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

public class StableMatchingAlgorithm implements Algorithm {

    private static final IdWithRatingComparator RATING_COMPARATOR = new IdWithRatingComparator();
    private final ContentPreferenceResolver preferenceResolver;
    private final int numberOfCharacters;

    public StableMatchingAlgorithm(ContentPreferenceResolver preferenceResolver, int numberOfCharacters) {
        this.preferenceResolver = preferenceResolver;
        this.numberOfCharacters = numberOfCharacters;
    }

    @Override
    public SolutionHolder findBestAssignment() {
        SolutionHolder solutionHolder = new SolutionHolder(
                preferenceResolver, new ContentSolutionEnhancer(preferenceResolver), 1);
        solutionHolder.saveSolution(calculateSolution());
        return solutionHolder;
    }

    private List<Assignment> calculateSolution() {
        Map<Integer, Integer> couples = findCouples(
                transformPreferences(Assignment::getUserId, Assignment::getCharId),
                transformPreferences(Assignment::getCharId, Assignment::getUserId)
        );
        return transformCouplesToAssignments(couples);
    }

    protected int[][] transformPreferences(Function<Assignment, Integer> getKeyFn,
                                           Function<Assignment, Integer> getValueFn) {
        List<List<IdWithRating>> preference2dList = init2dList(numberOfCharacters);
        preferenceResolver.getPreferenceMap().forEach(((assignment, rating) ->
                preference2dList.get(getKeyFn.apply(assignment)).add(
                        new IdWithRating(getValueFn.apply(assignment), rating)
                )
        ));
        preference2dList.forEach(list -> list.sort(RATING_COMPARATOR));

        int[][] preferences = new int[numberOfCharacters][numberOfCharacters];
        for (int d1 = 0; d1 < numberOfCharacters; d1++) {
            for (int d2 = 0; d2 < numberOfCharacters; d2++) {
                preferences[d1][d2] = preference2dList.get(d1).get(d2).getId();
            }
        }
        return preferences;
    }

    protected List<List<IdWithRating>> init2dList(int numberOfElements) {
        List<List<IdWithRating>> element2dList = new ArrayList<>();
        IntStream.range(0, numberOfElements).forEach(i -> element2dList.add(new ArrayList<>()));
        return element2dList;
    }

    protected List<Assignment> transformCouplesToAssignments(Map<Integer, Integer> couples) {
        List<Assignment> assignmentList = new ArrayList<>();
        couples.forEach((userId, charId) -> assignmentList.add(new Assignment(userId, charId)));
        return assignmentList;
    }

    private Map<Integer, Integer> findCouples(int[][] men, int[][] women) {

        //couples map will contain all the matches, with women as key and her match (men) as value
        Map<Integer, Integer> couples = new HashMap<>();

        //add all the women to couples map with their matches as NULL (initially)
        for (int i = 0; i < women.length; i++) {
            couples.put(i, null);
        }

        //create a list of all bachelors
        Set<Integer> bachelors = new HashSet<>();
        for (int i = 0; i < men.length; i++) {
            bachelors.add(i);
        }

        int bachelorCount = bachelors.size();

        //do till all the bachelors are nor engaged
        while (bachelorCount > 0) {

            int currentBachelor = bachelors.iterator().next();
            // System.out.println("\nMan " + currentBachelor + " is looking for a woman now-");

            // check for all the women preferences of current bachelor in preference order
            for (int wmen = 0; wmen < men[currentBachelor].length; wmen++) {

                //check if current woman is available for current bachelor
                if (couples.get(wmen) == null) {
                    //this woman is available for this man, make the match
                    couples.put(wmen, currentBachelor);
                    // System.out.println("Women " + wmen + " has ACCEPTED the man: " + currentBachelor);
                    bachelors.remove(currentBachelor);
                    break;
                } else {
                    //current woman had already accepted the proposal from some other man
                    //check if women is interested accepting current bachelor
                    // and dumping the man which she had accepted earlier
                    int alreadyAcceptedMan = couples.get(wmen);
                    if (willChangePartner(currentBachelor, alreadyAcceptedMan, wmen, women)) {

                        //current women will accept
                        couples.put(wmen, currentBachelor);
                        // add the dumped man in bachelor list
                        bachelors.add(alreadyAcceptedMan);
                        bachelors.remove(currentBachelor);
                        // System.out.println("Women " + wmen + " has DUMPED the man: " + alreadyAcceptedMan);
                        // System.out.println("Women " + wmen + " has ACCEPTED the man: " + currentBachelor);
                        break; //
                    }
                }
            }
            //get the size again
            bachelorCount = bachelors.size();
        }
        //return the couples
        return couples;
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
