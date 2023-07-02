package cz.rolling.moirai.assignment.algorithm.smti_kiraly;

import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.CharacterType;
import cz.rolling.moirai.model.common.DeltaFunction;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.RatingFunction;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.common.result.MetaSolution;
import cz.rolling.moirai.model.common.result.Solution;
import cz.rolling.moirai.model.form.WizardState;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmtiKiralyAlgorithmTest {

    @Test
    public void testInit2dList() {
        int numberOfElements = 5;
        SmtiKiralyAlgorithm algorithm = new SmtiKiralyAlgorithm(createPreferenceResolver(), numberOfElements, numberOfElements, 0, Collections.emptyList());
        List<List<SmtiKiralyAlgorithm.IdWithRating>> list = algorithm.init2dList(numberOfElements);
        Assert.assertEquals(numberOfElements, list.size());
        for (int i = 0; i < numberOfElements; i++) {
            Assert.assertNotNull(list.get(i));
        }
    }

    @Test
    public void testTransformPreferences() {
        SmtiKiralyAlgorithm algorithm = new SmtiKiralyAlgorithm(createPreferenceResolver(), 5, 5, 0, Collections.emptyList());
        List<List<Integer>> users = algorithm.transformPreferences(Assignment::getUserId, Assignment::getCharId, 5, Collections.emptySet());
        Assert.assertEquals(new ArrayList<>(Arrays.asList(4, 3, 2, 1, 0)), users.get(0));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(3, 2, 4, 1, 0)), users.get(1));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(2, 1, 3, 0, 4)), users.get(2));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(1, 0, 2, 3, 4)), users.get(3));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4)), users.get(4));
    }

    @Test
    public void testTransformCouplesToAssignments() {
        SmtiKiralyAlgorithm algorithm = new SmtiKiralyAlgorithm(createPreferenceResolver(), 5, 5,0, Collections.emptyList());
        Map<Integer, Integer> couples = new HashMap<>();
        couples.put(1 ,0);
        couples.put(3, 2);
        List<Assignment> assignmentList = algorithm.transformCouplesToAssignments(couples);
        Assert.assertEquals(2, assignmentList.size());
        Assert.assertTrue(assignmentList.contains(new Assignment(1, 0)));
        Assert.assertTrue(assignmentList.contains(new Assignment(3, 2)));
    }

    @Test
    public void testFindBestAssignment() {
        SmtiKiralyAlgorithm algorithm = new SmtiKiralyAlgorithm(createPreferenceResolver(), 5,5, 0, Collections.emptyList());
        SolutionHolder solutionHolder = algorithm.findBestAssignment();
        Assert.assertEquals(1, solutionHolder.getSolutions().size());

        Solution solution = solutionHolder.getBestSolution();
        Assert.assertEquals(5100, solution.getRating().intValue());
    }

    @Test
    public void testFindBestAssignmentMultiSelect() {
        SmtiKiralyAlgorithm algorithm = new SmtiKiralyAlgorithm(createPreferenceResolver2(),5,5, 2, Collections.emptyList());
        SolutionHolder solutionHolder = algorithm.findBestAssignment();
        Assert.assertEquals(1, solutionHolder.getSolutions().size());

        Solution solution = solutionHolder.getBestSolution();
        Assert.assertEquals(10160, solution.getRating().intValue());
        System.out.println("rating " + solution.getRating());

        Assert.assertTrue(solution instanceof MetaSolution);
        Assert.assertEquals(2, ((MetaSolution) solution).getSolutionList().size());
    }

    @Test
    public void testFindBestAssignmentMultiSelectFewerPlayers() {
        SmtiKiralyAlgorithm algorithm = new SmtiKiralyAlgorithm(createPreferenceResolver3(),3,5, 2, Collections.emptyList());
        SolutionHolder solutionHolder = algorithm.findBestAssignment();
        Assert.assertEquals(1, solutionHolder.getSolutions().size());

        Solution solution = solutionHolder.getBestSolution();
        Assert.assertEquals(6105, solution.getRating().intValue());
        System.out.println("rating " + solution.getRating());

        Assert.assertTrue(solution instanceof MetaSolution);
        Assert.assertEquals(2, ((MetaSolution) solution).getSolutionList().size());
    }

    private ContentPreferenceResolver createPreferenceResolver() {
        WizardState wizardState = new WizardState();
        wizardState.getMainConfiguration().setAttributeList(new ArrayList<>(Collections.singletonList(
                new CharacterAttribute("a1", 1, 5, -5, RatingFunction.LINEAR, DeltaFunction.STANDARD)
        )));
        wizardState.getCharactersConfiguration().setCharacterList(new ArrayList<>(Arrays.asList(
                new Character(0, "char0", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 1), Collections.emptySet()),
                new Character(1, "char1", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 2), Collections.emptySet()),
                new Character(2, "char2", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 3), Collections.emptySet()),
                new Character(3, "char3", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 4), Collections.emptySet()),
                new Character(4, "char4", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 5), Collections.emptySet())
        )));

        wizardState.getAlgorithmConfiguration().setUserList(new ArrayList<>(Arrays.asList(
                new User(0, "user0", "user0", Gender.AMBIGUOUS, Collections.singletonMap("a1", 5), Collections.emptySet(), false),
                new User(1, "user1", "user1", Gender.AMBIGUOUS, Collections.singletonMap("a1", 4), Collections.emptySet(), false),
                new User(2, "user2", "user2", Gender.AMBIGUOUS, Collections.singletonMap("a1", 3), Collections.emptySet(), false),
                new User(3, "user3", "user3", Gender.AMBIGUOUS, Collections.singletonMap("a1", 2), Collections.emptySet(), false),
                new User(4, "user4", "user4", Gender.AMBIGUOUS, Collections.singletonMap("a1", 1), Collections.emptySet(), false)
        )));

        return new ContentPreferenceResolver(wizardState);
    }

    private ContentPreferenceResolver createPreferenceResolver2() {
        WizardState wizardState = new WizardState();
        wizardState.getMainConfiguration().setAttributeList(new ArrayList<>(Collections.singletonList(
                new CharacterAttribute("a1", 1, 5, -5, RatingFunction.LINEAR, DeltaFunction.STANDARD)
        )));
        wizardState.getCharactersConfiguration().setCharacterList(new ArrayList<>(Arrays.asList(
                new Character(0, "char0", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 4), Collections.emptySet()),
                new Character(1, "char1", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 2), Collections.emptySet()),
                new Character(2, "char2", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 3), Collections.emptySet()),
                new Character(3, "char3", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 1), Collections.emptySet()),
                new Character(4, "char4", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 5), Collections.emptySet())
        )));

        wizardState.getAlgorithmConfiguration().setUserList(new ArrayList<>(Arrays.asList(
                new User(0, "user0", "user0", Gender.AMBIGUOUS, Collections.singletonMap("a1", 5), Collections.emptySet(), false),
                new User(1, "user1", "user1", Gender.AMBIGUOUS, Collections.singletonMap("a1", 4), Collections.emptySet(), false),
                new User(2, "user2", "user2", Gender.AMBIGUOUS, Collections.singletonMap("a1", 3), Collections.emptySet(), false),
                new User(3, "user3", "user3", Gender.AMBIGUOUS, Collections.singletonMap("a1", 2), Collections.emptySet(), false),
                new User(4, "user4", "user4", Gender.AMBIGUOUS, Collections.singletonMap("a1", 1), Collections.emptySet(), false)
        )));

        return new ContentPreferenceResolver(wizardState);
    }

    private ContentPreferenceResolver createPreferenceResolver3() {
        WizardState wizardState = new WizardState();
        wizardState.getMainConfiguration().setAttributeList(new ArrayList<>(Collections.singletonList(
                new CharacterAttribute("a1", 1, 5, -5, RatingFunction.LINEAR, DeltaFunction.STANDARD)
        )));
        wizardState.getCharactersConfiguration().setCharacterList(new ArrayList<>(Arrays.asList(
                new Character(0, "char0", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 4), Collections.emptySet()),
                new Character(1, "char1", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 2), Collections.emptySet()),
                new Character(2, "char2", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 3), Collections.emptySet()),
                new Character(3, "char3", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 1), Collections.emptySet()),
                new Character(4, "char4", Gender.AMBIGUOUS, CharacterType.FULL, Collections.singletonMap("a1", 5), Collections.emptySet())
        )));

        wizardState.getAlgorithmConfiguration().setUserList(new ArrayList<>(Arrays.asList(
                new User(0, "user0", "user0", Gender.AMBIGUOUS, Collections.singletonMap("a1", 5), Collections.emptySet(), false),
                new User(1, "user1", "user1", Gender.AMBIGUOUS, Collections.singletonMap("a1", 4), Collections.emptySet(), false),
                new User(2, "user2", "user2", Gender.AMBIGUOUS, Collections.singletonMap("a1", 3), Collections.emptySet(), false)
        )));

        return new ContentPreferenceResolver(wizardState);
    }
}
