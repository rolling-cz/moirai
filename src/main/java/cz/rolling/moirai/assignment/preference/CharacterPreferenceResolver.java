package cz.rolling.moirai.assignment.preference;


import cz.rolling.moirai.assignment.algorithm.character_dfs.AssignmentTask;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterAssignmentType;
import cz.rolling.moirai.model.common.CharacterType;
import cz.rolling.moirai.model.common.UnwantedAssignmentType;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.content.ContentConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CharacterPreferenceResolver extends AbstractPreferenceResolver {

    private final Map<Assignment, Integer> usersRankForChar = new HashMap<>();
    private final Map<Integer, List<Integer>> usersWantingCharacter = new HashMap<>();
    private final ContentConfiguration configuration;
    private final Set<Integer> usersForSingleRole = new HashSet<>();
    private final Set<Integer> usersForDoubleRole = new HashSet<>();

    public CharacterPreferenceResolver(ContentConfiguration configuration,
                                       Set<AssignmentWithRank> preferenceSet,
                                       List<Character> characterList,
                                       List<User> userList) {
        super(characterList, userList);
        this.configuration = configuration;

        initRatingsByAssignment(preferenceSet);
        initWantedCharacters();
        initSingleDoubleRoles(userList);
    }

    @Override
    protected int getRatingForGender() {
        return configuration.getUnwantedCharGender();
    }

    @Override
    public int calculateRating(List<Assignment> assignments) {
        int rankSum = 0;

        for (Assignment a : assignments) {
            rankSum += calcRatingOfAssignment(a);
        }

        return rankSum;
    }

    @Override
    public Integer getRating(Assignment a) {
        return usersRankForChar.get(a);
    }

    public UnwantedAssignmentType getUnwantedAssignmentType(Assignment assignment) {
        Character ch = getCharacterList().get(assignment.getCharId());
        User u = getUserList().get(assignment.getUserId());

        if (!isCorrectGender(u.getId(), ch.getId())) {
            return UnwantedAssignmentType.UNWANTED_CHAR_GENDER;
        } else if (ch.getType() == CharacterType.FULL && !u.isWantsToPlaySingleRole()) {
            return UnwantedAssignmentType.UNWANTED_SINGLE_ROLE;
        } else if ((ch.getType() == CharacterType.FIRST_PART || ch.getType() == CharacterType.SECOND_PART)
                && !u.isWantsToPlayDoubleRole()) {
            return UnwantedAssignmentType.UNWANTED_DOUBLE_ROLE;
        } else {
            return UnwantedAssignmentType.UNWANTED_CHAR;
        }
    }

    public Collection<UnwantedAssignmentType> getPossibleUnwantedTypes() {
        if (configuration.isMoreCharRoleTypes()) {
            return Arrays.asList(UnwantedAssignmentType.values());
        } else {
            return Arrays.stream(UnwantedAssignmentType.values())
                    .filter(type -> !type.isForMoreRoles())
                    .collect(Collectors.toList());
        }
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

    public CharacterAssignmentType calcCharacterAssignmentType(Assignment assignment) {
        Integer rank = usersRankForChar.get(assignment);
        if (rank != null) {
            return CharacterAssignmentType.createPreferredType(rank);
        } else {
            return CharacterAssignmentType.createNotPreferredType(getUnwantedAssignmentType(assignment));
        }
    }

    public int calcRatingOfAssignment(Assignment assignment) {
        CharacterAssignmentType assignmentType = calcCharacterAssignmentType(assignment);
        if (assignmentType.isPreferred()) {
            Character ch = getCharacterList().get(assignment.getCharId());
            if (ch.getType() == CharacterType.FULL) {
                return configuration.getRatingForNthPred(assignmentType.getPreferredRank() - 1);
            } else {
                return configuration.getRatingForNthPred(assignmentType.getPreferredRank() - 1) / 2;
            }
        } else {
            switch (assignmentType.getUnwantedAssignmentType()) {
                case UNWANTED_CHAR:
                    return configuration.getUnwantedCharPreference();
                case UNWANTED_SINGLE_ROLE:
                    return configuration.getUnwantedCharType();
                case UNWANTED_DOUBLE_ROLE:
                    return configuration.getUnwantedCharType() / 2;
                case UNWANTED_CHAR_GENDER:
                    return configuration.getUnwantedCharGender();
                default:
                    throw new RuntimeException("Unknown type " + assignmentType.getUnwantedAssignmentType());
            }
        }
    }

    public User getUser(Integer userId) {
        return getUserList().get(userId);
    }

    public boolean isUserWillingToPlayCharType(CharacterType type, Integer userId) {
        if (type == CharacterType.FULL) {
            return usersForSingleRole.contains(userId);
        } else {
            return usersForDoubleRole.contains(userId);
        }
    }

    public CharacterType getTypeOfCharacter(Integer charId) {
        return getCharacterList().get(charId).getType();
    }

    public int getBestPossibleOutcome(AssignmentTask task) {
        int result = task.getCurrentRank();
        result += task.getUnwantedCharNumber() * configuration.getUnwantedCharPreference();

        Set<Integer> assignedCharIdSet = task.getAssignedCharIdSet();
        for (Character ch : getCharacterList()) {
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
        User u = getUserList().get(userId);
        Character ch = getCharacterList().get(charId);
        return PreferenceUtils.isCorrectGender(u, ch);
    }
}
