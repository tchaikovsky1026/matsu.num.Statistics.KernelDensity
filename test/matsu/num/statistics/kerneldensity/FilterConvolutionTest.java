/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.statistics.kerneldensity;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link FilterConvolution} のテスト.
 */
@RunWith(Enclosed.class)
final class FilterConvolutionTest {

    public static class フィルタの適用のテスト {

        private FilterConvolution filterConvolution;

        @Before
        public void before_片側サイズ4のフィルタを生成する() {
            double[] filter = { 1d, 1d / 2, 1d / 4, 1d / 8 };
            filterConvolution = new FilterConvolution(filter);
        }

        @Test(expected = IllegalArgumentException.class)
        public void test_サイズが不適の場合はIAEx() {
            // サイズ7未満のシグナルはフィルタサイズより小さいため不適
            filterConvolution.compute(new double[6]);
        }

        @Test
        public void test_フィルタがジャストサイズのシグナルでの畳み込みの検証() {
            double[] signal = { 1, 2, 3, 4, 5, 6, 7 };
            double[] result = { 8.875, 8.125, 9.125, 11, 12.875, 13.875, 13.125 };
            assertThat(filterConvolution.compute(signal), is(result));
        }

        @Test
        public void test_フィルタより大きいサイズのシグナルでの畳み込みの検証() {
            double[] signal = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            double[] result = { 11.5, 9.25, 9.5, 11, 13.75, 16.5, 19.25, 20.75, 21, 18.75 };
            assertThat(filterConvolution.compute(signal), is(result));
        }
    }
}
