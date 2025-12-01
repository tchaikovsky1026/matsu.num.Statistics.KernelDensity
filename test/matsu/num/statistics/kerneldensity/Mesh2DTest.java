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
 * {@link Mesh2D} のテスト.
 */
@RunWith(Enclosed.class)
final class Mesh2DTest {

    public static class メッシュ計算のテスト {

        private final Range rangeX = Range.of(-1d, 2d);
        private final Range rangeY = Range.of(-1d, 1d);
        private final double resolutionX = 0.5;
        private final double resolutionY = 0.25;
        private final int extSizeX = 1;
        private final int extSizeY = 2;

        @Test
        public void test_xyのテスト() {
            Kde2DSourceDto source = new Kde2DSourceDto(1);
            source.x[0] = 0.5d;
            source.y[0] = 0.5d;

            Mesh2D mesh2d = new Mesh2D(
                    rangeX, rangeY, resolutionX, resolutionY, extSizeX, extSizeY, source);

            assertThat(mesh2d.x.length, is(7));
            assertThat(mesh2d.x[0], is(-1d));
            assertThat(mesh2d.x[1], is(-0.5d));
            assertThat(mesh2d.x[2], is(0d));
            assertThat(mesh2d.x[3], is(0.5d));
            assertThat(mesh2d.x[4], is(1d));
            assertThat(mesh2d.x[5], is(1.5d));
            assertThat(mesh2d.x[6], is(2d));

            assertThat(mesh2d.y.length, is(9));
            assertThat(mesh2d.y[0], is(-1d));
            assertThat(mesh2d.y[1], is(-0.75d));
            assertThat(mesh2d.y[2], is(-0.5d));
            assertThat(mesh2d.y[3], is(-0.25d));
            assertThat(mesh2d.y[4], is(0d));
            assertThat(mesh2d.y[5], is(0.25d));
            assertThat(mesh2d.y[6], is(0.5d));
            assertThat(mesh2d.y[7], is(0.75d));
            assertThat(mesh2d.y[8], is(1d));
        }

        @Test
        public void test_extendXYのテスト() {
            Kde2DSourceDto source = new Kde2DSourceDto(1);
            source.x[0] = 0.5d;
            source.y[0] = 0.5d;

            Mesh2D mesh2d = new Mesh2D(
                    rangeX, rangeY, resolutionX, resolutionY, extSizeX, extSizeY, source);

            assertThat(mesh2d.extendX.length, is(9));
            assertThat(mesh2d.extendX[0], is(-1.5d));
            assertThat(mesh2d.extendX[1], is(-1d));
            assertThat(mesh2d.extendX[2], is(-0.5d));
            assertThat(mesh2d.extendX[3], is(0d));
            assertThat(mesh2d.extendX[4], is(0.5d));
            assertThat(mesh2d.extendX[5], is(1d));
            assertThat(mesh2d.extendX[6], is(1.5d));
            assertThat(mesh2d.extendX[7], is(2d));
            assertThat(mesh2d.extendX[8], is(2.5d));

            assertThat(mesh2d.extendY.length, is(13));
            assertThat(mesh2d.extendY[0], is(-1.5d));
            assertThat(mesh2d.extendY[1], is(-1.25d));
            assertThat(mesh2d.extendY[2], is(-1d));
            assertThat(mesh2d.extendY[3], is(-0.75d));
            assertThat(mesh2d.extendY[4], is(-0.5d));
            assertThat(mesh2d.extendY[5], is(-0.25d));
            assertThat(mesh2d.extendY[6], is(0d));
            assertThat(mesh2d.extendY[7], is(0.25d));
            assertThat(mesh2d.extendY[8], is(0.5d));
            assertThat(mesh2d.extendY[9], is(0.75d));
            assertThat(mesh2d.extendY[10], is(1d));
            assertThat(mesh2d.extendY[11], is(1.25d));
            assertThat(mesh2d.extendY[12], is(1.5d));
        }

