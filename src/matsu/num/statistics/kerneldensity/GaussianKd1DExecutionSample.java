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

/**
 * {@link GaussianKd1D} の実行のサンプル.
 * 
 * <p>
 * 正式版リリース前に削除される予定.
 * </p>
 * 
 * @author Matsuura Y.
 */
@Deprecated(forRemoval = true)
final class GaussianKd1DExecutionSample {

    public static void main(String[] args) {
        double[] base = {
                1d, 1d, 2d, 3d, 4d, 4d, 5d
        };
        double[] src = IntStream.range(0, 25)
                .mapToObj(i -> base)
                .flatMapToDouble(arr -> Arrays.stream(arr))
                .toArray();

        KdeGrid1dDto result = GaussianKd1D.Factory.of(
                BandWidthRule.SCOTT_RULE, ResolutionRule.HIGH)
                .createOf(src)
                .evaluateIn(Range.of(-10d, 10d));

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
