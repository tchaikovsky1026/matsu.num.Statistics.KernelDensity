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

import matsu.num.statistics.kerneldensity.GaussianKd2D.BandWidthRule;
import matsu.num.statistics.kerneldensity.GaussianKd2D.ResolutionRule;

/**
 * {@link GaussianKd2D} のテスト.
 */
@RunWith(Enclosed.class)
final class GaussianKd2DTest {

    @RunWith(Theories.class)
    public static class 正常に実行できるかを確かめる {

        @DataPoints
        public static int[] sizes = {
                1, 2, 3, 10, 100, 1000, 10000
        };

        @Theory
        public void test_カーネル密度推定を実行する(int size) {
            Kde2DSourceDto source = new Kde2DSourceDto(size);
            System.arraycopy(createSource(size), 0, source.x, 0, size);
            System.arraycopy(createSource(size), 0, source.y, 0, size);

            GaussianKd2D.Factory
                    .of(BandWidthRule.STANDARD, ResolutionRule.STANDARD)
                    .withConvolutionBy(new EffectiveCyclicConvolutionStubForTesting())
                    .createOf(source)
                    .evaluateIn(Range.of(-1d, 1d), Range.of(-1d, 1d));
        }

        private static double[] createSource(int size) {
            // 標準正規乱数をソースとする.
            return IntStream.range(0, size)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextGaussian())
                    .toArray();
        }
    }
}
