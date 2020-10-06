package cz.rolling.moirai.model.content;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ContentConfiguration {
    private int characterCount = 155;
    private int userCount = 135;
    private int preferencesPerUser = 7;
    private List<Integer> ranks = new ArrayList<>(Arrays.asList(7, 6, 5, 4, 3, 2, 1));

    private int unwantedCharWithNoPref = 0;
    private int unwantedCharPreference = -10;
    private int unwantedCharType = -100;
    private int unwantedCharGender = -150;


    private int searchWide = 2;
    private int maximumTriedSolution = 5000000;
    private int numberOfBestSolutions = 10;


    public int getRatingForNthPred(int nth) {
        return ranks.get(nth);
    }
}
