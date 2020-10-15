package cz.rolling.moirai.assignment.algorithm.character_dfs;


import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.enhancer.CharacterSolutionEnhancer;
import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.assignment.preference.CharacterPreferenceResolver;
import cz.rolling.moirai.model.content.ContentConfiguration;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CharacterDfsAlgorithm implements Algorithm {

    private final CharacterPreferenceResolver preferencesHolder;
    private final ContentConfiguration configuration;

    public CharacterDfsAlgorithm(CharacterPreferenceResolver preferencesHolder, ContentConfiguration configuration) {
        this.preferencesHolder = preferencesHolder;
        this.configuration = configuration;
    }

    @Override
    public SolutionHolder findBestAssignment() {
        CharacterSolutionEnhancer enhancer = new CharacterSolutionEnhancer(configuration.getPreferencesPerUser(), preferencesHolder);
        SolutionHolder solutionHolder = new SolutionHolder(preferencesHolder, enhancer, configuration.getNumberOfBestSolutions());

        Deque<AssignmentTask> taskQueue = new ConcurrentLinkedDeque<>();
        AssignmentProcessor processor = new AssignmentProcessor(preferencesHolder, solutionHolder, configuration, configuration.getSearchWide());
        taskQueue.add(new AssignmentTask(configuration, preferencesHolder));

        while (!taskQueue.isEmpty()) {
            List<AssignmentTask> newTasks = processor.process(taskQueue.poll());
            for (AssignmentTask task : newTasks) {
                taskQueue.push(task);
            }

            if (solutionHolder.getTriedSolutionCounter() > configuration.getMaximumTriedSolution()) {
                break;
            }
        }

        return solutionHolder;
    }
}
