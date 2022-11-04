package cz.rolling.moirai.service;

import cz.rolling.moirai.exception.GeneralFormatImportException;
import cz.rolling.moirai.exception.UnknownCharacterImportException;
import cz.rolling.moirai.exception.UnknownGenderImportException;
import cz.rolling.moirai.exception.WrongAttributeValueImportException;
import cz.rolling.moirai.exception.WrongLabelImportException;
import cz.rolling.moirai.exception.WrongLineImportException;
import cz.rolling.moirai.model.common.ApproachType;
import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.CharacterLabel;
import cz.rolling.moirai.model.common.CharacterProperty;
import cz.rolling.moirai.model.common.Gender;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.common.UserProperty;
import cz.rolling.moirai.model.form.MainConfiguration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class ImportCsvParser {

    private final CsvHeaderHolder csvHeader;
    private static final String LABELS_SEPARATOR = " ";

    public ImportCsvParser(MessageSource messageSource) {
        csvHeader = new CsvHeaderHolder(messageSource);
    }

    public List<Character> parseCharacterList(InputStream is, MainConfiguration mainConfiguration, Locale locale) {
        List<Character> characterList = new ArrayList<>();

        String[] header = getCharacterColumnList(mainConfiguration, locale).toArray(new String[0]);
        CSVFormat csvFormat = CSVFormat.RFC4180.withHeader(header);
        CSVParser csvParser = createParser(is, csvFormat);
        int index = 0;
        for (CSVRecord record : csvParser) {
            if (record.size() != header.length) {
                throw new WrongLineImportException(index + 1, csvFormat.getHeader());
            }

            Character newCharacter = new Character();
            newCharacter.setId(index);
            newCharacter.setName(record.get(csvHeader.get(locale, CharacterProperty.NAME)));
            newCharacter.setGender(translateGender(record.get(csvHeader.get(locale, CharacterProperty.GENDER)), index + 1));
            if (mainConfiguration.getApproachType() == ApproachType.CONTENT) {
                newCharacter.setAttributeMap(loadAttributes(record, mainConfiguration, locale, index + 1));
                if (!mainConfiguration.getLabelList().isEmpty()) {
                    newCharacter.setLabels(loadLabels(record, mainConfiguration, locale, index + 1));
                }
            }
            characterList.add(newCharacter);
            index++;
        }

        return characterList;
    }

    private Set<CharacterLabel> loadLabels(CSVRecord record, MainConfiguration mainConfiguration, Locale locale, int line) {
        String allLabelsAsString = record.get(csvHeader.getCharLabels(locale));
        String[] labelStringList = StringUtils.split(allLabelsAsString, LABELS_SEPARATOR);

        Set<CharacterLabel> parsedLabels = new HashSet<>();
        Arrays.stream(labelStringList).forEach(labelString -> {
            CharacterLabel newLabel = new CharacterLabel(labelString);
            if (mainConfiguration.getLabelList().contains(newLabel)) {
                parsedLabels.add(newLabel);
            } else {
                throw new WrongLabelImportException(line, labelString);
            }
        });

        return parsedLabels;
    }

    private Map<String, Integer> loadAttributes(CSVRecord record, MainConfiguration mainConfiguration, Locale locale, int line) {
        Map<String, Integer> attributeMap = new HashMap<>();


        for (CharacterAttribute attr : mainConfiguration.getAttributeList()) {
            String valueString = record.get(csvHeader.get(locale, attr));
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

    public List<User> parseUserList(
            InputStream is,
            MainConfiguration mainConfiguration,
            List<Character> characterList,
            Locale locale
    ) {
        List<User> userList = new ArrayList<>();

        String[] header = getUserColumnList(mainConfiguration, locale).toArray(new String[0]);
        CSVFormat csvFormat = CSVFormat.RFC4180.withHeader(header);
        CSVParser csvParser = createParser(is, csvFormat);
        int userId = 0;
        for (CSVRecord record : csvParser) {
            if (record.size() != header.length) {
                throw new WrongLineImportException(userId + 1, csvFormat.getHeader());
            }

            User newUser = new User();
            newUser.setId(userId);
            newUser.setName(record.get(csvHeader.get(locale, UserProperty.NAME)));
            newUser.setSurname(record.get(csvHeader.get(locale, UserProperty.SURNAME)));
            newUser.setWantsPlayGender(translateGender(record.get(csvHeader.get(locale, UserProperty.GENDER)), userId + 1));

            if (mainConfiguration.getApproachType() == ApproachType.CHARACTERS) {
                for (int i = 1; i <= mainConfiguration.getNumberOfPreferredCharacters(); i++) {
                    String definedCharacterName = record.get(csvHeader.getWanted(locale, i));
                    newUser.savePreference(createPref(definedCharacterName, userId, i, characterList));
                }
                for (int i = 1; i <= mainConfiguration.getNumberOfHatedCharacters(); i++) {
                    String definedCharacterName = record.get(csvHeader.getHated(locale, i));
                    newUser.savePreference(createPref(definedCharacterName, userId, i, characterList));
                }
            }
            if (mainConfiguration.getApproachType() == ApproachType.CONTENT) {
                newUser.setAttributeMap(loadAttributes(record, mainConfiguration, locale,userId + 1));
                if (!mainConfiguration.getLabelList().isEmpty()) {
                    newUser.setLabels(loadLabels(record, mainConfiguration, locale, userId + 1));
                }
            }

            userList.add(newUser);
            userId++;
        }
        return userList;
    }

    public List<String> getCharacterColumnList(MainConfiguration mainConfiguration, Locale locale) {
        List<String> columnList = new ArrayList<>();
        for (CharacterProperty value : CharacterProperty.values()) {
            columnList.add(csvHeader.get(locale, value));
        }
        if (mainConfiguration.getApproachType() == ApproachType.CONTENT) {
            mainConfiguration.getAttributeList().forEach(attr ->
                    columnList.add(csvHeader.get(locale, attr))
            );
            if (!mainConfiguration.getLabelList().isEmpty()) {
                columnList.add(csvHeader.getCharLabels(locale));
            }
        }
        return columnList;
    }

    public String getCharactersFileFormat(MainConfiguration mainConfiguration, Locale locale) {
        return String.join(", ", getCharacterColumnList(mainConfiguration, locale));
    }

    public List<String> getUserColumnList(MainConfiguration mainConfiguration, Locale locale) {
        List<String> columnList = new ArrayList<>();
        for (UserProperty value : UserProperty.values()) {
            columnList.add(csvHeader.get(locale, value));
        }
        if (mainConfiguration.getApproachType() == ApproachType.CHARACTERS) {
            IntStream.range(0, mainConfiguration.getNumberOfPreferredCharacters()).forEach(
                    i -> columnList.add(csvHeader.getWanted(locale, i + 1))
            );
            IntStream.range(0, mainConfiguration.getNumberOfHatedCharacters()).forEach(
                    i -> columnList.add(csvHeader.getHated(locale, i + 1))
            );
        }
        if (mainConfiguration.getApproachType() == ApproachType.CONTENT) {
            mainConfiguration.getAttributeList().forEach(attr -> columnList.add(csvHeader.get(locale, attr)));
            if (!mainConfiguration.getLabelList().isEmpty()) {
                columnList.add(csvHeader.getCharLabels(locale));
            }
        }
        return columnList;
    }

    public String getUsersFileFormat(MainConfiguration mainConfiguration, Locale locale) {
        return String.join(", ", getUserColumnList(mainConfiguration, locale));
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

    private static class CsvHeaderHolder {
        private static final String MESSAGE_CODE_WANTED = "common.required-file-format.header.wanted";
        private static final String MESSAGE_CODE_HATED = "common.required-file-format.header.not-wanted";
        private static final String MESSAGE_CODE_ATTRIBUTE = "common.required-file-format.header.attribute";
        private static final String MESSAGE_CODE_LABEL = "common.required-file-format.header.label";

        private final MessageSource messageSource;

        public CsvHeaderHolder(MessageSource messageSource) {
            this.messageSource = messageSource;
        }

        public String get(Locale locale, CharacterProperty property) {
            return messageSource.getMessage(property.getKey(), null, locale);
        }

        public String get(Locale locale, CharacterAttribute attribute) {
            return messageSource.getMessage(MESSAGE_CODE_ATTRIBUTE, null, locale) + " " + attribute.getName();
        }

        public String get(Locale locale, UserProperty value) {
            return messageSource.getMessage(value.getKey(), null, locale);
        }

        public String getWanted(Locale locale, int i) {
            return messageSource.getMessage(MESSAGE_CODE_WANTED, null, locale) + " " + i;
        }

        public String getHated(Locale locale, int i) {
            return messageSource.getMessage(MESSAGE_CODE_HATED, null, locale) + " " + i;
        }

        public String getCharLabels(Locale locale) {
            return messageSource.getMessage(MESSAGE_CODE_LABEL, null, locale);
        }
    }
}


