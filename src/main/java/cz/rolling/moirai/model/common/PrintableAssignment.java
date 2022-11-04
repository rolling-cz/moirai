package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PrintableAssignment {
    private final String name;
    private final String surname;
    private final String character;
    private final GenderAssignment gender;
    private final int rating;
    private final CharacterAssignmentType assignmentType;
    private final List<AttributeAssignment> attributeList;
    private final LabelsAssignment characterLabels;
}
