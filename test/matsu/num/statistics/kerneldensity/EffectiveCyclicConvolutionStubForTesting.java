/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.25
 */
package matsu.num.statistics.kerneldensity;

import java.util.function.Function;

/**
 * {@link EffectiveCyclicConvolution} のスタブ.
 * 
 * <p>
 * このクラスはテストのために用意したものであり, "効率的"な実装になっていない.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class EffectiveCyclicConvolutionStubForTesting implements EffectiveCyclicConvolution {

    /**
     * 唯一のコンストラクタ.
     */
    public EffectiveCyclicConvolutionStubForTesting() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * {@link EffectiveCyclicConvolutionStubForTesting}
     * では, 2の累乗のサイズが受け入れ可能である. <br>
     * このメソッドの戻り値は, 引数に対して2の累乗になるように切り上げを行う.
     * </p>
     */
    @Override
    public int calcAcceptableSize(int lower) {
        if (lower <= 1) {
            return 1;
        }

        // lower の切り上げ = lower - 1 の切り捨てを2倍
        return Integer.highestOneBit(lower - 1) << 1;
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public Function<double[], double[]> applyPartial(double[] f) {
        final int size = f.length;

        // 引数の検証
        if (size != calcAcceptableSize(size)) {
            throw new IllegalArgumentException(
                    "size is not acceptable, size = %s"
                            .formatted(size));
        }
        if (size > (1 << 29)) {
            throw new IllegalArgumentException(
                    "size is too large: size = %s".formatted(size));
        }

        return new PartialApplyImpl(f);
    }

    /**
     * {@link EffectiveCyclicConvolutionStubForTesting#applyPartial(double[])}
     * の戻り値の実装.
     */
    private static final class PartialApplyImpl implements Function<double[], double[]> {

        private final double[] f;

        /**
         * エンクロージングクラスから呼ばれる.
         * 引数はバリデーションされない.
         */
        PartialApplyImpl(double[] f) {
            super();
            this.f = f;
        }

        @Override
        public double[] apply(double[] g) {
            final int size = f.length;

            // 引数の検証
            if (g.length != size) {
                throw new IllegalArgumentException(
                        "f.length != g.length: f.length = %s, g.length = %s"
                                .formatted(f.length, g.length));
            }

            /*
             * j,kに関して二重ループを回し, iを計算するようにする.
             * iに関するループを回すよりも, 並列化される余地がある.
             */
            double[] out = new double[size];
            for (int j = 0; j < size; j++) {
                double f_j = f[j];

                for (int k = 0; k < size - j; k++) {
                    out[j + k] += f_j * g[k];
                }
                for (int k = size - j; k < size; k++) {
                    out[j + k - size] += f_j * g[k];
                }
            }

            return out;
        }
    }
}
