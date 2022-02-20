package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttributeAssignment {
    private final String name;
    private final int requested;
    private final int assigned;
    private final long rating;
}
