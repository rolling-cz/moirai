package cz.rolling.moirai.service;

import cz.rolling.moirai.exception.GeneralFormatImportException;
import cz.rolling.moirai.exception.UnknownCharacterImportException;
import cz.rolling.moirai.exception.UnknownGenderImportException;
import cz.rolling.moirai.exception.WrongAttributeValueImportException;
import cz.rolling.moirai.exception.WrongLineImportException;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.CharacterProperty;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.common.UserProperty;
import cz.rolling.moirai.model.form.MainConfiguration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class ImportCsvParser {

    public static String WANTED_PREFIX = "WantedAs";
    public static String HATED_PREFIX = "NotWantedAs";

    public List<Character> parseCharacterList(InputStream is, MainConfiguration mainConfiguration) {
        List<Character> characterList = new ArrayList<>();

        String[] header = getCharacterColumnList(mainConfiguration).toArray(new String[0]);
        CSVFormat csvFormat = CSVFormat.RFC4180.withHeader(header);
        CSVParser csvParser = createParser(is, csvFormat);
        int index = 0;
        for (CSVRecord record : csvParser) {
            if (record.size() != header.length) {
                throw new WrongLineImportException(index + 1, csvFormat.getHeader());
            }

            Character newCharacter = new Character();
            newCharacter.setId(index);
            newCharacter.setName(record.get(CharacterProperty.NAME.getKey()));
            newCharacter.setGender(translateGender(record.get(CharacterProperty.GENDER.getKey()), index + 1));
            newCharacter.setAttributeMap(loadAttributes(record, mainConfiguration, index + 1));
            characterList.add(newCharacter);
            index++;
        }

        return characterList;
    }

    private Map<String, Integer> loadAttributes(CSVRecord record, MainConfiguration mainConfiguration, int line) {
        Map<String, Integer> attributeMap = new HashMap<>();


        for (CharacterAttribute attr : mainConfiguration.getAttributeList()) {
            String valueString = record.get(attr.getName());
            int value;
            try {
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException e) {
                throw new WrongAttributeValueImportException(line, valueString, attr);
            }

            if (value < attr.getMin() || value > attr.getMax()) {
                throw new WrongAttributeValueImportException(line, valueString, attr);
            }

            attributeMap.put(attr.getName(), value);
        }

        return attributeMap;
    }

    private CSVParser createParser(InputStream is, CSVFormat csvFormat) {
        try {
            return csvFormat.parse(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new GeneralFormatImportException(csvFormat.getHeader(), e.getMessage());
        }
    }

    private Gender translateGender(String key, int line) {
        try {
            return Gender.valueOf(key);
        } catch (IllegalArgumentException e) {
            throw new UnknownGenderImportException(line, key);
        }
    }

    public List<User> parseUserList(InputStream is, MainConfiguration mainConfiguration, List<Character> characterList) {
        List<User> userList = new ArrayList<>();

        String[] header = getUserColumnList(mainConfiguration).toArray(new String[0]);
        CSVFormat csvFormat = CSVFormat.RFC4180.withHeader(header);
        CSVParser csvParser = createParser(is, csvFormat);
        int userId = 0;
        for (CSVRecord record : csvParser) {
            if (record.size() != header.length) {
                throw new WrongLineImportException(userId + 1, csvFormat.getHeader());
            }

            User newUser = new User();
            newUser.setId(userId);
            newUser.setName(record.get(UserProperty.NAME.getKey()));
            newUser.setName(record.get(UserProperty.NAME.getKey()));
            newUser.setSurname(record.get(UserProperty.SURNAME.getKey()));
            newUser.setWantsPlayGender(translateGender(record.get(UserProperty.GENDER.getKey()), userId + 1));

            for (int i = 1; i <= mainConfiguration.getNumberOfPreferredCharacters(); i++) {
                String definedCharacterName = record.get(WANTED_PREFIX + i);
                newUser.savePreference(createPref(definedCharacterName, userId, i, characterList));
            }
            for (int i = 1; i <= mainConfiguration.getNumberOfHatedCharacters(); i++) {
                String definedCharacterName = record.get(HATED_PREFIX + i);
                newUser.savePreference(createPref(definedCharacterName, userId, i, characterList));
            }
            newUser.setAttributeMap(loadAttributes(record, mainConfiguration, userId + 1));

            userList.add(newUser);
            userId++;
        }
        return userList;
    }

    public List<String> getCharacterColumnList(MainConfiguration mainConfiguration) {
        List<String> columnList = new ArrayList<>();
        for (CharacterProperty value : CharacterProperty.values()) {
            columnList.add(value.getKey());
        }
        mainConfiguration.getAttributeList().forEach(attr -> columnList.add(attr.getName()));
        return columnList;
    }

    public String getCharactersFileFormat(MainConfiguration mainConfiguration) {
        return String.join(",", getCharacterColumnList(mainConfiguration));
    }

    public List<String> getUserColumnList(MainConfiguration mainConfiguration) {
        List<String> columnList = new ArrayList<>();
        for (UserProperty value : UserProperty.values()) {
            columnList.add(value.getKey());
        }
        IntStream.range(0, mainConfiguration.getNumberOfPreferredCharacters()).forEach(
                i -> columnList.add(WANTED_PREFIX + (i + 1))
        );
        IntStream.range(0, mainConfiguration.getNumberOfHatedCharacters()).forEach(
                i -> columnList.add(HATED_PREFIX + (i + 1))
        );
        mainConfiguration.getAttributeList().forEach(attr -> columnList.add(attr.getName()));
        return columnList;
    }

    public String getUsersFileFormat(MainConfiguration mainConfiguration) {
        return String.join(",", getUserColumnList(mainConfiguration));
    }

    private AssignmentWithRank createPref(String definedCharacterName, int userId, int nth, List<Character> characterList) {
        Optional<Character> character = characterList.stream()
                .filter(ch -> ch.getName().equals(definedCharacterName))
                .findFirst();
        if (!character.isPresent()) {
            throw new UnknownCharacterImportException(userId + 1, definedCharacterName);
        }
        Assignment assignment = new Assignment(userId, character.get().getId());
        return new AssignmentWithRank(assignment, nth);
    }
}
