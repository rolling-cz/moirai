package cz.rolling.moirai.service;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmExecutor {

    private final ApplicationContext context;

    public AlgorithmExecutor(ApplicationContext context) {
        this.context = context;
    }

    public SolutionHolder executeAlgorithm(WizardState wizardState) throws ClassNotFoundException {
        AlgorithmFactory algorithmFactory = (AlgorithmFactory) context.getBean(
                Class.forName(wizardState.getAlgorithmConfiguration().getAlgorithmFactoryName())
        );
        Algorithm algorithm = algorithmFactory.createAlgorithmManager(wizardState);
        return algorithm.findBestAssignment();
    }
}
