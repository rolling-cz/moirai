package cz.rolling.moirai.service;

import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.AssignmentWithRank;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.common.VerboseSolution;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SolutionCsvPrinter {

    private static final String[] FILE_HEADER = new String[]{
            "userName", "userSurname", "characterName", "assignmentType"
    };

    public byte[] printSolution(VerboseSolution solution, List<User> userList, List<Character> characterList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8), CSVFormat.RFC4180.withHeader(FILE_HEADER));

        for (Assignment assignment : solution.getSolution().getAssignmentList()) {
            csvPrinter.printRecord(convertToCsv(assignment, userList, characterList));
        }
        csvPrinter.flush();
        return byteArrayOutputStream.toByteArray();
    }

    private List<String> convertToCsv(Assignment assignment, List<User> userList, List<Character> characterList) {
        List<String> values = new ArrayList<>();

        User user = userList.get(assignment.getUserId());
        Character character = characterList.get(assignment.getCharId());
        values.add(user.getName());
        values.add(user.getSurname());
        values.add(character.getName());

        Optional<Integer> rank = user.getPreferences().stream()
                .filter(a -> a.getAssignment().getCharId() == assignment.getCharId())
                .map(AssignmentWithRank::getRank)
                .findFirst();

        values.add(String.valueOf(rank.orElse(-1)));
        return values;
    }
}
