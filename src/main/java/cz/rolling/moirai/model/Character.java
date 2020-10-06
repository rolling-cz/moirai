package cz.rolling.moirai.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.csv.CSVRecord;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Character {
    private String name;

    private Gender gender;

    public Character(CSVRecord record, List<String> headerColumnList) {
        this.name = record.get(CharacterProperty.NAME.getKey());
        this.gender = Gender.valueOf(record.get(CharacterProperty.GENDER.getKey()));
    }
}
