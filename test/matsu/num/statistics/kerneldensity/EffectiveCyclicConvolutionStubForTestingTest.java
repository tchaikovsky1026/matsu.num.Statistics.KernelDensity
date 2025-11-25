/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.statistics.kerneldensity;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * {@link EffectiveCyclicConvolutionStubForTesting} のテスト
 */
@RunWith(Enclosed.class)
final class EffectiveCyclicConvolutionStubForTestingTest {

    @RunWith(Theories.class)
    public static class 受け入れ可能サイズに関するテスト {

        private final EffectiveCyclicConvolution convolution =
                new EffectiveCyclicConvolutionStubForTesting();

        @DataPoints
        public static int[][] argAndExpectedSet = {
                { -1, 1 },
                { 0, 1 },
                { 1, 1 },
                { 2, 2 },
                { 3, 4 },
                { 4, 4 },
                { 5, 8 }
        };

        @Theory
        public void test_calcAcceptableSizeメソッドの引数戻り値ペアのテスト(int[] argAndExpected) {
            int arg = argAndExpected[0];
            int expected = argAndExpected[1];
            assertThat(convolution.calcAcceptableSize(arg), is(expected));
        }
    }

    public static class 巡回畳み込みに関するテスト {

        private final EffectiveCyclicConvolution testingConvolution =
                new EffectiveCyclicConvolutionStubForTesting();

        private final NaiveCyclicConvolutionForTesting validator =
                new NaiveCyclicConvolutionForTesting();

        @Test
        public void test_サイズ1でテスト() {
            // 丸め誤差がない信号
            double[] f = { 0.5 };
            double[] g = { 0.25 };
            assertThat(testingConvolution.apply(f, g), is(validator.compute(f, g)));
        }

        @Test
        public void test_サイズ2でテスト() {
            // 丸め誤差がない信号
            double[] f = { 0.5, 0.125 };
            double[] g = { 0.25, 0.5 };
            assertThat(testingConvolution.apply(f, g), is(validator.compute(f, g)));
        }

        @Test
        public void test_サイズ4でテスト() {
            // 丸め誤差がない信号
            double[] f = { 0.5, 0.125, 0.25, 2 };
            double[] g = { 0.25, 0.5, 1, 0.5 };
            assertThat(testingConvolution.apply(f, g), is(validator.compute(f, g)));
        }

        @Test
        public void test_サイズ8でテスト() {
            // 丸め誤差がない信号
            double[] f = { 0.5, 0.125, 0.25, 2, 2.5, 0.75, 0.125, 0.375 };
            double[] g = { 0.25, 0.5, 1, 0.5, 0.75, 1.25, 2.5, 0.75 };
            assertThat(testingConvolution.apply(f, g), is(validator.compute(f, g)));
        }

    }
}
