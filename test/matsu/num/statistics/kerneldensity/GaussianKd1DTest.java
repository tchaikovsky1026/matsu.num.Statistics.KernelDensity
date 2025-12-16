/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package matsu.num.statistics.kerneldensity;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import matsu.num.statistics.kerneldensity.GaussianKd1D.BandWidthRule;
import matsu.num.statistics.kerneldensity.GaussianKd1D.ResolutionRule;
import matsu.num.statistics.kerneldensity.conv.CyclicConvolutions;

/**
 * {@link GaussianKd1D} のテスト.
 */
@RunWith(Enclosed.class)
final class GaussianKd1DTest {

    @RunWith(Theories.class)
    public static class 正常に実行できるかを確かめる {

        @DataPoints
        public static int[] sizes = {
                1, 2, 3, 10, 100, 1000, 10000
        };

        @Theory
        public void test_カーネル密度推定を実行する(int size) {
            double[] source = createSource(size);

            GaussianKd1D.Factory
                    .of(BandWidthRule.STANDARD, ResolutionRule.STANDARD)
                    .withConvolutionBy(CyclicConvolutions.fftBased())
                    .createOf(source)
                    .evaluateIn(Range.of(-5d, 5d));
        }

        private static double[] createSource(int size) {
            // 標準正規乱数をソースとする.
            return IntStream.range(0, size)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextGaussian())
                    .toArray();
        }
    }
}
