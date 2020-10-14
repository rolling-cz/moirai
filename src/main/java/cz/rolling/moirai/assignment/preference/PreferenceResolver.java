package cz.rolling.moirai.assignment.preference;

import cz.rolling.moirai.model.common.Assignment;

import java.util.List;

public interface PreferenceResolver {
    int calculateRating(List<Assignment> assignmentList);

    Integer getRating(Assignment a);
}
