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

import matsu.num.statistics.kerneldensity.KdeGrid1dDto;
import matsu.num.statistics.kerneldensity.KdeGrid1dDtoFactoryForTesting;
import matsu.num.statistics.kerneldensity.KernelDensity1D;
import matsu.num.statistics.kerneldensity.Range;

/**
 * {@link Kde1dCharSVTextFormatter} のテスト.
 */
@RunWith(Enclosed.class)
final class Kde1dCharSVTextFormatterTest {

    public static class フォーマッターのテスト {

        // kde1dの戻り値
        // DTOの内部を書き換えてはいけない
        private KdeGrid1dDto dto;

        // Rangeにnullを与えても良い
        private KernelDensity1D kde1d;

        @Before
        public void before_DTOの作成() {
            double[] x = { 1d, 2d };
            double[] density = { 3d, 4d };

            dto = KdeGrid1dDtoFactoryForTesting.instanceOf(x, density);
        }

        @Before
        public void before_Kde1dの作成() {
            kde1d = new KernelDensity1D() {

                @Override
                public KdeGrid1dDto evaluateIn(Range range) {
                    return dto;
                }
            };
        }

        private FormattableKdeResult1D createTextKdeResult1D() {
            return FormattableKdeResult1D.evaluate(kde1d, null);
        }

        @Test
        public void test_文字列_カンマ区切り() {
            Iterable<String> resultCsv = createTextKdeResult1D()
                    .formatted(Kde1dCharSVTextFormatter.withLabel(','));

            String[] resultStr = StreamSupport.stream(resultCsv.spliterator(), false)
                    .toArray(String[]::new);
            String[] expected = {
                    "x,density",
                    "1.0,3.0",
                    "2.0,4.0"
            };
            assertThat(resultStr, is(expected));
        }

        @Test
        public void test_文字列_ラベルレスカンマ区切り() {
            Iterable<String> resultCsv = createTextKdeResult1D()
                    .formatted(Kde1dCharSVTextFormatter.labelless(','));

            String[] resultStr = StreamSupport.stream(resultCsv.spliterator(), false)
                    .toArray(String[]::new);
            String[] expected = {
                    "1.0,3.0",
                    "2.0,4.0"
            };
            assertThat(resultStr, is(expected));
        }

        @Test
        public void test_文字列_ラベルエスケープカンマ区切り() {
            Iterable<String> resultCsv = createTextKdeResult1D()
                    .formatted(Kde1dCharSVTextFormatter.withLabelEscaped(',','#'));

            String[] resultStr = StreamSupport.stream(resultCsv.spliterator(), false)
                    .toArray(String[]::new);
            String[] expected = {
                    "#x,density",
                    "1.0,3.0",
                    "2.0,4.0"
            };
            assertThat(resultStr, is(expected));
        }
    }
}
