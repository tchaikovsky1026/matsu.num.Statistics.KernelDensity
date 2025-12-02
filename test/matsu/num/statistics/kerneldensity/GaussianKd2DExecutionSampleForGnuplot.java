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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import matsu.num.statistics.kerneldensity.GaussianKd2D.BandWidthRule;
import matsu.num.statistics.kerneldensity.GaussianKd2D.ResolutionRule;

/**
 * {@link GaussianKd2D} の実行のサンプル2.
 * 
 * <p>
 * プロダクトコードには含めない.
 * </p>
 * 
 * @author Matsuura Y.
 */
final class GaussianKd2DExecutionSampleForGnuplot {

    /**
     * gnuplotで描画できる形で出力する. <br>
     * {@code splot filename usi 1:2:3 w l} <br>
     * でプロットできる.
     * 
     * @param args -
     */
    public static void main(String[] args) {
        final int num = 100;
        List<Double> srcXList = new ArrayList<>();
        List<Double> srcYList = new ArrayList<>();
        for (int c = 0; c < num; c++) {
            double x;
            double y;
            if (ThreadLocalRandom.current().nextBoolean()) {
                x = ThreadLocalRandom.current().nextGaussian(1d, 0.5d);
                y = ThreadLocalRandom.current().nextGaussian(1d, 0.25d);
            } else {
                x = ThreadLocalRandom.current().nextGaussian(4d, 1d);
                y = ThreadLocalRandom.current().nextGaussian(2d, 0.25d);
            }
            srcXList.add(x);
            srcYList.add(y);
        }

        double[] srcX = srcXList.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double[] srcY = srcYList.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        Kde2DSourceDto src = new Kde2DSourceDto(srcX.length);
        System.arraycopy(srcX, 0, src.x, 0, src.size);
        System.arraycopy(srcY, 0, src.y, 0, src.size);

        KdeGrid2dDto result = GaussianKd2D.Factory
                .of(BandWidthRule.STANDARD, ResolutionRule.STANDARD)
                .withConvolutionBy(new EffectiveCyclicConvolutionStubForTesting())
                .createOf(src)
                .evaluateIn(Range.of(-1d, 7d), Range.of(0d, 3d));

        // 結果の出力
        System.out.println("#x\ty\tdensity");
        for (int j = 0; j < result.x.length; j++) {
            for (int k = 0; k < result.y.length; k++) {
                System.out.println(
                        "%s\t%s\t%s"
                                .formatted(result.x[j], result.y[k], result.density[j][k]));
            }
            System.out.println();
        }
    }
}
