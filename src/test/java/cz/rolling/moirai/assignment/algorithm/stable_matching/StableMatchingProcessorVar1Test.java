package cz.rolling.moirai.assignment.algorithm.stable_matching;

import cz.rolling.moirai.exception.NoSolutionException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class StableMatchingProcessorVar1Test {

    @Test(expected = NoSolutionException.class)
    public void testFindCouplesNoSolution() {
        int[][] men = {{4, 3, 2, 1, 0}, {3, 2, 4, 1, 0}, {2, 1, 3, 0, 4}, {1, 0, 2, 3, 4}, {0, 1, 2, 3, 4}};
        int[][] women = {{4, 3, 2, 1, 0}, {3, 4, 2, 1, 0}, {2, 3, 1, 4, 0}, {1, 2, 0, 3, 4}, {0, 1, 2, 3, 4}};
        boolean[][] forbidden = {{false, false, false, false, true}, {false, false, false, true, false}, {false, false, true, false, false}, {false, true, false, false, false}, {true, false, false, false, false}};

        StableMatchingProcessorVar1 processor = new StableMatchingProcessorVar1();
        processor.init(men, women, forbidden);
        Map<Integer, Integer> couples = processor.process();
        Assert.assertEquals(null, couples);
    }

    @Test
    public void testFindCouplesSinglesolution() {
        int[][] men = {{4, 3, 2, 1, 0}, {3, 2, 4, 1, 0}, {2, 1, 3, 0, 4}, {1, 0, 2, 3, 4}, {0, 1, 2, 3, 4}};
        int[][] women = {{4, 3, 2, 1, 0}, {3, 4, 2, 1, 0}, {2, 3, 1, 4, 0}, {1, 2, 0, 3, 4}, {0, 1, 2, 3, 4}};
        boolean[][] forbidden = {{false, false, false, false, false}, {false, false, false, false, false}, {false, false, false, false, false}, {false, false, false, false, false}, {false, false, false, false, false}};

        StableMatchingProcessorVar1 processor = new StableMatchingProcessorVar1();
        processor.init(men, women, forbidden);
        Map<Integer, Integer> couples = processor.process();
        Assert.assertEquals( Map.of(0, 4, 1, 3, 2, 2, 3, 1, 4, 0), couples);
    }
}
