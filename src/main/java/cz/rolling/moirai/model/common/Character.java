package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    private Set<CharacterLabel> labels = new HashSet<>();
}
