package cz.rolling.moirai.model.common;

import org.junit.Assert;
import org.junit.Test;

public class DeltaFunctionTest {
    @Test
    public void testMiddleNeutral() {
        Assert.assertEquals(0, DeltaFunction.MIDDLE_NEUTRAL.getDelta(1, 3, 1, 5));
        Assert.assertEquals(0, DeltaFunction.MIDDLE_NEUTRAL.getDelta(5, 3, 1, 5));
        Assert.assertEquals(1, DeltaFunction.MIDDLE_NEUTRAL.getDelta(1, 2, 1, 5));
        Assert.assertEquals(3, DeltaFunction.MIDDLE_NEUTRAL.getDelta(1, 4, 1, 5));
        Assert.assertEquals(4, DeltaFunction.MIDDLE_NEUTRAL.getDelta(1, 5, 1, 5));
        Assert.assertEquals(1, DeltaFunction.MIDDLE_NEUTRAL.getDelta(2, 1, 1, 5));
        Assert.assertEquals(3, DeltaFunction.MIDDLE_NEUTRAL.getDelta(4, 1, 1, 5));
        Assert.assertEquals(4, DeltaFunction.MIDDLE_NEUTRAL.getDelta(5, 1, 1, 5));
        Assert.assertEquals(1, DeltaFunction.MIDDLE_NEUTRAL.getDelta(4, 5, 1, 5));
    }

    @Test
    public void testZeroNeutral() {
        Assert.assertEquals(0, DeltaFunction.ZERO_NEUTRAL.getDelta(1, 0, 0, 5));
        Assert.assertEquals(0, DeltaFunction.ZERO_NEUTRAL.getDelta(5, 0, 0, 5));
        Assert.assertEquals(1, DeltaFunction.ZERO_NEUTRAL.getDelta(1, 2, 0, 5));
        Assert.assertEquals(3, DeltaFunction.ZERO_NEUTRAL.getDelta(1, 4, 0, 5));
        Assert.assertEquals(4, DeltaFunction.ZERO_NEUTRAL.getDelta(1, 5, 0, 5));
        Assert.assertEquals(1, DeltaFunction.ZERO_NEUTRAL.getDelta(2, 1, 0, 5));
        Assert.assertEquals(3, DeltaFunction.ZERO_NEUTRAL.getDelta(4, 1, 0, 5));
    }
}
