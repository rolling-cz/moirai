package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class VerboseSolution {
    private final Integer rating;

    private final List<AssignmentWithRank> assignmentList;

    private final Map<String, Integer> distributionMap;
}
