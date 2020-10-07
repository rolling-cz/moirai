package cz.rolling.moirai.assignment.helper;


import cz.rolling.moirai.assignment.algorithm.content_dfs.AssignmentTask;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterType;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.UnwantedAssignmentType;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.content.ContentConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PreferencesHolder {

    private final Map<Assignment, Integer> usersRankForChar = new HashMap<>();
    private final List<Character> characterList;
    private final List<User> userList;
    private final Map<Integer, List<Integer>> usersWantingCharacter = new HashMap<>();
    private final ContentConfiguration configuration;
    private final Set<Integer> usersForSingleRole = new HashSet<>();
    private final Set<Integer> usersForDoubleRole = new HashSet<>();

    public PreferencesHolder(ContentConfiguration configuration, Set<AssignmentWithRank> preferenceSet, List<Character> characterList, List<User> userList) {
        this.configuration = configuration;
        this.characterList = characterList;
        this.userList = userList;

        initRatingsByAssignment(preferenceSet);
        initWantedCharacters();
        initSingleDoubleRoles(userList);
    }

    private void initRatingsByAssignment(Set<AssignmentWithRank> preferenceSet) {
        preferenceSet.forEach(assignmentWithRating ->
                usersRankForChar.put(assignmentWithRating.getAssignment(), assignmentWithRating.getRank())
        );
    }

    private void initSingleDoubleRoles(List<User> userList) {
        userList.forEach(u -> {
            if (u.isWantsToPlaySingleRole()) {
                usersForSingleRole.add(u.getId());
            }
            if (u.isWantsToPlayDoubleRole()) {
                usersForDoubleRole.add(u.getId());
            }
        });
    }

    private void initWantedCharacters() {
        Map<Integer, List<AssignmentWithRank>> usersWantingCharacterTemp = new HashMap<>();
        for (int i = 0; i < configuration.getCharacterCount(); i++) {
            usersWantingCharacterTemp.put(i, new ArrayList<>());
        }

        usersRankForChar.forEach((assignment, rank) -> {
            List<AssignmentWithRank> userList = usersWantingCharacterTemp.get(assignment.getCharId());
            userList.add(new AssignmentWithRank(assignment, rank));
        });

        usersWantingCharacterTemp.forEach((key, value) -> {
            value.sort(Comparator.naturalOrder());
            usersWantingCharacter.put(key, value.stream()
                    .map(rankedAssignment -> rankedAssignment.getAssignment().getUserId())
                    .collect(Collectors.toList()));
        });
    }

    public List<Integer> getUsersWantingChar(Integer charId) {
        return usersWantingCharacter.get(charId);
    }

    public int rankAssignmentList(List<Assignment> assignments) {
        int rankSum = 0;

        for (Assignment a : assignments) {
            rankSum += calcRatingOfAssignment(a);
        }

        return rankSum;
    }

    public Integer getAssignmentRank(Assignment a) {
        return usersRankForChar.get(a);
    }

    public int calcRatingOfAssignment(Assignment a) {
        Integer rank = usersRankForChar.get(a);
        if (rank != null) {
            Character ch = characterList.get(a.getCharId());
            if (ch.getType() == CharacterType.FULL) {
                return configuration.getRatingForNthPred(rank - 1);
            } else {
                return configuration.getRatingForNthPred(rank - 1) / 2;
            }
        } else {
            UnwantedAssignmentType unwantedType = getUnwantedAssignmentType(a);
            switch (unwantedType) {
                case UNWANTED_CHAR:
                    return configuration.getUnwantedCharPreference();
                case UNWANTED_CHAR_BUT_NO_PREF:
                    return configuration.getUnwantedCharWithNoPref();
                case UNWANTED_SINGLE_ROLE:
                    return configuration.getUnwantedCharType();
                case UNWANTED_DOUBLE_ROLE:
                    return configuration.getUnwantedCharType() / 2;
                case UNWANTED_CHAR_GENDER:
                    return configuration.getUnwantedCharGender();
                default:
                    throw new RuntimeException("Unknown type " + unwantedType);
            }
        }
    }

    public User getUser(Integer userId) {
        return userList.get(userId);
    }

    public UnwantedAssignmentType getUnwantedAssignmentType(Assignment assignment) {
        Character ch = characterList.get(assignment.getCharId());
        User u = userList.get(assignment.getUserId());

        if (!isCorrectGender(u.getId(), ch.getId())) {
            return UnwantedAssignmentType.UNWANTED_CHAR_GENDER;
        } else if (ch.getType() == CharacterType.FULL && !u.isWantsToPlaySingleRole()) {
            return UnwantedAssignmentType.UNWANTED_SINGLE_ROLE;
        } else if ((ch.getType() == CharacterType.FIRST_PART || ch.getType() == CharacterType.SECOND_PART)
                && !u.isWantsToPlayDoubleRole()) {
            return UnwantedAssignmentType.UNWANTED_DOUBLE_ROLE;
        } else {
            if (u.getPreferences().isEmpty()) {
                return UnwantedAssignmentType.UNWANTED_CHAR_BUT_NO_PREF;
            } else {
                return UnwantedAssignmentType.UNWANTED_CHAR;
            }
        }
    }

    public boolean isUserWillingToPlayCharType(CharacterType type, Integer userId) {
        if (type == CharacterType.FULL) {
            return usersForSingleRole.contains(userId);
        } else {
            return usersForDoubleRole.contains(userId);
        }
    }

    public CharacterType getTypeOfCharacter(Integer charId) {
        return characterList.get(charId).getType();
    }

    public int getBestPossibleOutcome(AssignmentTask task) {
        int result = task.getCurrentRank();
        result += task.getUnwantedCharNumber() * configuration.getUnwantedCharWithNoPref();

        Set<Integer> assignedCharIdSet = task.getAssignedCharIdSet();
        for (Character ch : characterList) {
            if (!assignedCharIdSet.contains(ch.getId())) {
                if (ch.getType() == CharacterType.FULL) {
                    result += configuration.getRatingForNthPred(0);
                } else {
                    result += configuration.getRatingForNthPred(0) / 2;
                }
            }
        }

        return result;
    }

    public boolean isCorrectGender(Integer userId, Integer charId) {
        User u = userList.get(userId);
        Character ch = characterList.get(charId);

        return u.getWantsPlayGender() == ch.getGender() ||
                u.getWantsPlayGender() == Gender.AMBIGUOUS ||
                ch.getGender() == Gender.AMBIGUOUS;
    }
}
