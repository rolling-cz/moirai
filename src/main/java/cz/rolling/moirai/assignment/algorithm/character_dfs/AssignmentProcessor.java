package cz.rolling.moirai.assignment.algorithm.character_dfs;

import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.result.DirectSolution;
import cz.rolling.moirai.model.content.ContentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
        // end processing when task contains assignment for everybody
        if (task.isComplete()) {
            DirectSolution solution = new DirectSolution(preferencesHolder.calculateRating(task.getAssignmentList()), task.getAssignmentList());
            solutionHolder.saveSolution(solution);
            return Collections.emptyList();
        }

        // alpha-beta pruning
        if (preferencesHolder.getBestPossibleOutcome(task) < solutionHolder.getWorstSolution().getRating()) {
            int depth = task.getAssignedCharIdSet().size();
            if (depth < 100) {
                logger.debug("cut at depth " + depth);
            }
            solutionHolder.saveFailedSolution();
            return Collections.emptyList();
        }

        // next char to process
        List<Integer> charIds = task.getNextLeastWantedChar();
        List<AssignmentTask> newTasks = new ArrayList<>();
        if (!charIds.isEmpty()) {
            for (Integer charId: charIds) {
                List<AssignmentTask> tasks = createTasksForChar(task, charId, preferencesHolder.getUsersWantingChar(charId), task::isUserEligibleForChar);
                Collections.reverse(tasks);
                newTasks.addAll(tasks);
            };
        }

        // no available preferred assignment -> switch to players to resolve unwanted characters
        if (newTasks.isEmpty()) {
            Integer userId = task.getNextUnresolvedPlayer();
            if (userId == null) {
                logger.warn("Unfinished task without any user to process - " + task);
                return Collections.emptyList();
            }
            Gender wantedGender = preferencesHolder.getUser(userId).getWantsPlayGender();
            List<Character> chars = task.getUnwantedChars().stream()
                    .map(id -> preferencesHolder.getCharacterList().get(id))
                    .sorted(new WantedGenderComparator(wantedGender))
                    .collect(Collectors.toList());

            newTasks = createTasksForUser(task, userId, chars);
            // TODO fix back different type chars
        }

        if (newTasks.isEmpty()) {
            solutionHolder.saveFailedSolution();
        }

        return newTasks;
    }

    private List<AssignmentTask> createTasksForUser(AssignmentTask task,
                                                    Integer userId,
                                                    List<Character> charIdList) {
        List<AssignmentTask> newTasks = new ArrayList<>();
        int startedTasks = 0;
        for (Character chr : charIdList) {
            if (++startedTasks > wideOfSearch) {
                break;
            }

            newTasks.add(new AssignmentTask(task, new Assignment(userId, chr.getId())));
        }
        return newTasks;
    }

    private List<AssignmentTask> createTasksForChar(AssignmentTask task,
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

    private static class WantedGenderComparator implements Comparator<Character> {
        private final Gender wantedGender;
        private final Gender notWantedGender;

        private WantedGenderComparator(Gender wantedGender) {
            this.wantedGender = wantedGender;
            switch (wantedGender) {
                case MALE:
                    notWantedGender = Gender.FEMALE;
                    break;
                case FEMALE:
                    notWantedGender = Gender.MALE;
                    break;
                default:
                    notWantedGender = null;
                    break;
            }
        }

        @Override
        public int compare(Character ch1, Character ch2) {
            if (wantedGender == Gender.AMBIGUOUS) {
                return 0;
            }

            Gender leftGender = ch1.getGender();
            Gender rightGender = ch2.getGender();
            if (leftGender == rightGender) {
                return 0;
            } else if (leftGender == wantedGender) {
                return -1;
            } else if (leftGender == notWantedGender || rightGender == wantedGender) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
