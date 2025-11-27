/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.27
 */
package matsu.num.statistics.kerneldensity.incubator;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * {@link NaiveFilterZeroFillingConvolutionIncubator}
 * に関するパフォーマンステスト.
 * 
 * @author Matsuura Y.
 */
final class NaiveFilterConvolutionPerformanceTest {

    private static final NaiveFilterZeroFillingConvolutionIncubator SEQUENTIAL =
            new NaiveFilterZeroFillingConvolutionIncubator(false);

    private static final NaiveFilterZeroFillingConvolutionIncubator PARALLEL =
            new NaiveFilterZeroFillingConvolutionIncubator(true);

    public static void main(String[] args) {

        new Exe(SEQUENTIAL, "SEQUENTIAL").exe();
        new Exe(PARALLEL, "PARALLEL").exe();
        new Exe(SEQUENTIAL, "SEQUENTIAL").exe();
        new Exe(PARALLEL, "PARALLEL").exe();
    }

    private static final class Exe {

        private final String title;
        private final NaiveFilterZeroFillingConvolutionIncubator conv;

        /**
         * 唯一のコンストラクタ. <br>
         * 畳み込み演算を与える.
         */
        Exe(NaiveFilterZeroFillingConvolutionIncubator conv, String title) {
            super();
            this.conv = conv;
            this.title = title;
        }

        void exe() {
            System.out.println(title + ":");

            int filterSize = 100;
            int signalSize = 500;
            double[] filter = IntStream.range(0, filterSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble() - 0.5)
                    .toArray();
            double[] signal = IntStream.range(0, signalSize)
                    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble() - 0.5)
                    .toArray();

            double dummy = 0d;
            int iteration = 100_000;

            long startTime = System.nanoTime();
            for (int c = 0; c < iteration; c++) {
                double[] result = conv.compute(filter, signal);
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
