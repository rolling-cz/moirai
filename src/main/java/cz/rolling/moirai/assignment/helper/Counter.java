package cz.rolling.moirai.assignment.helper;

import lombok.Getter;

@Getter
public class Counter {
    private int number = 0;

    public void add() {
        number++;
    }
}
