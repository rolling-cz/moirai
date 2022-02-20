package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenderAssignment {
    private final Gender requested;
    private final Gender assigned;
    private final int rating;
}
