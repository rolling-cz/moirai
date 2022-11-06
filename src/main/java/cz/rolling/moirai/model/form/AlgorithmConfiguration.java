package cz.rolling.moirai.model.form;

import cz.rolling.moirai.model.common.AlgorithmSpecificParameter;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AlgorithmConfiguration {
    private String algorithmFactoryName;

    private Set<AlgorithmSpecificParameter<?>> parameterSet = new HashSet<>();

    private List<User> userList = new ArrayList<>();

    private List<Assignment> blockedAssignmentList = new ArrayList<>();

    public int getNumberOfUsers() {
        return userList.size();
    }
}
