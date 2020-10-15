package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DistributionHeader {
    private final MessageWithParams message;

    private final String distributionKey;

    private final boolean isPositive;
}
