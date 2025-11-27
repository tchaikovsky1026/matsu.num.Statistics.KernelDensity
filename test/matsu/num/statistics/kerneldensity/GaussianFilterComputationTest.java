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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link GaussianFilterComputation} のテスト.
 */
@RunWith(Enclosed.class)
final class GaussianFilterComputationTest {

    public static final Class<?> TEST_CLASS = GaussianFilterComputation.class;

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

    @Ignore
    public static class フィルタ内容の確認表示 {

        @BeforeClass
        public static void before_printヘッダ() {
            System.out.println(TEST_CLASS.getName() + ":");
        }

        @Test
        public void test_分解能_0_25_フィルタ() {
            double resolutionScale = 0.25;
            double[] filter = compute(resolutionScale);
            System.out.println(
                    "resolutionScale = %s, filter.length = %s".formatted(
                            resolutionScale, filter.length));
        }

        @Test
        public void test_分解能_0_1_フィルタ() {
            double resolutionScale = 0.1;
            double[] filter = compute(resolutionScale);
            System.out.println(
                    "resolutionScale = %s, filter.length = %s".formatted(
                            resolutionScale, filter.length));
        }

        @AfterClass
        public static void after_printフッタ() {
            System.out.println();
        }
    }
}
