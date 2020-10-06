package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class Solution implements Comparable<Solution> {

    public static final Solution EMPTY = new Solution(Integer.MIN_VALUE);
    private final Integer rank;
    private final List<Assignment> assignmentList;

    private Solution(int rank) {
        this(rank, Collections.emptyList());
    }


    @Override
    public String toString() {
        return "Solution{" +
                "rank=" + rank +
                ", assignmentList=" + assignmentList +
                '}';
    }

    @Override
    public int compareTo(Solution o) {
        return rank.compareTo(o.rank);
    }
}
