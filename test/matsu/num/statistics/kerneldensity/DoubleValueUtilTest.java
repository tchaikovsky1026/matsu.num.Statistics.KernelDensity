/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.statistics.kerneldensity;

import static matsu.num.statistics.kerneldensity.DoubleValueUtil.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link DoubleValueUtil} のテスト.
 */
@RunWith(Enclosed.class)
final class DoubleValueUtilTest {

    public static class isSortedのテスト {

        @Test
        public void test_空ならtrue() {
            assertThat(isSorted(new double[] {}), is(true));
        }

        @Test
        public void test_要素数1ならtrue() {
            assertThat(isSorted(new double[] { 0d }), is(true));
        }

        @Test
        public void test_ソート済みならtrue() {
            assertThat(isSorted(new double[] { 0d, 1d, 2d }), is(true));
        }

        @Test
        public void test_ソート済みでないならfalse() {
            assertThat(isSorted(new double[] { 0d, 2d, 1d }), is(false));
        }
    }
}
