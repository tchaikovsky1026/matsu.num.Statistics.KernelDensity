/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.statistics.kerneldensity;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link NaiveCyclicConvolution} のテスト.
 */
@RunWith(Enclosed.class)
final class NaiveCyclicConvolutionTest {

    public static class 巡回畳み込みのテスト {

        @Test
        public void test_空配列の場合() {
            double[] f = {};
            double[] g = {};

            assertThat(new NaiveCyclicConvolution().compute(f, g), is(new double[] {}));
        }

        @Test
        public void test_テストデータに対して巡回畳み込みを検証する() {
            double[] f = { 1, 2, 3, -1, -2 };
            double[] g = { 3, 2, 1, -1, 4 };
            double[] expected = { 3, 19, 12, -4, -3 };

            assertThat(new NaiveCyclicConvolution().compute(f, g), is(expected));
        }
    }
}
