package cz.rolling.moirai.model.common;

import lombok.Getter;

@Getter
public class AssignmentDetailCharacters extends AssignmentDetail {
    private final CharacterAssignmentType assignmentType;

    public AssignmentDetailCharacters(
            Assignment assignment,
            Integer rating,
            GenderAssignment gender,
            CharacterAssignmentType assignmentType) {
        super(assignment, rating, gender);
        this.assignmentType = assignmentType;
    }
}
