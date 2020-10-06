package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlgorithmSpecificParameter<T> {
    private final String name;

    private final Class<T> type;

    private T value;
}
