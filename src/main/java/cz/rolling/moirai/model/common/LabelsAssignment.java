package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LabelsAssignment {
    private final List<CharacterLabel> triggeredLabels;
    private final int rating;
}
