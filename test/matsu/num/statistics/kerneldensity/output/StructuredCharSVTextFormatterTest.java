/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package matsu.num.statistics.kerneldensity.output;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import matsu.num.statistics.kerneldensity.KdeGrid2dDto;
import matsu.num.statistics.kerneldensity.KdeGrid2dDtoFactoryForTesting;
import matsu.num.statistics.kerneldensity.KernelDensity2D;
import matsu.num.statistics.kerneldensity.Range;

/**
 * {@link StructuredCharSVTextFormatter} のテスト.
 */
@RunWith(Enclosed.class)
final class StructuredCharSVTextFormatterTest {

    public static class InnerIteratorの生成に関するテスト {

        private FormattableKdeResult2D kdeResult;

        @Before
        public void before_KdeResultの生成() {
            double[] x = { 1d, 2d };
            double[] y = { 3d, 4d, 5d };
            double[][] density = {
                    { 11d, 12d, 13d },
                    { 14d, 15d, 16d }
            };
            KdeGrid2dDto dto = KdeGrid2dDtoFactoryForTesting.instanceOf(x, y, density);
            KernelDensity2D kde2d = new KernelDensity2D() {
                @Override
                public KdeGrid2dDto evaluateIn(Range rangeX, Range rangeY) {
                    return dto;
                }
            };

            kdeResult = FormattableKdeResult2D.evaluate(kde2d, null, null);
        }

        @Test(expected = None.class)
        public void test_Innerイテレータは1度は生成できる() {
            Iterable<Iterable<String>> resultCsv = kdeResult
                    .formatted(StructuredCharSVTextFormatter.of(','));

            Iterable<String> inner = resultCsv.iterator().next();

            // innerの一度目のイテレータは生成可能
            // expected = None.class
            inner.iterator().next();
        }

        @Test(expected = IllegalStateException.class)
        public void test_Innerイテレータは2度目は生成できない() {
            Iterable<Iterable<String>> resultCsv;
            Iterable<String> inner;

            try {
                resultCsv = kdeResult
                        .formatted(StructuredCharSVTextFormatter.of(','));
                inner = resultCsv.iterator().next();

                // innerの1度目のイテレータは生成可能
                inner.iterator();
            } catch (Exception e) {
                throw new AssertionError();
            }

            // innerの2度目のイテレータは生成不可
            // expected = IllegalStateException.class
            inner.iterator();
        }

        @Test(expected = None.class)
        public void test_拡張for文の内側のイテレーションを2回できない() {
            Iterable<Iterable<String>> resultCsv = kdeResult
                    .formatted(StructuredCharSVTextFormatter.of(','));

            for (Iterable<String> inner : resultCsv) {
                // 1度目
                for (String s : inner) {
                    // 空イテレーション
                    s.toString();
                }

                try {
                    // 2度目: この拡張for文は実行不可
                    for (String s : inner) {
                        s.toString();
                    }
                    throw new AssertionError("unreachable");
                } catch (IllegalStateException ignore) {
                }
            }
        }
    }

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
        public void test_文字列_空行1_カンマ区切り() {
            Iterable<Iterable<String>> resultCsv = createTextKdeResult2D()
                    .formatted(StructuredCharSVTextFormatter.of(','));

            List<String> resultStr = new ArrayList<>();
            for (Iterable<String> outer : resultCsv) {
                for (String s : outer) {
                    resultStr.add(s);
                }
                resultStr.add("");
            }

            String[] expected = {
                    "1.0,3.0,11.0",
                    "1.0,4.0,12.0",
                    "1.0,5.0,13.0",
                    "",
                    "2.0,3.0,14.0",
                    "2.0,4.0,15.0",
                    "2.0,5.0,16.0",
                    ""
            };
            assertThat(resultStr.toArray(String[]::new), is(expected));
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
        public void test_文字列_空行1_カンマ区切り() {
            Iterable<Iterable<String>> resultCsv = createTextKdeResult2D()
                    .formatted(StructuredCharSVTextFormatter.of(','));

            List<String> resultStr = new ArrayList<>();
            for (Iterable<String> outer : resultCsv) {
                for (String s : outer) {
                    resultStr.add(s);
                }
                resultStr.add("");
            }

            String[] expected = {
                    "1.0,4.0,11.0",
                    "1.0,5.0,12.0",
                    "",
                    "2.0,4.0,13.0",
                    "2.0,5.0,14.0",
                    "",
                    "3.0,4.0,15.0",
                    "3.0,5.0,16.0",
                    ""
            };
            assertThat(resultStr.toArray(String[]::new), is(expected));
        }
    }
}
