package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class VerboseSolution {
    private final Solution solution;

    private final Map<String, Integer> assignments;
}
