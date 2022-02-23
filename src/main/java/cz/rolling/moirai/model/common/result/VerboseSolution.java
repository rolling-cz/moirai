package cz.rolling.moirai.model.common.result;

import cz.rolling.moirai.model.common.AssignmentDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class VerboseSolution {
    private final Integer rating;

    private final List<AssignmentDetail> assignmentList;

    private final Map<String, Integer> distributionMap;
}
