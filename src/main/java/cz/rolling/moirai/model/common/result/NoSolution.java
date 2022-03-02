package cz.rolling.moirai.model.common.result;

import cz.rolling.moirai.model.common.Assignment;

import java.util.Collections;
import java.util.List;

public class NoSolution implements Solution{

    private final String reasonCode;

    public NoSolution(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    @Override
    public Integer getRating() {
        return Integer.MIN_VALUE;
    }

    @Override
    public List<Assignment> getAssignmentList() {
        return Collections.emptyList();
    }

    @Override
    public int compareTo(Solution o) {
        return 1;
    }
}
