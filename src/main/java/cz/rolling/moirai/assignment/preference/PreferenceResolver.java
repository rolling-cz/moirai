package cz.rolling.moirai.assignment.preference;

import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.GenderAssignment;

import java.util.List;

public interface PreferenceResolver {
    int calculateRating(List<Assignment> assignmentList);

    Integer getRating(Assignment a);

    GenderAssignment evaluateGenderAssignment(Assignment assignment);
}
