/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.24
 */
package matsu.num.statistics.kerneldensity;

import java.util.Arrays;
import java.util.stream.IntStream;

import matsu.num.statistics.kerneldensity.GaussianKd1D.BandWidthRule;
import matsu.num.statistics.kerneldensity.GaussianKd1D.ResolutionRule;
import matsu.num.statistics.kerneldensity.conv.CyclicConvolutions;

/**
 * {@link GaussianKd1D} の実行のサンプル.
 * 
 * <p>
 * プロダクトコードには含めない.
 * </p>
 * 
 * @author Matsuura Y.
 */
final class GaussianKd1DExecutionSample {

    public static void main(String[] args) {
        double[] base = {
                1d, 1d, 2d, 3d, 4d, 4d, 5d
        };
        double[] src = IntStream.range(0, 25)
                .mapToObj(i -> base)
                .flatMapToDouble(arr -> Arrays.stream(arr))
                .toArray();

        KdeGrid1dDto result = GaussianKd1D.Factory
                .of(BandWidthRule.STANDARD, ResolutionRule.STANDARD)
                .withConvolutionBy(CyclicConvolutions.fftBased())
                .createOf(src)
                .evaluateIn(Range.of(-100d, 100d));

        System.out.println("x\tdensity\tcum");
        int size = result.size;
        double sum = 0d;
        for (int i = 0; i < size; i++) {
            double x = result.x[i];
            double density = result.density[i];
            System.out.println("%s\t%s\t%s".formatted(x, density, sum));
            sum += density;
        }
    }
}
