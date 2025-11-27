/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.23
 */
package matsu.num.statistics.kerneldensity;

import java.util.Objects;

/**
 * フィルタを使用して畳み込みを行うクラス. <br>
 * 範囲外について, 0埋めしたものとして計算する.
 * 
 * <p>
 * このクラスはテスト用に用意されている.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class FilterZeroFillingConvolutionForTesting {

    private final double[] filter;

    /**
     * フィルタを入れて, 畳み込み器を生成する.
     * 
     * <p>
     * フィルタは片側の値を配列でを与える. <br>
     * 与えた配列に対して, <br>
     * {@code filter[length - 1], ... , filter[1], filter[0] (center), filter[1], ... , filter[length - 1]}
     * <br>
     * となる.
     * </p>
     * 
     * <p>
     * {@code filter.length} は 1 以上でなければならない.
     * </p>
     * 
     * @param filter フィルタ
     * @throws NullPointerException 引数がnullの場合
     */
    public FilterZeroFillingConvolutionForTesting(double[] filter) {
        this.filter = Objects.requireNonNull(filter);

        if (this.filter.length == 0) {
            throw new IllegalArgumentException("filter is empty");
        }
    }

    /**
     * 与えたシグナルに対して, フィルタによる畳み込みを適用する. <br>
     * 畳み込みは外部に0埋めして行う.
     * 
     * <p>
     * シグナルサイズは1以上でなければならない.
     * </p>
     * 
     * @param signal シグナル
     * @return 畳み込みの結果
     */
    public double[] compute(double[] signal) {
        int size = signal.length;

        if (size == 0) {
            throw new IllegalArgumentException("signal is empty");
        }

        // 素朴にフィルタによる畳み込みを実行する
        double[] out = new double[size];
        for (int j = 0; j < signal.length; j++) {
            double v = signal[j];

            out[j] += v * filter[0];
            for (int i = 1, len = Math.min(filter.length, signal.length - j); i < len; i++) {
                out[j + i] += v * filter[i];
            }
            for (int i = 1, len = Math.min(filter.length, j + 1); i < len; i++) {
                out[j - i] += v * filter[i];
            }
        }

        return out;
    }
}