        @Test
        public void test_weightのテスト() {

            // データ数2のソースを重複させて構築
            Kde2DSourceDto source = new Kde2DSourceDto(4);
            source.x[0] = 0.5d;
            source.y[0] = 0.25d;
            source.x[1] = 1.125d;
            source.y[1] = 0.125d;
            source.x[2] = 0.5d;
            source.y[2] = 0.25d;
            source.x[3] = 1.125d;
            source.y[3] = 0.125d;

            Mesh2D mesh2d = new Mesh2D(
                    rangeX, rangeY, resolutionX, resolutionY, extSizeX, extSizeY, source);

            /*
             * weightの期待値を構築.
             * データ数2より, 各データの重みは0.5
             * 
             * No.1
             * (0.5, 0.25) -> [4.0][7.0]
             * -> [4][7]: 0.5 * 1 * 1
             * 
             * N0. 2
             * (1.125, 0.125) -> [5.25][6.5]
             * ->
             * [5][6]: 0.5 * 0.75 * 0.5
             * [5][7]: 0.5 * 0.75 * 0.5
             * [6][6]: 0.5 * 0.25 * 0.5
             * [6][7]: 0.5 * 0.25 * 0.5
             */
            double[][] expectedWeight = new double[9][13];
            // [5.0][7.0]
            expectedWeight[4][7] = 0.5 * 1 * 1;
            expectedWeight[5][6] = 0.5 * 0.75 * 0.5;
            expectedWeight[5][7] = 0.5 * 0.75 * 0.5;
            expectedWeight[6][6] = 0.5 * 0.25 * 0.5;
            expectedWeight[6][7] = 0.5 * 0.25 * 0.5;

            assertThat(mesh2d.weight, is(expectedWeight));
        }
    }

    public static class reduceSizeのテスト {

        private final Range rangeX = Range.of(0d, 1d);
        private final Range rangeY = Range.of(0d, 1d);
        private final double resolutionX = 0.5;
        private final double resolutionY = 0.25;
        private final int extSizeX = 1;
        private final int extSizeY = 2;

        private Mesh2D mesh2d;

        @Before
        public void before_メッシュの生成() {
            Kde2DSourceDto source = new Kde2DSourceDto(1);
            source.x[0] = 0.5d;
            source.y[0] = 0.5d;

            mesh2d = new Mesh2D(
                    rangeX, rangeY, resolutionX, resolutionY, extSizeX, extSizeY, source);

            assertThat(mesh2d.x.length, is(3));
            assertThat(mesh2d.y.length, is(5));
        }

        @Test(expected = IllegalArgumentException.class)
        public void test_reduceSizeのテスト_xのサイズ違いは受け入れ不可() {
            double[][] reducedSrc = new double[mesh2d.extendX.length - 1][mesh2d.extendY.length];

            mesh2d.reduceSize(reducedSrc);
        }

        @Test(expected = IllegalArgumentException.class)
        public void test_reduceSizeのテスト_yのサイズ違いは受け入れ不可() {
            double[][] reducedSrc = new double[mesh2d.extendX.length][mesh2d.extendY.length - 1];

            mesh2d.reduceSize(reducedSrc);
        }

        @Test
        public void test_reduceSizeのテスト() {

            /*
             * xy:(3, 5) -> extXY:(5, 9)
             */
            double[][] reducedSrc = {
                    { 0, 1, 2, 3, 4, 5, 6, 7, 8 },
                    { 10, 11, 12, 13, 14, 15, 16, 17, 18 },
                    { 20, 21, 22, 23, 24, 25, 26, 27, 28 },
                    { 30, 31, 32, 33, 34, 35, 36, 37, 38 },
                    { 40, 41, 42, 43, 44, 45, 46, 47, 48 },
            };
            double[][] expected = {
                    { 12, 13, 14, 15, 16 },
                    { 22, 23, 24, 25, 26 },
                    { 32, 33, 34, 35, 36 }
            };

            assertThat(
                    mesh2d.reduceSize(reducedSrc), is(expected));
        }
    }
}
