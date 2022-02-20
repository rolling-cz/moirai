package cz.rolling.moirai.model.common;

import lombok.Getter;

import java.util.List;

@Getter
public class AssignmentDetailContent extends AssignmentDetail {
    private final List<AttributeAssignment> attributeAssignments;

    public AssignmentDetailContent(
            Assignment assignment,
            Integer rating,
            GenderAssignment gender,
            List<AttributeAssignment> attributeAssignments) {
        super(assignment, rating, gender);
        this.attributeAssignments = attributeAssignments;
    }
}
