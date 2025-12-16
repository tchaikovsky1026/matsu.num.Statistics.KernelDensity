/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package matsu.num.statistics.kerneldensity.conv;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import matsu.num.statistics.kerneldensity.EffectiveCyclicConvolution;
import matsu.num.statistics.kerneldensity.EffectiveCyclicConvolutionStubForTesting;

/**
 * {@link Power2DftInjectedCyclicConvolution} のテスト.
 */
@RunWith(Enclosed.class)
final class Power2DftInjectedCyclicConvolutionTest {

    private static final EffectiveCyclicConvolution REFERENCE_CONV =
            new EffectiveCyclicConvolutionStubForTesting();

    @RunWith(Theories.class)
    public static class 受け入れ可能サイズに関するテスト {

        private final EffectiveCyclicConvolution convolution =
                new Power2DftInjectedCyclicConvolution(new NaivePower2DftForTesting());

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

    @SuppressWarnings("exports")
    @RunWith(Theories.class)
    public static class ランダムな信号でテスト_DFTバリエーション {

        /*
         * publicフィールドとメソッドに含まれるPower2Dftが
         * 非公開であることに対する警告抑制.
         */

        /**
         * テストに使うDFTのセット.
         */
        @DataPoints
        public static Set<Power2Dft> testingDfts = testingDfts();

        /**
         * テストするサイズ.
         */
        @DataPoints
        public static int[] sizes = { 1, 2, 4, 8, 16, 32, 512, 2048 };

        @Theory
        public void test_与えたサイズとDFTを用いて畳み込みをテストする(int size, Power2Dft dft) {
            EffectiveCyclicConvolution testingConv =
                    new Power2DftInjectedCyclicConvolution(dft);

            final int iteration = 10;

            for (int c = 0; c < iteration; c++) {
                double[] f = generateRandomSignal(size);
                double[] g = generateRandomSignal(size);

                double[] result = testingConv.apply(f, g);
                double[] expected = REFERENCE_CONV.apply(f, g);

                double scale = Arrays.stream(expected)
                        .map(Math::abs)
                        .max().orElse(1E-200);

                for (int j = 0; j < size; j++) {
                    assertThat(
                            result[j], is(
                                    closeTo(
                                            expected[j],
                                            scale * 1E-15 * Math.min(size, 100))));
                }
            }
        }
    }

    /**
     * 与えた長さのランダムな信号を生成する. <br>
     * 信号の値は -0.5 以上 0.5 未満である
     * (境界値は怪しいかも).
     */
    private static double[] generateRandomSignal(int length) {
        return IntStream.range(0, length)
                .mapToDouble(i -> (ThreadLocalRandom.current().nextDouble() - 0.5))
                .toArray();
    }

    /**
     * このクラスでテストに用いられる {@link Power2Dft} のセットを返す.
     */
    private static Set<Power2Dft> testingDfts() {
        return Set.of(
                new NaivePower2DftForTesting(),
                new Power2Fft());
    }
}
