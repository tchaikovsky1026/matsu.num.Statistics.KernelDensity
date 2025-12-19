/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package matsu.num.statistics.kerneldensity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * {@link NaiveFilterZeroFillingConvolutionParallelizable} のテスト.
 */
@RunWith(Enclosed.class)
final class NaiveFilterZeroFillingConvolutionParallelizableTest {

    private static final FilterZeroFillingConvolution TESTING_CONVOLUTION =
            NaiveFilterZeroFillingConvolutionParallelizable.instance();

    private static final Function<double[], FilterZeroFillingConvolutionForTesting> VALIDATOR =
            filter -> new FilterZeroFillingConvolutionForTesting(filter);

    @RunWith(Theories.class)
    public static class フィルタサイズ5での網羅的テスト {

        private final double[] filter = {
                1, 0.5, 0.25, 0.125, 0.0625
        };

        @DataPoints
        public static int[] signalSizes;

        @BeforeClass
        public static void before_シグナルサイズのリストを作成する() {
            ArrayList<Integer> sizeList = new ArrayList<>(
                    IntStream.range(1, 100)
                            .mapToObj(Integer::valueOf)
                            .toList());

            // IntRangeSpliteratorのtrySplitが作動するように特殊ケースを追加
            sizeList.addAll(
                    Arrays.stream(new int[] { 1000, 1200, 4000 })
                            .mapToObj(Integer::valueOf)
                            .toList());

            signalSizes = sizeList.stream()
                    .mapToInt(Integer::intValue)
                    .toArray();
        }

        @Theory
        public void test_畳み込みの検証(int signalSize) {
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble())
                    .toArray();

            double[] result = TESTING_CONVOLUTION.applyPartial(filter).compute(signal, false);
            double[] expected = VALIDATOR.apply(filter).compute(signal);
            assertThat(result.length, is(expected.length));

            double[] res = expected.clone();
            for (int i = 0; i < res.length; i++) {
                res[i] -= result[i];
            }

            assertThat(DoubleValueUtil.absMax(res), is(lessThan(1E-13)));
        }
    }

    @RunWith(Theories.class)
    public static class フィルタサイズ5での網羅的テスト_並列 {

        private final double[] filter = {
                1, 0.5, 0.25, 0.125, 0.0625
        };

        @DataPoints
        public static int[] signalSizes;

        @BeforeClass
        public static void before_シグナルサイズのリストを作成する() {
            ArrayList<Integer> sizeList = new ArrayList<>(
                    IntStream.range(1, 100)
                            .mapToObj(Integer::valueOf)
                            .toList());

            // IntRangeSpliteratorのtrySplitが作動するように特殊ケースを追加
            sizeList.addAll(
                    Arrays.stream(new int[] { 1000, 1200, 4000 })
                            .mapToObj(Integer::valueOf)
                            .toList());

            signalSizes = sizeList.stream()
                    .mapToInt(Integer::intValue)
                    .toArray();
        }

        @Theory
        public void test_畳み込みの検証(int signalSize) {
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble())
                    .toArray();

            double[] result = TESTING_CONVOLUTION.applyPartial(filter).compute(signal, true);
            double[] expected = VALIDATOR.apply(filter).compute(signal);
            assertThat(result.length, is(expected.length));

            double[] res = expected.clone();
            for (int i = 0; i < res.length; i++) {
                res[i] -= result[i];
            }

            assertThat(DoubleValueUtil.absMax(res), is(lessThan(1E-13)));
        }
    }

    @RunWith(Theories.class)
    public static class フィルタサイズ7での網羅的テスト {

        private final double[] filter = {
                1, 0.5, 0.25, 0.125, 0.0625, 0.25, 0.5
        };

        @DataPoints
        public static int[] signalSizes;

        @BeforeClass
        public static void before_シグナルサイズのリストを作成する() {
            ArrayList<Integer> sizeList = new ArrayList<>(
                    IntStream.range(1, 100)
                            .mapToObj(Integer::valueOf)
                            .toList());

            // IntRangeSpliteratorのtrySplitが作動するように特殊ケースを追加
            sizeList.addAll(
                    Arrays.stream(new int[] { 1000, 1200, 4000 })
                            .mapToObj(Integer::valueOf)
                            .toList());

            signalSizes = sizeList.stream()
                    .mapToInt(Integer::intValue)
                    .toArray();
        }

        @Theory
        public void test_畳み込みの検証(int signalSize) {
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble())
                    .toArray();

            double[] result = TESTING_CONVOLUTION.applyPartial(filter).compute(signal, false);
            double[] expected = VALIDATOR.apply(filter).compute(signal);
            assertThat(result.length, is(expected.length));

            double[] res = expected.clone();
            for (int i = 0; i < res.length; i++) {
                res[i] -= result[i];
            }

            assertThat(DoubleValueUtil.absMax(res), is(lessThan(1E-13)));
        }
    }

    @RunWith(Theories.class)
    public static class フィルタサイズ7での網羅的テスト_並列 {

        private final double[] filter = {
                1, 0.5, 0.25, 0.125, 0.0625, 0.25, 0.5
        };

        @DataPoints
        public static int[] signalSizes;

        @BeforeClass
        public static void before_シグナルサイズのリストを作成する() {
            ArrayList<Integer> sizeList = new ArrayList<>(
                    IntStream.range(1, 100)
                            .mapToObj(Integer::valueOf)
                            .toList());

            // IntRangeSpliteratorのtrySplitが作動するように特殊ケースを追加
            sizeList.addAll(
                    Arrays.stream(new int[] { 1000, 1200, 4000 })
                            .mapToObj(Integer::valueOf)
                            .toList());

            signalSizes = sizeList.stream()
                    .mapToInt(Integer::intValue)
                    .toArray();
        }

        @Theory
        public void test_畳み込みの検証(int signalSize) {
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble())
                    .toArray();

            double[] result = TESTING_CONVOLUTION.applyPartial(filter).compute(signal, true);
            double[] expected = VALIDATOR.apply(filter).compute(signal);
            assertThat(result.length, is(expected.length));

            double[] res = expected.clone();
            for (int i = 0; i < res.length; i++) {
                res[i] -= result[i];
            }

            assertThat(DoubleValueUtil.absMax(res), is(lessThan(1E-13)));
        }
    }
}
