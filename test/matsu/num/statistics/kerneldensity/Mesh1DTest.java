/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.statistics.kerneldensity;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link Mesh1D} のテスト.
 */
@RunWith(Enclosed.class)
final class Mesh1DTest {

    public static class メッシュ計算のテスト {

        private final Range range = Range.of(-1d, 2d);
        private final double resolution = 0.5;;
        private final int extSize = 2;

        @Test
        public void test_xのテスト() {
            double[] source = { 0.5d };
            Mesh1D mesh1d = new Mesh1D(range, resolution, extSize, source);

            assertThat(mesh1d.x.length, is(7));
            assertThat(mesh1d.x[0], is(-1d));
            assertThat(mesh1d.x[1], is(-0.5d));
            assertThat(mesh1d.x[2], is(0d));
            assertThat(mesh1d.x[3], is(0.5d));
            assertThat(mesh1d.x[4], is(1d));
            assertThat(mesh1d.x[5], is(1.5d));
            assertThat(mesh1d.x[6], is(2d));
        }

        @Test
        public void test_extendXのテスト() {
            double[] source = { 0.5d };
            Mesh1D mesh1d = new Mesh1D(range, resolution, extSize, source);

            assertThat(mesh1d.extendX.length, is(11));
            assertThat(mesh1d.extendX[0], is(-2d));
            assertThat(mesh1d.extendX[1], is(-1.5d));
            assertThat(mesh1d.extendX[2], is(-1d));
            assertThat(mesh1d.extendX[3], is(-0.5d));
            assertThat(mesh1d.extendX[4], is(0d));
            assertThat(mesh1d.extendX[5], is(0.5d));
            assertThat(mesh1d.extendX[6], is(1d));
            assertThat(mesh1d.extendX[7], is(1.5d));
            assertThat(mesh1d.extendX[8], is(2d));
            assertThat(mesh1d.extendX[9], is(2.5d));
            assertThat(mesh1d.extendX[10], is(3d));
        }

        @Test
        public void test_weightのテスト() {
            double[] source = { -2.25d, 1.125d };
            Mesh1D mesh1d = new Mesh1D(range, resolution, extSize, source);

            assertThat(mesh1d.weight.length, is(11));
            assertThat(mesh1d.weight[0], is(0.25d));
            assertThat(mesh1d.weight[1], is(0d));
            assertThat(mesh1d.weight[2], is(0d));
            assertThat(mesh1d.weight[3], is(0d));
            assertThat(mesh1d.weight[4], is(0d));
            assertThat(mesh1d.weight[5], is(0d));
            assertThat(mesh1d.weight[6], is(0.375d));
            assertThat(mesh1d.weight[7], is(0.125d));
            assertThat(mesh1d.weight[8], is(0d));
            assertThat(mesh1d.weight[9], is(0d));
            assertThat(mesh1d.weight[10], is(0d));
        }
    }

    public static class reduceSizeのテスト {

        private final Range range = Range.of(-1d, 2d);
        private final double resolution = 0.5;;
        private final int extSize = 2;
        private final double[] source = { 0.5d };

        private Mesh1D mesh1d;

        @Before
        public void before_メッシュの生成() {
            mesh1d = new Mesh1D(range, resolution, extSize, source);
            assertThat(mesh1d.x.length, is(7));
        }

        @Test(expected = IllegalArgumentException.class)
        public void test_reduceSizeのテスト_サイズ違いは受け入れ不可() {
            double[] reducedSrc = new double[mesh1d.extendX.length - 1];

            mesh1d.reduceSize(reducedSrc);
        }

        @Test
        public void test_reduceSizeのテスト() {
            double[] reducedSrc = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

            assertThat(
                    mesh1d.reduceSize(reducedSrc), is(
                            new double[] { 3, 4, 5, 6, 7, 8, 9 }));
        }
    }
}
