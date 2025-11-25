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

/**
 * 素朴な実装による, フィルタ畳み込み.
 * 
 * @author Matsuura Y.
 */
final class NaiveFilterZeroFillingConvolution {

    /**
     * 唯一のコンストラクタ.
     */
    NaiveFilterZeroFillingConvolution() {
        super();
    }

    /**
     * 与えたシグナルに対して, フィルタによる畳み込みを適用する. <br>
     * 畳み込みは外部に0埋めして行う.
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
     * {@code filter.length} は 1 以上でなければならない. <br>
     * シグナルサイズは1以上でなければならない.
     * </p>
     * 
     * @param filter フィルタ
     * @param signal シグナル
     * @return 畳み込みの結果
     */
    double[] compute(double[] filter, double[] signal) {
        assert filter.length >= 1;
        assert signal.length >= 1;

        int size = signal.length;

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
