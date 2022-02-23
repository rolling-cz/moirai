package cz.rolling.moirai.service;

import cz.rolling.moirai.model.common.AssignmentDetail;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.common.result.VerboseSolution;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolutionCsvPrinter {

    private static final String[] FILE_HEADER = new String[]{
            "userName", "userSurname", "characterName", "assignmentRating"
    };

    public byte[] printSolution(VerboseSolution solution, List<User> userList, List<Character> characterList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8), CSVFormat.RFC4180.withHeader(FILE_HEADER));

        for (AssignmentDetail assignment : solution.getAssignmentList()) {
            csvPrinter.printRecord(convertToCsv(assignment, userList, characterList));
        }
        csvPrinter.flush();
        return byteArrayOutputStream.toByteArray();
    }

    private List<String> convertToCsv(AssignmentDetail assignment, List<User> userList, List<Character> characterList) {
        List<String> values = new ArrayList<>();

        User user = userList.get(assignment.getAssignment().getUserId());
        Character character = characterList.get(assignment.getAssignment().getCharId());
        values.add(user.getName());
        values.add(user.getSurname());
        values.add(character.getName());
        values.add(String.valueOf(assignment.getRating()));
        return values;
    }
}
