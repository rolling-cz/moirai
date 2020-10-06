package cz.rolling.moirai.model.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Character {
    private int id;

    private CharacterType type = CharacterType.FULL;

    private String name;

    private Gender gender;
}
