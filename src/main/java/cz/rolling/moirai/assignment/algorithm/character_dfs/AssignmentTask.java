package cz.rolling.moirai.assignment.algorithm.character_dfs;

import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.CharacterType;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.content.ContentConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssignmentTask {

    private final ContentConfiguration configuration;
    private final CharacterPreferenceResolver preferencesHolder;
    private final List<Assignment> assignmentList;
    private final Set<Integer> assignedCharIdSet;
    private final Set<Integer> assignedUserIdSet;
    private final Set<Integer> unwantedCharIdSet;
    private final Map<CharacterType, Set<Integer>> blockedUsers = new HashMap<>();
    private final int currentRank;

    public AssignmentTask(AssignmentTask previousTask, Assignment newAssignment) {
        configuration = previousTask.getConfiguration();
        preferencesHolder = previousTask.preferencesHolder;
        assignmentList = new ArrayList<>(previousTask.getAssignmentList());
        assignedCharIdSet = new HashSet<>(previousTask.assignedCharIdSet);
        assignedUserIdSet = new HashSet<>(previousTask.assignedUserIdSet);
        unwantedCharIdSet = new HashSet<>(previousTask.unwantedCharIdSet);
        for (CharacterType type : CharacterType.values()) {
            blockedUsers.put(type, new HashSet<>(previousTask.blockedUsers.get(type)));
        }

        addAssignment(newAssignment);
        currentRank = previousTask.currentRank + preferencesHolder.calcRatingOfAssignment(newAssignment);
    }

    public AssignmentTask(ContentConfiguration configuration, CharacterPreferenceResolver preferencesHolder) {
        this.configuration = configuration;
        this.preferencesHolder = preferencesHolder;
        this.assignmentList = new ArrayList<>();
        this.assignedCharIdSet = new HashSet<>();
        this.assignedUserIdSet = new HashSet<>();
        this.unwantedCharIdSet = new HashSet<>();
        for (CharacterType type : CharacterType.values()) {
            blockedUsers.put(type, new HashSet<>());
        }
        currentRank = 0;
    }

    public List<Integer> getNextLeastWantedChar() {
        int leastWantedSum = Integer.MAX_VALUE;
        int leastWantedBestRank = Integer.MAX_VALUE;
        List<Integer> leastWantedCharId = new ArrayList<>();

        for (int charId = 0; charId < configuration.getCharacterCount(); charId++) {
            if (assignedCharIdSet.contains(charId) || unwantedCharIdSet.contains(charId)) {
                continue;
            }

            int unAssignedUserSum = 0;
            CharacterType characterType = preferencesHolder.getTypeOfCharacter(charId);
            for (Integer userId : preferencesHolder.getUsersWantingChar(charId)) {
                if (isUserEligibleForCharType(userId, characterType)) {
                    unAssignedUserSum++;
                }
            }

            int bestRank = findBestRank(charId);
            if (unAssignedUserSum == 0) {
                unwantedCharIdSet.add(charId);
            } else if (unAssignedUserSum < leastWantedSum || bestRank < leastWantedBestRank) {
                leastWantedSum = unAssignedUserSum;
                leastWantedBestRank = bestRank;
                leastWantedCharId = new ArrayList<>();
                leastWantedCharId.add(charId);
            } else if (unAssignedUserSum == leastWantedSum && bestRank == leastWantedBestRank) {
                leastWantedCharId.add(charId);
            }
        }

        return leastWantedCharId;
    }

    private int findBestRank(int charId) {
        return preferencesHolder.getUsersWantingChar(charId).stream()
                .map(userId -> preferencesHolder.getRating(new Assignment(userId, charId)))
                .mapToInt(a -> a)
                .min().orElse(Integer.MAX_VALUE);
    }

    public List<Integer> getUnwantedChars() {
        return new ArrayList<>(unwantedCharIdSet);
    }

    public boolean isComplete() {
        return assignedCharIdSet.size() == configuration.getUserCount();
    }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    private ContentConfiguration getConfiguration() {
        return configuration;
    }

    private void addAssignment(Assignment newAssignment) {
        assignmentList.add(newAssignment);
        assignedCharIdSet.add(newAssignment.getCharId());
        assignedUserIdSet.add(newAssignment.getUserId());
        unwantedCharIdSet.remove(newAssignment.getCharId());

        CharacterType characterType = preferencesHolder.getTypeOfCharacter(newAssignment.getCharId());
        blockedUsers.get(characterType).add(newAssignment.getUserId());
    }

    public boolean isUserEligibleForChar(Integer userId, Integer charId) {
        CharacterType characterType = preferencesHolder.getTypeOfCharacter(charId);
        return isUserEligibleForCharType(userId, characterType);
    }

    private boolean isUserEligibleForCharType(Integer userId, CharacterType characterType) {
        return preferencesHolder.isUserWillingToPlayCharType(characterType, userId)
                && isUserNotBlockedForCharType(userId, characterType);
    }

    public boolean isUserNotBlockedForChar(Integer userId, Integer charId) {
        CharacterType characterType = preferencesHolder.getTypeOfCharacter(charId);
        return isUserNotBlockedForCharType(userId, characterType);
    }

    private boolean isUserNotBlockedForCharType(Integer userId, CharacterType characterType) {
        if (characterType == CharacterType.FULL) {
            return !(blockedUsers.get(CharacterType.FULL).contains(userId)
                    || blockedUsers.get(CharacterType.FIRST_PART).contains(userId)
                    || blockedUsers.get(CharacterType.SECOND_PART).contains(userId));
        } else {
            return !(blockedUsers.get(CharacterType.FULL).contains(userId)
                    || blockedUsers.get(characterType).contains(userId));
        }
    }

    public List<Integer> getUsersBlockedForHalfGame() {
        ArrayList<Integer> onlyFirst = new ArrayList<>(blockedUsers.get(CharacterType.FIRST_PART));
        onlyFirst.removeAll(blockedUsers.get(CharacterType.SECOND_PART));

        ArrayList<Integer> onlySecond = new ArrayList<>(blockedUsers.get(CharacterType.SECOND_PART));
        onlySecond.removeAll(blockedUsers.get(CharacterType.FIRST_PART));

        onlyFirst.addAll(onlySecond);

        return onlyFirst;
    }

    public Set<Integer> getAssignedCharIdSet() {
        return Collections.unmodifiableSet(assignedCharIdSet);
    }

    public int getUnwantedCharNumber() {
        return unwantedCharIdSet.size();
    }

    public int getCurrentRank() {
        return currentRank;
    }

    public Integer getNextUnresolvedPlayer() {
        return preferencesHolder.getUserList().stream()
                .map(User::getId)
                .filter(id -> !assignedUserIdSet.contains(id))
                .findFirst()
                .orElse(null);
    }
}
