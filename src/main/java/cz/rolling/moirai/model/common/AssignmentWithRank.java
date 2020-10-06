package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssignmentWithRank implements Comparable<AssignmentWithRank> {
    private final Assignment assignment;
    private final Integer rank;

    @Override
    public int compareTo(AssignmentWithRank o) {
        return rank.compareTo(o.rank);
    }
}
