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

import java.util.Objects;
import java.util.function.UnaryOperator;

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
        return this.applyPartial(filter).apply(signal);
    }

    /**
     * フィルタを与えて,
     * {@code signal -> (フィルタ畳み込み)}
     * という関数を返す.
     * 
     * <p>
     * 引数はコピーされないので, 書き換えられないことを呼び出しもとで保証すること. <br>
     * 例外スローなどの条件は, {@link #compute(double[], double[])} に従う.
     * </p>
     * 
     */
    UnaryOperator<double[]> applyPartial(double[] filter) {
        if (filter.length == 0) {
            throw new IllegalArgumentException("filter is empty");
        }

        return new PartialApplied(filter);
    }

    /**
     * {@link FilterZeroFillingConvolution#applyPartial(double[])}
     * の戻り値の実装.
     */
    private final class PartialApplied implements UnaryOperator<double[]> {

        private final double[] filter;
        private final NaiveFilterZeroFillingConvolutionParallelizable.PartialApplied naiveConvolutionPartial;
        private final EffectiveFilterZeroFillingConvolution.PartialApplied effectiveConvolutionPartial;

        /**
         * 非公開コンストラクタ.
         * 引数チェックは行われていない.
         */
        PartialApplied(double[] filter) {
            super();

            assert filter.length > 0;
            this.filter = filter;
            this.naiveConvolutionPartial = naiveConvolution.applyPartial(filter);
            this.effectiveConvolutionPartial =
                    Objects.nonNull(effectiveConvolution)
                            ? effectiveConvolution.applyPartial(filter)
                            : null;
        }

        /**
         * filterConvolution(filter, signal)
         * を計算する.
         */
        @Override
        public double[] apply(double[] signal) {
            if (signal.length == 0) {
                throw new IllegalArgumentException("signal is empty");
            }

            if (Objects.nonNull(effectiveConvolutionPartial)
                    && EffectiveFilterZeroFillingConvolution.shouldBeUsed(filter, signal)) {
                return effectiveConvolutionPartial.compute(signal);
            }

            return naiveConvolutionPartial.compute(signal);
        }
    }
}
