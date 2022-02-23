package cz.rolling.moirai.model.common.result;

import cz.rolling.moirai.model.common.Assignment;

import java.util.List;

public interface Solution extends Comparable<Solution> {
    Integer getRating();

    List<Assignment> getAssignmentList();
}
