package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AssignmentDetail {
    private final Assignment assignment;
    private final int rating;
    private final GenderAssignment genderAssignment;
}
