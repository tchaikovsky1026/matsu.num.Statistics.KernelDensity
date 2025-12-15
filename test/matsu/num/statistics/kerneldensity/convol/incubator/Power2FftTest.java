/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package matsu.num.statistics.kerneldensity.convol.incubator;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * {@link Power2Fft} のテスト.
 */
@RunWith(Enclosed.class)
final class Power2FftTest {

    private static final Power2Dft TESTING_CONV = new Power2Fft();

    private static final Power2Dft REFERENCE_CONV = new NaivePower2DftForTesting();

    @RunWith(Theories.class)
    public static class ランダムな信号でテスト {

        @DataPoints
        public static int[] sizes = { 1, 2, 4, 8, 16, 32, 512, 2048 };

        @Theory
        public void test_サイズでパラメータ化テスト_DFT(int size) {
            final int iteration = 10;

            for (int c = 0; c < iteration; c++) {
                double[] re = generateRandomSignal(size);
                double[] im = generateRandomSignal(size);

                double[][] result = TESTING_CONV.dft(new double[][] { re, im });
                double[][] expected = REFERENCE_CONV.dft(new double[][] { re, im });

                double scale = Arrays.stream(expected)
                        .flatMapToDouble((double[] arr) -> Arrays.stream(arr))
                        .map(Math::abs)
                        .max().orElse(1E-200);

                for (int j = 0; j < size; j++) {
                    assertThat(result[0][j], is(closeTo(expected[0][j], scale * 1E-14)));
                    assertThat(result[1][j], is(closeTo(expected[1][j], scale * 1E-14)));
                }
            }
        }

        @Theory
        public void test_サイズでパラメータ化テスト_IDFT(int size) {
            final int iteration = 10;

            for (int c = 0; c < iteration; c++) {
                double[] re = generateRandomSignal(size);
                double[] im = generateRandomSignal(size);

                double[][] result = TESTING_CONV.idft(new double[][] { re, im });
                double[][] expected = REFERENCE_CONV.idft(new double[][] { re, im });

                double scale = Arrays.stream(expected)
                        .flatMapToDouble((double[] arr) -> Arrays.stream(arr))
                        .map(Math::abs)
                        .max().orElse(1E-200);

                for (int j = 0; j < size; j++) {
                    assertThat(result[0][j], is(closeTo(expected[0][j], scale * 1E-14)));
                    assertThat(result[1][j], is(closeTo(expected[1][j], scale * 1E-14)));
                }
            }
        }
    }

    private static double[] generateRandomSignal(int length) {
        return IntStream.range(0, length)
                .mapToDouble(i -> (ThreadLocalRandom.current().nextDouble() - 0.5))
                .toArray();
    }
}
