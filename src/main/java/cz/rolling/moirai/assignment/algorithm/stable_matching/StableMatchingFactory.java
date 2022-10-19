package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.assignment.algorithm.Algorithm;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.assignment.preference.ContentPreferenceResolver;
import cz.rolling.moirai.model.common.AlgorithmSpecificParameter;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.form.MainConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class StableMatchingFactory implements AlgorithmFactory {

    private static final HashSet<AlgorithmFeature> ALGORITHM_FEATURES = new HashSet<>(Arrays.asList(
            AlgorithmFeature.CONTENT_APPROACH,
            AlgorithmFeature.MULTI_SELECT,
            AlgorithmFeature.NOT_ENOUGH_PLAYERS
    ));

    private static final Set<AlgorithmSpecificParameter<?>> PARAMETER_SET = new HashSet<>();

    @Override
    public String getName() {
        return "stable-matching";
    }

    @Override
    public Class<?> getFactoryClass() {
        return this.getClass();
    }

    @Override
    public Set<AlgorithmFeature> getSupportedFeatures() {
        return ALGORITHM_FEATURES;
    }

    @Override
    public Set<AlgorithmSpecificParameter<?>> getParameterList() {
        return PARAMETER_SET;
    }

    @Override
    public Algorithm createAlgorithmManager(WizardState wizardState) {
        int missingPlayers = wizardState.getCharactersConfiguration().getNumberOfCharacters() -
                wizardState.getAlgorithmConfiguration().getNumberOfUsers();
        if (missingPlayers > 0) {
            wizardState.getAlgorithmConfiguration().getUserList().addAll(
                    generateDummyPlayers(
                            missingPlayers,
                            wizardState.getMainConfiguration(),
                            wizardState.getAlgorithmConfiguration().getNumberOfUsers()
                    )
            );
        }

        ContentPreferenceResolver preferenceResolver = new ContentPreferenceResolver(wizardState);
        return new StableMatchingAlgorithm(
                preferenceResolver,
                new StableMatchingProcessorVar1(),
                wizardState.getCharactersConfiguration().getNumberOfCharacters(),
                wizardState.getMainConfiguration().getMultiSelect(),
                false
        );
    }

    private Collection<User> generateDummyPlayers(int missingPlayers, MainConfiguration mainConfiguration, int lastId) {
        Map<String, Integer> attributes = mainConfiguration.getAttributeList().stream()
                .collect(Collectors.toMap(
                        CharacterAttribute::getName,
                        attr -> (attr.getMin() + attr.getMax()) / 2)
                );

        return IntStream.range(0, missingPlayers).mapToObj(i -> User.builder()
                .id(lastId + i)
                .name("Dummy " + i)
                .surname("Dummy " + i)
                .wantsPlayGender(Gender.AMBIGUOUS)
                .attributeMap(attributes)
                .isDummy(true)
                .build()).collect(Collectors.toList());
    }
}
