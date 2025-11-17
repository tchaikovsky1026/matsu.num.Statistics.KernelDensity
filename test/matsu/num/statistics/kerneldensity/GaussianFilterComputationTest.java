/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.10.15
 */
package matsu.num.statistics.kerneldensity;

import static matsu.num.statistics.kerneldensity.GaussianFilterComputation.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link GaussianFilterComputation} のテスト.
 */
@RunWith(Enclosed.class)
final class GaussianFilterComputationTest {

    public static class フィルタ計算のテスト {

        @Test
        public void test_大きなスケールを与えた場合はサイズ1になる() {
            assertThat(compute(Double.POSITIVE_INFINITY).length, is(1));
        }

        @Test
        public void test_合計が1に規格化されている() {
            double[] filter = compute(0.5d);
            double sumTotal = 2 * Arrays.stream(filter).sum() - filter[0];

            assertThat(sumTotal, is(closeTo(1d, 1E-15)));
        }
    }
}
