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

import java.util.Arrays;
import java.util.Objects;

/**
 * フィルタを使用して畳み込みを行うクラス. <br>
 * 範囲外について, 0埋めしたものとして計算する.
 * 
 * @author Matsuura Y.
 */
final class FilterZeroFillingConvolution {

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
    FilterZeroFillingConvolution(double[] filter) {
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
    double[] compute(double[] signal) {
        int size = signal.length;

        if (size == 0) {
            throw new IllegalArgumentException("signal is empty");
        }

        // フィルタのテール部のサイズ分だけ0で拡張する
        int extendSize = filter.length - 1;
        double[] extendSignal = new double[signal.length + extendSize * 2];
        System.arraycopy(signal, 0, extendSignal, extendSize, signal.length);

        // フィルタを両側化し, サイズをextendedSignalに合わせる
        double[] filterForConvolution = Arrays.copyOf(filter, extendSignal.length);
        for (int i = 1; i < filter.length; i++) {
            filterForConvolution[filterForConvolution.length - i] = filter[i];
        }

        double[] conv = new NaiveCyclicConvolution().compute(filterForConvolution, extendSignal);
        return Arrays.copyOfRange(conv, extendSize, conv.length - extendSize);
    }
}
