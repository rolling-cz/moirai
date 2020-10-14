package cz.rolling.moirai.assignment.algorithm.character_dfs;

import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.CharacterType;
import cz.rolling.moirai.model.content.ContentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class AssignmentProcessor {

    private final CharacterPreferenceResolver preferencesHolder;
    private final SolutionHolder solutionHolder;
    private final List<Integer> allUserIdList = new ArrayList<>();
    private final int wideOfSearch;

    private final Logger logger = LoggerFactory.getLogger(AssignmentProcessor.class);

    public AssignmentProcessor(CharacterPreferenceResolver preferencesHolder, SolutionHolder solutionHolder, ContentConfiguration configuration, int wideOfSearch) {
        this.preferencesHolder = preferencesHolder;
        this.solutionHolder = solutionHolder;
        this.wideOfSearch = wideOfSearch;

        for (int i = 0; i < configuration.getUserCount(); i++) {
            allUserIdList.add(i);
        }
    }

    public List<AssignmentTask> process(AssignmentTask task) {
        if (task.isComplete()) {
            solutionHolder.saveSolution(task.getAssignmentList());
            return Collections.emptyList();
        }

        if (preferencesHolder.getBestPossibleOutcome(task) < solutionHolder.getWorstSolution().getRating()) {
            int depth = task.getAssignedCharIdSet().size();
            if (depth < 100) {
                logger.debug("cut at depth " + depth);
            }
            solutionHolder.saveFailedSolution();
            return Collections.emptyList();
        }

        Integer charId = task.getNextLeastWantedChar();
        List<Integer> userIdList;
        BiFunction<Integer, Integer, Boolean> canBeChosen;
        if (charId != null) {
            userIdList = preferencesHolder.getUsersWantingChar(charId);
            canBeChosen = task::isUserEligibleForChar;
        } else if (task.hasUnWantedCharacters()) {
            charId = task.getNextUnwantedChar();
            userIdList = allUserIdList;
            canBeChosen = (uId, chId) -> task.isUserEligibleForChar(uId, chId) && preferencesHolder.isCorrectGender(uId, chId);
        } else {
            logger.warn("Unfinished task without any other char to process - " + task);
            return Collections.emptyList();
        }

        List<AssignmentTask> newTasks = createTasks(task, charId, userIdList, canBeChosen);

        if (newTasks.isEmpty()) {
            CharacterType typeOfCharacter = preferencesHolder.getTypeOfCharacter(charId);
            if (typeOfCharacter != CharacterType.FULL) {
                userIdList = task.getUsersBlockedForHalfGame();
                userIdList.sort((a, b) -> {
                    boolean aWantsSingle = preferencesHolder.getUser(a).isWantsToPlaySingleRole();
                    boolean bWantsSingle = preferencesHolder.getUser(b).isWantsToPlaySingleRole();
                    if (aWantsSingle == bWantsSingle) {
                        return 0;
                    } else {
                        return aWantsSingle ? -1 : 1;
                    }
                });
            }
            canBeChosen = (uId, chId) -> task.isUserNotBlockedForChar(uId, chId) && preferencesHolder.isCorrectGender(uId, chId);
            newTasks = createTasks(task, charId, userIdList, canBeChosen);

            if (typeOfCharacter != CharacterType.FULL && newTasks.isEmpty()) {
                newTasks = createTasks(task, charId, allUserIdList, canBeChosen);
            }

            if (newTasks.isEmpty()) {
                newTasks = createTasks(task, charId, allUserIdList, task::isUserNotBlockedForChar);
            }
        }

        if (newTasks.isEmpty()) {
            solutionHolder.saveFailedSolution();
        }

        Collections.reverse(newTasks);
        return newTasks;
    }

    private List<AssignmentTask> createTasks(AssignmentTask task,
                                             Integer charId,
                                             List<Integer> userIdList,
                                             BiFunction<Integer, Integer, Boolean> canBeChosen) {
        List<AssignmentTask> newTasks = new ArrayList<>();
        int startedTasks = 0;
        for (Integer userId : userIdList) {
            if (!canBeChosen.apply(userId, charId)) {
                continue;
            }

            if (++startedTasks > wideOfSearch) {
                break;
            }

            newTasks.add(new AssignmentTask(task, new Assignment(userId, charId)));
        }
        return newTasks;
    }
}
