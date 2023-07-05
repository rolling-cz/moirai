package cz.rolling.moirai.assignment.algorithm.smti_kiraly;

import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.assignment.preference.PreferenceUtils;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterType;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.common.result.Solution;
import cz.rolling.moirai.model.content.ContentConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SmtiKiralyCharacterWrapperTest {

    @Test
    public void testCollectUserPreferences() {
        WizardState wizardState = createWizard1();
        SmtiKiralyCharacterWrapper algorithm = createAlgorithm(wizardState);

        Map<Integer, List<Integer>> prefs = algorithm.collectUserPreferences();
        Assert.assertEquals(5, prefs.size());
        Assert.assertEquals(new ArrayList<>(Arrays.asList(0, 1, 2)), prefs.get(0));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(1, 2, 3)), prefs.get(1));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(2, 3, 4)), prefs.get(2));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(3, 4, 0)), prefs.get(3));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(4, 0, 1)), prefs.get(4));
    }

    @Test
    public void testCollectCharPreferences() {
        WizardState wizardState = createWizard1();
        SmtiKiralyCharacterWrapper algorithm = createAlgorithm(wizardState);

        Map<Integer, List<Integer>> prefs = algorithm.collectCharPreferences();
        Assert.assertEquals(5, prefs.size());
        Assert.assertEquals(new ArrayList<>(Arrays.asList(0, 4, 3)), prefs.get(0));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(1, 0, 4)), prefs.get(1));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(2, 1, 0)), prefs.get(2));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(3, 2, 1)), prefs.get(3));
        Assert.assertEquals(new ArrayList<>(Arrays.asList(4, 3, 2)), prefs.get(4));
    }

    @Test
    public void testFindBestSolutionSimple() {
        WizardState wizardState = createWizard1();
        SmtiKiralyCharacterWrapper algorithm = createAlgorithm(wizardState);

        SolutionHolder solutionHolder = algorithm.findBestAssignment();
        Assert.assertNotNull(solutionHolder);
        Assert.assertEquals(1, solutionHolder.getSolutions().size());

        Solution solution = solutionHolder.getBestSolution();
        Assert.assertEquals(25, solution.getRating().intValue());
        List<Assignment> expectedAssignments = new ArrayList<>(Arrays.asList(
                new Assignment(0, 0),
                new Assignment(1, 1),
                new Assignment(2, 2),
                new Assignment(3, 3),
                new Assignment(4, 4)
        ));
        Assert.assertEquals(expectedAssignments, solution.getAssignmentList());
    }

    @Test
    public void testFindBestSolutionSimpleCompetitive() {
        WizardState wizardState = createWizard2();
        SmtiKiralyCharacterWrapper algorithm = createAlgorithm(wizardState);

        SolutionHolder solutionHolder = algorithm.findBestAssignment();
        Assert.assertNotNull(solutionHolder);
        Assert.assertEquals(1, solutionHolder.getSolutions().size());

        Solution solution = solutionHolder.getBestSolution();
        Assert.assertEquals(17, solution.getRating().intValue());
        List<Assignment> expectedAssignments = new ArrayList<>(Arrays.asList(
                new Assignment(0, 0),
                new Assignment(2, 1),
                new Assignment(1, 2),
                new Assignment(3, 3),
                new Assignment(4, 5)
        ));
        Assert.assertEquals(expectedAssignments, solution.getAssignmentList());
    }

    private WizardState createWizard1() {
        WizardState wizardState = new WizardState();
        wizardState.getCharactersConfiguration().setCharacterList(
                IntStream.range(0, 5).mapToObj(this::createChar).collect(Collectors.toList())
        );
        wizardState.getAlgorithmConfiguration().setUserList(new ArrayList<>(Arrays.asList(
                createUser(0, new ArrayList<>(Arrays.asList(0, 1, 2))),
                createUser(1, new ArrayList<>(Arrays.asList(1, 2, 3))),
                createUser(2, new ArrayList<>(Arrays.asList(2, 3, 4))),
                createUser(3, new ArrayList<>(Arrays.asList(3, 4, 0))),
                createUser(4, new ArrayList<>(Arrays.asList(4, 0, 1)))
        )));

        return wizardState;
    }

    private WizardState createWizard2() {
        WizardState wizardState = new WizardState();
        wizardState.getCharactersConfiguration().setCharacterList(
                IntStream.range(0, 6).mapToObj(this::createChar).collect(Collectors.toList())
        );
        wizardState.getAlgorithmConfiguration().setUserList(new ArrayList<>(Arrays.asList(
                createUser(0, new ArrayList<>(Arrays.asList(0, 1, 2))),
                createUser(1, new ArrayList<>(Arrays.asList(0, 2, 1))),
                createUser(2, new ArrayList<>(Arrays.asList(0, 2, 1))),
                createUser(3, new ArrayList<>(Arrays.asList(3, 4, 5))),
                createUser(4, new ArrayList<>(Arrays.asList(3, 5, 4)))
        )));

        return wizardState;
    }

    private User createUser(int userId, List<Integer> preferredCharIds) {
        User user =  new User(userId, "user" + userId, "user" + userId, Gender.AMBIGUOUS, Collections.emptyMap(), Collections.emptySet(), false);
        for (int i = 0; i < preferredCharIds.size(); i++) {
            user.savePreference(new AssignmentWithRank(new Assignment(userId, preferredCharIds.get(i)), i + 1));
        }
        return user;
    }

    private Character createChar(int charId) {
        return new Character(charId, "char" + charId, Gender.AMBIGUOUS, CharacterType.FULL, Collections.emptyMap(), Collections.emptySet());
    }

    private SmtiKiralyCharacterWrapper createAlgorithm(WizardState wizardState) {
        ContentConfiguration config = PreferenceUtils.createConfiguration(wizardState);
        CharacterPreferenceResolver preferenceResolver = PreferenceUtils.createPreferenceHolder(wizardState, config);
        return new SmtiKiralyCharacterWrapper(preferenceResolver, config);
    }
}
