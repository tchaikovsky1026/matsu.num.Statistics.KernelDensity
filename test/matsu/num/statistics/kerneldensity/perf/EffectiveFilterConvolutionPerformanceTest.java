/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.18
 */
package matsu.num.statistics.kerneldensity.perf;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import matsu.num.statistics.kerneldensity.EffectiveFilterZeroFillingConvolutionPublicWrapper;
import matsu.num.statistics.kerneldensity.NaiveFilterZeroFillingConvolutionPublicWrapper;

/**
 * {@link NaiveFilterZeroFillingConvolutionPublicWrapper}
 * と
 * {@link EffectiveFilterZeroFillingConvolutionPublicWrapper}
 * に関するパフォーマンステスト.
 * 
 * @author Matsuura Y.
 */
final class EffectiveFilterConvolutionPerformanceTest {

    private static final Function<double[], UnaryOperator<double[]>> NAIVE;

    private static final Function<double[], UnaryOperator<double[]>> EFFECTIVE;

    static {
        NaiveFilterZeroFillingConvolutionPublicWrapper naive_conv =
                new NaiveFilterZeroFillingConvolutionPublicWrapper();

        EffectiveFilterZeroFillingConvolutionPublicWrapper effective_conv =
                new EffectiveFilterZeroFillingConvolutionPublicWrapper();

        NAIVE = filter -> (signal -> naive_conv.applyPartial(filter).compute(signal));
        EFFECTIVE = filter -> (signal -> effective_conv.applyPartial(filter).compute(signal));
    }

    public static void main(String[] args) {

        new Exe(NAIVE, "NAIVE").exe();
        new Exe(EFFECTIVE, "EFFECTIVE").exe();
        new Exe(NAIVE, "NAIVE").exe();
        new Exe(EFFECTIVE, "EFFECTIVE").exe();
    }

    private static final class Exe {

        private final String title;
        private final Function<double[], UnaryOperator<double[]>> conv;

        /**
         * 唯一のコンストラクタ. <br>
         * 畳み込み演算を与える.
         */
        Exe(Function<double[], UnaryOperator<double[]>> conv, String title) {
            super();
            this.conv = conv;
            this.title = title;
        }

        void exe() {
            System.out.println(title + ":");

            int filterSize = 100;
            int signalSize = 5000;

            double dummy = 0d;
            int iteration = 10_000;

            double[] filter = IntStream.range(0, filterSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble() - 0.5)
                    .toArray();
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble() - 0.5)
                    .toArray();
            UnaryOperator<double[]> partialConv = conv.apply(filter);

            long startTime = System.nanoTime();
            for (int c = 0; c < iteration; c++) {
                double[] result = partialConv.apply(signal);
                for (double v : result) {
                    dummy += v;
                }
            }
            long endTime = System.nanoTime();
            long nanoTime = endTime - startTime;

            System.out.println(((double) nanoTime / iteration) + " ns");
            System.out.println("dummy: " + dummy);
            System.out.println();
        }
    }
}
