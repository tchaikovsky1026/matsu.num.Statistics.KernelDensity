/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.27
 */
package matsu.num.statistics.kerneldensity;

import java.util.Objects;

/**
 * フィルタを使用して畳み込みを行うクラス. <br>
 * 範囲外について, 0埋めしたものとして計算する.
 * 
 * @author Matsuura Y.
 */
final class FilterZeroFillingConvolution {

    private final NaiveFilterZeroFillingConvolutionParallelizable naiveConvolution;
    private final EffectiveFilterZeroFillingConvolution effectiveConvolution;

    /**
     * {@link EffectiveCyclicConvolution}
     * を与えて, 効率的な0埋め畳み込みを構築する.
     * 
     * <p>
     * {@code null} を与えても良い. <br>
     * この場合, 計算効率は低下する.
     * </p>
     * 
     * @param cyclicConvolution nullも許容される
     */
    FilterZeroFillingConvolution(EffectiveCyclicConvolution cyclicConvolution) {
        super();
        this.naiveConvolution = new NaiveFilterZeroFillingConvolutionParallelizable();
        this.effectiveConvolution =
                Objects.isNull(cyclicConvolution)
                        ? null
                        : new EffectiveFilterZeroFillingConvolution(cyclicConvolution);
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
     * {@code filter.length}, {@code signal.length} は1以上でなければならない.
     * </p>
     * 
     * @param filter フィルタ
     * @param signal シグナル
     * @return 畳み込みの結果
     * @throws IllegalArgumentException filter または signal が長さ0の場合
     * @throws NullPointerException 引数がnullの場合
     */
    double[] compute(double[] filter, double[] signal) {

        if (filter.length == 0) {
            throw new IllegalArgumentException("filter is empty");
        }

        if (signal.length == 0) {
            throw new IllegalArgumentException("signal is empty");
        }

        if (Objects.nonNull(effectiveConvolution)
                && effectiveConvolution.shouldBeUsed(filter, signal)) {
            return effectiveConvolution.compute(filter, signal);
        }

        return naiveConvolution.compute(filter, signal);
    }

}
