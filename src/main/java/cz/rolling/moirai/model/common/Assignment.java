package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Assignment {

    private int userId;
    private int charId;

    @Override
    public String toString() {
        return "<" + getUserId() + "->" + getCharId() + ">";
    }
}
