package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.CharacterType;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.RatingFunction;
import cz.rolling.moirai.model.common.Solution;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.form.WizardState;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StableMatchingAlgorithmTest {

    @Test
    public void testInit2dList() {
        int numberOfElements = 5;
        StableMatchingAlgorithm algorithm = new StableMatchingAlgorithm(createPreferenceResolver(), 0);
        List<List<StableMatchingAlgorithm.IdWithRating>> list = algorithm.init2dList(numberOfElements);
        Assert.assertEquals(numberOfElements, list.size());
        for (int i = 0; i < numberOfElements; i++) {
            Assert.assertNotNull(list.get(i));
        }
    }

    @Test
    public void testTransformPreferences() {
        StableMatchingAlgorithm algorithm = new StableMatchingAlgorithm(createPreferenceResolver(), 5);
        int[][] users = algorithm.transformPreferences(Assignment::getUserId, Assignment::getCharId);
        Assert.assertArrayEquals(new int[]{4, 3, 2, 1, 0}, users[0]);
        Assert.assertArrayEquals(new int[]{3, 2, 4, 1, 0}, users[1]);
        Assert.assertArrayEquals(new int[]{2, 1, 3, 0, 4}, users[2]);
        Assert.assertArrayEquals(new int[]{1, 0, 2, 3, 4}, users[3]);
        Assert.assertArrayEquals(new int[]{0, 1, 2, 3, 4}, users[4]);
    }

    @Test
    public void testTransformCouplesToAssignments() {
        StableMatchingAlgorithm algorithm = new StableMatchingAlgorithm(createPreferenceResolver(), 5);
        Map<Integer, Integer> couples = new HashMap<>();
        couples.put(0, 1);
        couples.put(2, 3);
        List<Assignment> assignmentList = algorithm.transformCouplesToAssignments(couples);
        Assert.assertEquals(2, assignmentList.size());
        Assert.assertTrue(assignmentList.contains(new Assignment(0, 1)));
        Assert.assertTrue(assignmentList.contains(new Assignment(2, 3)));
    }

    @Test
    public void testFindBestAssignment() {
        StableMatchingAlgorithm algorithm = new StableMatchingAlgorithm(createPreferenceResolver(), 5);
        SolutionHolder solutionHolder = algorithm.findBestAssignment();
        Assert.assertEquals(1, solutionHolder.getSolutions().size());

        Solution solution = solutionHolder.getBestSolution();
        Assert.assertEquals(5100, solution.getRating().intValue());

        List<Assignment> assignmentList = solution.getAssignmentList();
        Assert.assertEquals(5, assignmentList.size());
        Assert.assertTrue(assignmentList.contains(new Assignment(0, 4)));
        Assert.assertTrue(assignmentList.contains(new Assignment(1, 3)));
        Assert.assertTrue(assignmentList.contains(new Assignment(2, 2)));
        Assert.assertTrue(assignmentList.contains(new Assignment(3, 1)));
        Assert.assertTrue(assignmentList.contains(new Assignment(4, 0)));
    }

    private ContentPreferenceResolver createPreferenceResolver() {
        WizardState wizardState = new WizardState();
        wizardState.getMainConfiguration().setAttributeList(new ArrayList<>(Collections.singletonList(
                new CharacterAttribute("a1", 1, 5, -5, RatingFunction.LINEAR)
        )));
        wizardState.getCharactersConfiguration().setCharacterList(new ArrayList<>(Arrays.asList(
                new Character(0, "char0", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 1)),
                new Character(1, "char1", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 2)),
                new Character(2, "char2", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 3)),
                new Character(3, "char3", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 4)),
                new Character(4, "char4", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 5))
        )));

        wizardState.getAlgorithmConfiguration().setUserList(new ArrayList<>(Arrays.asList(
                new User(0, "user0", "user0", Gender.AMBIGUOUS, Collections.singletonMap("a1", 5)),
                new User(1, "user1", "user1", Gender.AMBIGUOUS, Collections.singletonMap("a1", 4)),
                new User(2, "user2", "user2", Gender.AMBIGUOUS, Collections.singletonMap("a1", 3)),
                new User(3, "user3", "user3", Gender.AMBIGUOUS, Collections.singletonMap("a1", 2)),
                new User(4, "user4", "user4", Gender.AMBIGUOUS, Collections.singletonMap("a1", 1))
        )));

        return new ContentPreferenceResolver(wizardState);
    }
}
