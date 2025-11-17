/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.17
 */
package matsu.num.statistics.kerneldensity;

import java.util.Arrays;
import java.util.Objects;

/**
 * フィルタを使用して畳み込みを行うクラス.
 * 
 * @author Matsuura Y.
 */
final class FilterConvolution {

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
    FilterConvolution(double[] filter) {
        this.filter = Objects.requireNonNull(filter);

        if (this.filter.length == 0) {
            throw new IllegalArgumentException("filter is empty");
        }
    }

    /**
     * 与えたシグナルに対して, フィルタによる畳み込みを適用する. <br>
     * 畳み込みは巡畳み込みで行う.
     * 
     * <p>
     * {@code signal.length} は {@code 2 * filter.length - 1} 以上でなければならない.
     * </p>
     * 
     * @param signal シグナル
     * @return 畳み込みの結果
     */
    double[] compute(double[] signal) {
        int size = signal.length;

        if (size < 2 * filter.length - 1) {
            throw new IllegalArgumentException("size < 2 * filter.length - 1");
        }
        
        // フィルタを両側化し, サイズをsignalに合わせる
        double[] filterForConvolution = Arrays.copyOf(filter, size);
        for (int i = 1; i < filter.length; i++) {
            filterForConvolution[filterForConvolution.length - i] = filter[i];
        }

        return new NaiveCyclicConvolution().compute(filterForConvolution, signal);
    }
}
