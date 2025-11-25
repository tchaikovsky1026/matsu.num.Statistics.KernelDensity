/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.statistics.kerneldensity;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link FilterZeroFillingConvolutionLegacy} のテスト.
 */
@RunWith(Enclosed.class)
@Deprecated
final class FilterZeroFillingConvolutionLegacyTest {

    public static class フィルタの適用のテスト {

        private FilterZeroFillingConvolutionLegacy filterConvolution;

        @Before
        public void before_片側サイズ4のフィルタを生成する() {
            double[] filter = { 1d, 1d / 2, 1d / 4, 1d / 8 };
            filterConvolution = new FilterZeroFillingConvolutionLegacy(filter);
        }

        @Test(expected = IllegalArgumentException.class)
        public void test_サイズが不適の場合はIAEx() {
            filterConvolution.compute(new double[0]);
        }

        @Test
        public void test_サイズ1のシグナルでの畳み込みの検証() {
            double[] signal = { 2 };
            double[] result = { 2 };
            assertThat(filterConvolution.compute(signal), is(result));
        }

        @Test
        public void test_サイズ2のシグナルでの畳み込みの検証() {
            double[] signal = { 1, 2 };
            double[] result = { 2, 2.5 };
            assertThat(filterConvolution.compute(signal), is(result));
        }

        @Test
        public void test_サイズ3のシグナルでの畳み込みの検証() {
            double[] signal = { 1, 2, 3 };
            double[] result = { 2.75, 4, 4.25 };
            assertThat(filterConvolution.compute(signal), is(result));
        }

        @Test
        public void test_サイズ5のシグナルでの畳み込みの検証() {
            double[] signal = { 1, 2, 3, 4, 5 };
            double[] result = { 3.25, 5.625, 7.5, 8.625, 8 };
            assertThat(filterConvolution.compute(signal), is(result));
        }
    }
}
