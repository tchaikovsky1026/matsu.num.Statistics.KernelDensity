/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.30
 */
package matsu.num.statistics.kerneldensity;

import java.util.Arrays;
import java.util.stream.IntStream;

import matsu.num.statistics.kerneldensity.GaussianKd2D.BandWidthRule;
import matsu.num.statistics.kerneldensity.GaussianKd2D.ResolutionRule;
import matsu.num.statistics.kerneldensity.conv.CyclicConvolutions;

/**
 * {@link GaussianKd2D} の実行のサンプル.
 * 
 * <p>
 * プロダクトコードには含めない.
 * </p>
 * 
 * @author Matsuura Y.
 */
final class GaussianKd2DExecutionSample {

    public static void main(String[] args) {
        final double[] baseX = {
                1d, 1d, 2d, 3d, 4d, 4d, 5d
        };
        final double[] baseY = {
                1d, 1d, 1d, 1d, 1d, 1d, 2d
        };
        final int copy = 300;

        double[] srcX = IntStream.range(0, copy)
                .mapToObj(i -> baseX)
                .flatMapToDouble(arr -> Arrays.stream(arr))
                .toArray();
        double[] srcY = IntStream.range(0, copy)
                .mapToObj(i -> baseY)
                .flatMapToDouble(arr -> Arrays.stream(arr))
                .toArray();
        Kde2DSourceDto src = new Kde2DSourceDto(srcX.length);
        System.arraycopy(srcX, 0, src.x, 0, src.size);
        System.arraycopy(srcY, 0, src.y, 0, src.size);

        KdeGrid2dDto result = GaussianKd2D.Factory
                .of(BandWidthRule.STANDARD, ResolutionRule.LOW)
                .withConvolutionBy(CyclicConvolutions.fftBased())
                .createOf(src)
                .evaluateIn(Range.of(-2d, 8d), Range.of(0d, 3d));

        System.out.println("x");
        for (double x : result.x) {
            System.out.println(x);
        }
        System.out.println();

        System.out.println("y");
        for (double y : result.y) {
            System.out.println(y);
        }
        System.out.println();

        // 結果のプロット
        System.out.println("density");
        System.out.println("↓x→y");
        //　ヘッダ: y
        for (double y : result.y) {
            System.out.print("\t");
            System.out.print(y);
        }
        System.out.println();

        for (int j = 0; j < result.x.length; j++) {
            System.out.print(result.x[j]);
            for (double d : result.density[j]) {
                System.out.print("\t");
                System.out.print(d);
            }
            System.out.println();
        }
    }
}
