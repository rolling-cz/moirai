package cz.rolling.moirai.service;

import cz.rolling.moirai.exception.GeneralException;
import cz.rolling.moirai.model.common.ApproachType;
import cz.rolling.moirai.model.common.AssignmentDetailCharacters;
import cz.rolling.moirai.model.common.AssignmentDetailContent;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.PrintableAssignment;
import cz.rolling.moirai.model.common.User;
import cz.rolling.moirai.model.common.result.ResultSummary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolutionDisplayService {

    private final Comparator<PrintableAssignment> naturalNameComparator = Comparator.comparing(PrintableAssignment::getSurname)
            .thenComparing(PrintableAssignment::getName);

    public List<PrintableAssignment> getDetailAssignments(
            List<User> userList,
            List<Character> characterList,
            ApproachType approachType,
            ResultSummary solution) {

        List<PrintableAssignment> rows;
        switch (approachType) {
            case CHARACTERS:
                rows = getAssignmentsForCharacters(userList, characterList, solution);
                break;
            case CONTENT:
                rows = getAssignmentsForContent(userList, characterList, solution);
                break;
            default:
                throw new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown approach type " + approachType);
        }

        rows.sort(naturalNameComparator);
        return rows;
    }

    private List<PrintableAssignment> getAssignmentsForCharacters(
            List<User> userList,
            List<Character> characterList,
            ResultSummary solution) {
        return solution.getAssignmentList().stream()
                .map(assignment -> (AssignmentDetailCharacters) assignment)
                .map(assignment -> {
                    User user = userList.get(assignment.getAssignment().getUserId());
                    Character character = characterList.get(assignment.getAssignment().getCharId());

                    return new PrintableAssignment(
                            user.getName(),
                            user.getSurname(),
                            character.getName(),
                            assignment.getGenderAssignment(),
                            assignment.getRating(),
                            assignment.getAssignmentType(),
                            null,
                            null
                    );
                }).collect(Collectors.toList());
    }

    private List<PrintableAssignment> getAssignmentsForContent(
            List<User> userList,
            List<Character> characterList,
            ResultSummary solution) {
        return solution.getAssignmentList().stream()
                .map(assignment -> (AssignmentDetailContent) assignment)
                .map(assignment -> {
                    User user = userList.get(assignment.getAssignment().getUserId());
                    Character character = characterList.get(assignment.getAssignment().getCharId());

                    return new PrintableAssignment(
                            user.getName(),
                            user.getSurname(),
                            character.getName(),
                            assignment.getGenderAssignment(),
                            assignment.getRating(),
                            null,
                            assignment.getAttributeAssignments(),
                            assignment.getLabelsAssignment()
                    );
                }).collect(Collectors.toList());
    }
}
