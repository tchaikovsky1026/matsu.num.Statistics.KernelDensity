/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package matsu.num.statistics.kerneldensity.output;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import matsu.num.statistics.kerneldensity.KdeGrid2dDto;
import matsu.num.statistics.kerneldensity.KdeGrid2dDtoFactoryForTesting;
import matsu.num.statistics.kerneldensity.KernelDensity2D;
import matsu.num.statistics.kerneldensity.Range;

/**
 * {@link Kde2dCharSVTextFormatter} のテスト.
 */
@RunWith(Enclosed.class)
final class Kde2dCharSVTextFormatterTest {

    public static class フォーマッターのテスト_x2_y3 {

        // kde2dの戻り値
        // DTOの内部を書き換えてはいけない
        private KdeGrid2dDto dto;

        // Rangeにnullを与えても良い
        private KernelDensity2D kde2d;

        @Before
        public void before_DTOの作成() {
            double[] x = { 1d, 2d };
            double[] y = { 3d, 4d, 5d };
            double[][] density = {
                    { 11d, 12d, 13d },
                    { 14d, 15d, 16d }
            };

            dto = KdeGrid2dDtoFactoryForTesting.instanceOf(x, y, density);
        }

        @Before
        public void before_Kde2dの作成() {
            kde2d = new KernelDensity2D() {
                @Override
                public KdeGrid2dDto evaluateIn(Range rangeX, Range rangeY) {
                    return dto;
                }
            };
        }

        private FormattableKdeResult2D createTextKdeResult2D() {
            return FormattableKdeResult2D.evaluate(kde2d, null, null);
        }

        @Test
        public void test_文字列_カンマ区切り() {
            Iterable<String> resultCsv = createTextKdeResult2D()
                    .formatted(Kde2dCharSVTextFormatter.withLabel(','));

            String[] resultStr = StreamSupport.stream(resultCsv.spliterator(), false)
                    .toArray(String[]::new);
            String[] expected = {
                    "x,y,density",
                    "1.0,3.0,11.0",
                    "1.0,4.0,12.0",
                    "1.0,5.0,13.0",
                    "2.0,3.0,14.0",
                    "2.0,4.0,15.0",
                    "2.0,5.0,16.0"
            };
            assertThat(resultStr, is(expected));
        }

        @Test
        public void test_文字列_ラベルレスカンマ区切り() {
            Iterable<String> resultCsv = createTextKdeResult2D()
                    .formatted(Kde2dCharSVTextFormatter.labelless(','));

            String[] resultStr = StreamSupport.stream(resultCsv.spliterator(), false)
                    .toArray(String[]::new);
            String[] expected = {
                    "1.0,3.0,11.0",
                    "1.0,4.0,12.0",
                    "1.0,5.0,13.0",
                    "2.0,3.0,14.0",
                    "2.0,4.0,15.0",
                    "2.0,5.0,16.0"
            };
            assertThat(resultStr, is(expected));
        }
    }

    public static class フォーマッターのテスト_x3_y2 {

        // kde2dの戻り値
        // DTOの内部を書き換えてはいけない
        private KdeGrid2dDto dto;

        // Rangeにnullを与えても良い
        private KernelDensity2D kde2d;

        @Before
        public void before_DTOの作成() {
            double[] x = { 1d, 2d, 3d };
            double[] y = { 4d, 5d };
            double[][] density = {
                    { 11d, 12d, },
                    { 13d, 14d, },
                    { 15d, 16d, }
            };

            dto = KdeGrid2dDtoFactoryForTesting.instanceOf(x, y, density);
        }

        @Before
        public void before_Kde2dの作成() {
            kde2d = new KernelDensity2D() {
                @Override
                public KdeGrid2dDto evaluateIn(Range rangeX, Range rangeY) {
                    return dto;
                }
            };
        }

        private FormattableKdeResult2D createTextKdeResult2D() {
            return FormattableKdeResult2D.evaluate(kde2d, null, null);
        }

        @Test
        public void test_文字列_カンマ区切り() {
            Iterable<String> resultCsv = createTextKdeResult2D()
                    .formatted(Kde2dCharSVTextFormatter.withLabel(','));

            String[] resultStr = StreamSupport.stream(resultCsv.spliterator(), false)
                    .toArray(String[]::new);
            String[] expected = {
                    "x,y,density",
                    "1.0,4.0,11.0",
                    "1.0,5.0,12.0",
                    "2.0,4.0,13.0",
                    "2.0,5.0,14.0",
                    "3.0,4.0,15.0",
                    "3.0,5.0,16.0"
            };
            assertThat(resultStr, is(expected));
        }

        @Test
        public void test_文字列_ラベルレスカンマ区切り() {
            Iterable<String> resultCsv = createTextKdeResult2D()
                    .formatted(Kde2dCharSVTextFormatter.labelless(','));

            String[] resultStr = StreamSupport.stream(resultCsv.spliterator(), false)
                    .toArray(String[]::new);
            String[] expected = {
                    "1.0,4.0,11.0",
                    "1.0,5.0,12.0",
                    "2.0,4.0,13.0",
                    "2.0,5.0,14.0",
                    "3.0,4.0,15.0",
                    "3.0,5.0,16.0"
            };
            assertThat(resultStr, is(expected));
        }
    }
}
