/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package matsu.num.statistics.kerneldensity.convol.incubator;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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

    private static final EffectiveCyclicConvolution TESTING_CONV =
            new Power2DftInjectedCyclicConvolution(new NaivePower2DftForTesting());

    private static final EffectiveCyclicConvolution REFERENCE_CONV =
            new EffectiveCyclicConvolutionStubForTesting();

    @RunWith(Theories.class)
    public static class ランダムな信号でテスト {

        @DataPoints
        public static int[] sizes = { 1, 2, 4, 8, 16, 32, 512 };

        @Theory
        public void test_サイズ8(int size) {
            final int iteration = 10;

            for (int c = 0; c < iteration; c++) {
                double[] f = generateRandomSignal(size);
                double[] g = generateRandomSignal(size);

                double[] result = TESTING_CONV.apply(f, g);
                double[] expected = REFERENCE_CONV.apply(f, g);

                for (int j = 0; j < size; j++) {
                    assertThat(result[j], is(closeTo(expected[j], 1E-12)));
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
