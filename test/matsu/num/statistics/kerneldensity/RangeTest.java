package matsu.num.statistics.kerneldensity;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link Range} のテスト.
 */
@RunWith(Enclosed.class)
final class RangeTest {

    public static class 生成に関する {

        @Test(expected = None.class)
        public void test_正常系() {
            Range.of(-1d, 1d);
        }

        @Test(expected = None.class)
        public void test_正常系_ゼロ幅() {
            Range.of(1d, 1d);
        }

        @Test(expected = IllegalArgumentException.class)
        public void test_異常系_大小不整合() {
            Range.of(1d, 0d);
        }

        @Test(expected = None.class)
        public void test_正常系_min無限大() {
            Range.of(Double.NEGATIVE_INFINITY, 0d);
        }

        @Test(expected = None.class)
        public void test_正常系_max無限大() {
            Range.of(0d, Double.POSITIVE_INFINITY);
        }

        @Test(expected = IllegalArgumentException.class)
        public void test_異常系_NaN含む() {
            Range.of(Double.NaN, 0d);
        }
    }
}
