/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.16
 */
package matsu.num.statistics.kerneldensity;

/**
 * Gaussian フィルタの生成器.
 * 
 * @author Matsuura Y.
 */
final class GaussianFilterComputation {

    /**
     * ガウシアンフィルタをどの範囲まで計算するかの定数. <br>
     * 4 であれば, 標準偏差の4倍まで計算 (このとき中心の 0.3%).
     */
    private static final double SIZE_COEFF = 4;

    private GaussianFilterComputation() {
        // インスタンス化不可
        throw new AssertionError();
    }

    /**
     * 分解能スケール (ガウス分布の標準偏差を1としてどれくらいのスケールで分割するかの値, 小さい方が細かい)
     * を指定して, フィルタを構築する.
     * 
     * <p>
     * 分解能スケールは 10^(-2) 以上でなければならない. <br>
     * 戻り値は配列 ({@code filter}) として表現し, 片側方向のみを返す. <br>
     * 仕様は以下の通り.
     * </p>
     * 
     * <p>
     * {@code filter[0]} が中心, {@code filter[length - 1]} が端 <br>
     * {@code filter[0] + 2 * (filter[1] + filter[2] + ... + filter[length - 1]) = 1}
     * <br>
     * {@code filter[(int)(1d / resolutionScale)]} がおよそ中心の e^(-1/2) 倍
     * (標準偏差での確率密度).
     * </p>
     * 
     * @param resolutionScale 分解能スケール
     * @return フィルタ
     * @throws IllegalArgumentException resolutionScale が不適の場合 (10^(-2) 以上でない場合)
     */
    static double[] compute(double resolutionScale) {
        if (!(resolutionScale >= 1E-2)) {
            throw new IllegalArgumentException("illegal: resolutionScale = " + resolutionScale);
        }

        // 無限大の場合にNaNが発生するのを回避するため, 有限値に補正
        resolutionScale = Math.min(resolutionScale, Double.MAX_VALUE);

        final int size = 1 + (int) (SIZE_COEFF / resolutionScale);
        final double[] filter = new double[size];

        // filter[0]のみ重複度1なので別に処理する
        filter[0] = 1d;
        double rawTotal = 1d;
        for (int i = 1; i < size; i++) {
            double x = i * resolutionScale;
            double v = Math.exp(-0.5 * x * x);
            filter[i] = v;
            rawTotal += 2 * v;
        }

        final double invRawTotal = 1d / rawTotal;
        for (int i = 0; i < size; i++) {
            filter[i] *= invRawTotal;
        }

        return filter;
    }
}
