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

import matsu.num.statistics.kerneldensity.NaiveFilterZeroFillingConvolutionPublicWrapper;

/**
 * {@link NaiveFilterZeroFillingConvolutionPublicWrapper}
 * に関するパフォーマンステスト.
 * 
 * @author Matsuura Y.
 */
final class NaiveFilterConvolutionPerformanceTest {

    private static final Function<double[], UnaryOperator<double[]>> SEQUENTIAL;

    private static final Function<double[], UnaryOperator<double[]>> PARALLEL;

    static {
        NaiveFilterZeroFillingConvolutionPublicWrapper conv =
                new NaiveFilterZeroFillingConvolutionPublicWrapper();

        SEQUENTIAL = filter -> (signal -> conv.applyPartial(filter).compute(signal, false));
        PARALLEL = filter -> (signal -> conv.applyPartial(filter).compute(signal, true));
    }

    public static void main(String[] args) {

        new Exe(SEQUENTIAL, "SEQUENTIAL").exe();
        new Exe(PARALLEL, "PARALLEL").exe();
        new Exe(SEQUENTIAL, "SEQUENTIAL").exe();
        new Exe(PARALLEL, "PARALLEL").exe();
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
            int signalSize = 500;
            double[] filter = IntStream.range(0, filterSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble())
                    .toArray();
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble())
                    .toArray();

            double dummy = 0d;
            int iteration = 100_000;

            UnaryOperator<double[]> partialConv = conv.apply(filter);

            long startTime = System.nanoTime();
            for (int c = 0; c < iteration; c++) {
                double[] result = partialConv.apply(signal);
                dummy += result[0];
            }
            long endTime = System.nanoTime();
            long nanoTime = endTime - startTime;

            System.out.println(((double) nanoTime / iteration) + " ns");
            System.out.println("dummy: " + dummy);
            System.out.println();
        }
    }
}
