/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.statistics.kerneldensity;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import matsu.num.statistics.kerneldensity.conv.CyclicConvolutions;

/**
 * {@link EffectiveFilterZeroFillingConvolution} のテスト.
 */
@RunWith(Enclosed.class)
final class EffectiveFilterZeroFillingConvolutionTest {

    /**
     * テストされるべき巡回畳み込み. <br>
     * (依存関係がおかしいので, 本来はここにあるべきではないと思われる.)
     */
    private static final EffectiveCyclicConvolution[] TESTING_CYCLIC_CONV = {
            new EffectiveCyclicConvolutionStubForTesting(),
            CyclicConvolutions.fftBased()
    };

    private static final Function<double[], FilterZeroFillingConvolutionForTesting> VALIDATOR =
            filter -> new FilterZeroFillingConvolutionForTesting(filter);

    @RunWith(Theories.class)
    public static class フィルタサイズ5での網羅的テスト {

        private final double[] filter = {
                1, 0.5, 0.25, 0.125, 0.0625
        };

        @DataPoints
        public static EffectiveCyclicConvolution[] cyclicConv = TESTING_CYCLIC_CONV;

        @DataPoints
        public static int[] signalSizes;

        @BeforeClass
        public static void before_シグナルサイズのリストを作成する() {
            // 畳み込み区間のtupleが複数区間になるようにシグナルサイズの範囲を決める
            signalSizes = IntStream.range(1, 200).toArray();
        }

        @Theory
        public void test_畳み込みの検証(int signalSize, EffectiveCyclicConvolution cyclicConv) {
            FilterZeroFillingConvolution testingFIlterConv =
                    EffectiveFilterZeroFillingConvolution.instanceOf(cyclicConv);

            // 巡回畳み込みで負の数が出やすいように, 意図的に0を混ぜる
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> {
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            return 0d;
                        }
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            return 0d;
                        }

                        return ThreadLocalRandom.current().nextDouble();
                    })
                    .toArray();

            double[] result = testingFIlterConv.applyPartial(filter).compute(signal, false);
            double[] expected = VALIDATOR.apply(filter).compute(signal);

            assertThat(result.length, is(expected.length));
            for (double v : result) {
                assertThat(v, is(greaterThanOrEqualTo(0d)));
            }

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
        public static EffectiveCyclicConvolution[] cyclicConv = TESTING_CYCLIC_CONV;

        @DataPoints
        public static int[] signalSizes;

        @BeforeClass
        public static void before_シグナルサイズのリストを作成する() {
            // 畳み込み区間のtupleが複数区間になるようにシグナルサイズの範囲を決める
            signalSizes = IntStream.range(1, 200).toArray();
        }

        @Theory
        public void test_畳み込みの検証(int signalSize, EffectiveCyclicConvolution cyclicConv) {
            FilterZeroFillingConvolution testingFIlterConv =
                    EffectiveFilterZeroFillingConvolution.instanceOf(cyclicConv);

            // 巡回畳み込みで負の数が出やすいように, 意図的に0を混ぜる
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> {
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            return 0d;
                        }
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            return 0d;
                        }

                        return ThreadLocalRandom.current().nextDouble();
                    })
                    .toArray();

            double[] result = testingFIlterConv.applyPartial(filter).compute(signal, true);
            double[] expected = VALIDATOR.apply(filter).compute(signal);

            assertThat(result.length, is(expected.length));
            for (double v : result) {
                assertThat(v, is(greaterThanOrEqualTo(0d)));
            }

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
        public static EffectiveCyclicConvolution[] cyclicConv = TESTING_CYCLIC_CONV;

        @DataPoints
        public static int[] signalSizes;

        @BeforeClass
        public static void before_シグナルサイズのリストを作成する() {
            // 畳み込み区間のtupleが複数区間になるようにシグナルサイズの範囲を決める
            signalSizes = IntStream.range(1, 200).toArray();
        }

        @Theory
        public void test_畳み込みの検証(int signalSize, EffectiveCyclicConvolution cyclicConv) {
            FilterZeroFillingConvolution testingFIlterConv =
                    EffectiveFilterZeroFillingConvolution.instanceOf(cyclicConv);

            // 巡回畳み込みで負の数が出やすいように, 意図的に0を混ぜる
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> {
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            return 0d;
                        }
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            return 0d;
                        }

                        return ThreadLocalRandom.current().nextDouble();
                    })
                    .toArray();

            double[] result = testingFIlterConv.applyPartial(filter).compute(signal, false);
            double[] expected = VALIDATOR.apply(filter).compute(signal);

            assertThat(result.length, is(expected.length));
            for (double v : result) {
                assertThat(v, is(greaterThanOrEqualTo(0d)));
            }

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

        @DataPoints
        public static EffectiveCyclicConvolution[] cyclicConv = TESTING_CYCLIC_CONV;

        @BeforeClass
        public static void before_シグナルサイズのリストを作成する() {
            // 畳み込み区間のtupleが複数区間になるようにシグナルサイズの範囲を決める
            signalSizes = IntStream.range(1, 200).toArray();
        }

        @Theory
        public void test_畳み込みの検証(int signalSize, EffectiveCyclicConvolution cyclicConv) {
            FilterZeroFillingConvolution testingFIlterConv =
                    EffectiveFilterZeroFillingConvolution.instanceOf(cyclicConv);

            // 巡回畳み込みで負の数が出やすいように, 意図的に0を混ぜる
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> {
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            return 0d;
                        }
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            return 0d;
                        }

                        return ThreadLocalRandom.current().nextDouble();
                    })
                    .toArray();

            double[] result = testingFIlterConv.applyPartial(filter).compute(signal, true);
            double[] expected = VALIDATOR.apply(filter).compute(signal);

            assertThat(result.length, is(expected.length));
            for (double v : result) {
                assertThat(v, is(greaterThanOrEqualTo(0d)));
            }

            double[] res = expected.clone();
            for (int i = 0; i < res.length; i++) {
                res[i] -= result[i];
            }

            assertThat(DoubleValueUtil.absMax(res), is(lessThan(1E-13)));
        }
    }
}
