package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Character {
    private int id;

    private String name;

    private Gender gender;

    private CharacterType type = CharacterType.FULL;

    private Map<String, Integer> attributeMap;
}
