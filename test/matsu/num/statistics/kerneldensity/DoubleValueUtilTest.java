/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.statistics.kerneldensity;

import static matsu.num.statistics.kerneldensity.DoubleValueUtil.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link DoubleValueUtil} のテスト.
 */
@RunWith(Enclosed.class)
final class DoubleValueUtilTest {

    public static class correctのテスト {

        @Test
        public void test_有限値はそのまま() {
            assertThat(correctInfinite(1d), is(1d));
            assertThat(correctInfinite(-1d), is(-1d));
        }

        @Test
        public void test_正の無限大の置き換え() {
            assertThat(correctInfinite(Double.POSITIVE_INFINITY), is(Double.MAX_VALUE));
        }

        @Test
        public void test_負の無限大の置き換え() {
            assertThat(correctInfinite(Double.NEGATIVE_INFINITY), is(-Double.MAX_VALUE));
        }

        @Test
        public void test_NaNはそのまま() {
            assertThat(correctInfinite(Double.NaN), is(Double.NaN));
        }
    }

    public static class isSortedのテスト {

        @Test
        public void test_空ならtrue() {
            assertThat(isSorted(new double[] {}), is(true));
        }

        @Test
        public void test_要素数1ならtrue() {
            assertThat(isSorted(new double[] { 0d }), is(true));
        }

        @Test
        public void test_ソート済みならtrue() {
            assertThat(isSorted(new double[] { 0d, 1d, 2d }), is(true));
        }

        @Test
        public void test_ソート済みでないならfalse() {
            assertThat(isSorted(new double[] { 0d, 2d, 1d }), is(false));
        }
    }

    public static class absMaxのテスト {

        @Test
        public void test_空は0() {
            double[] v = {};
            assertThat(absMax(v), is(0d));
        }

        @Test
        public void test_サイズ1() {
            double[] v = { -2d };
            assertThat(absMax(v), is(2d));
        }

        @Test
        public void test_サイズ2() {
            double[] v = { -2d, -3d };
            assertThat(absMax(v), is(3d));
        }
    }

    public static class averageのテスト {

        @Test
        public void test_空はNaN() {
            double[] v = {};
            assertThat(average(v), is(Double.NaN));
        }

        @Test
        public void test_サイズ1() {
            double[] v = { -2d };
            assertThat(average(v), is(-2d));
        }

        @Test
        public void test_サイズ2() {
            double[] v = { -2d, -3d };
            assertThat(average(v), is(-2.5d));
        }

        @Test
        public void test_巨大数() {
            double[] v = { Double.MAX_VALUE, Double.MAX_VALUE * 0.5 };
            assertThat(average(v), is(Double.MAX_VALUE * 0.75));
        }

        /**
         * {@link DoubleValueUtil#average(double[], double)} をコールするショートカット. <br>
         * 内部で absMax(double[]) を計算する.
         */
        private static double average(double[] v) {
            return DoubleValueUtil.average(v, absMax(v));
        }
    }
}
