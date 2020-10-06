package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Assignment {

    private final int userId;
    private final int charId;

    @Override
    public String toString() {
        return "<" + getUserId() + "->" + getCharId() + ">";
    }
}
