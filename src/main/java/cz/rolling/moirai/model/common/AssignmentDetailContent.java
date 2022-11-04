package cz.rolling.moirai.model.common;

import lombok.Getter;

import java.util.List;

@Getter
public class AssignmentDetailContent extends AssignmentDetail {
    private final List<AttributeAssignment> attributeAssignments;
    private final LabelsAssignment labelsAssignment;

    public AssignmentDetailContent(
            Assignment assignment,
            Integer rating,
            GenderAssignment gender,
            List<AttributeAssignment> attributeAssignments,
            LabelsAssignment labelsAssignment) {
        super(assignment, rating, gender);
        this.attributeAssignments = attributeAssignments;
        this.labelsAssignment = labelsAssignment;
    }
}
