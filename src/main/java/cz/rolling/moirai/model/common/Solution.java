package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class Solution implements Comparable<Solution> {

    public static final Solution EMPTY = new Solution(Integer.MIN_VALUE);
    private final Integer rating;
    private final List<Assignment> assignmentList;

    private Solution(int rating) {
        this(rating, Collections.emptyList());
    }


    @Override
    public String toString() {
        return "Solution{" +
                "rank=" + rating +
                ", assignmentList=" + assignmentList +
                '}';
    }

    @Override
    public int compareTo(Solution o) {
        return rating.compareTo(o.rating);
    }
}
