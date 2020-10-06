package cz.rolling.moirai.assignment.algorithm.content_dfs;


import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.helper.PreferencesHolder;
import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.model.content.ContentConfiguration;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ContentDfsAlgorithm implements Algorithm {

    private final PreferencesHolder preferencesHolder;
    private final ContentConfiguration configuration;

    public ContentDfsAlgorithm(PreferencesHolder preferencesHolder, ContentConfiguration configuration) {
        this.preferencesHolder = preferencesHolder;
        this.configuration = configuration;
    }

    @Override
    public SolutionHolder findBestAssignment() {
        SolutionHolder solutionHolder = new SolutionHolder(preferencesHolder, configuration);

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
