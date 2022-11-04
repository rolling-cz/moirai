package cz.rolling.moirai.model.content;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ContentConfiguration {
    private int characterCount = 155;
    private int userCount = 135;
    private int preferencesPerUser = 0;
    private List<Integer> ratings = new ArrayList<>();

    private int unwantedCharPreference = -10;
    private int unwantedCharType = -100;
    private int unwantedCharGender = -150;
    private int unwantedCharLabel = -120;

    private int searchWide = 2;
    private int maximumTriedSolution = 500000;
    private int numberOfBestSolutions = 10;

    private boolean isMoreCharRoleTypes = false;

    public int getRatingForNthPred(int nth) {
        return ratings.get(nth);
    }
}
