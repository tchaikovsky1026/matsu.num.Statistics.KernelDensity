/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.15
 */
package matsu.num.statistics.kerneldensity.convol.incubator;

import java.util.Objects;
import java.util.function.UnaryOperator;

import matsu.num.statistics.kerneldensity.EffectiveCyclicConvolution;

/**
 * {@link Power2Dft} をインジェクションすることで作動する,
 * {@link EffectiveCyclicConvolution} の実装.
 * 
 * <p>
 * 2の累乗サイズにのみ対応している. <br>
 * 対応可能サイズの最大はインジェクションされる {@link Power2Dft#maxAcceptableSize()}
 * に一致する.
 * </p>
 * 
 * @author Matsuura Y.
 */
final class Power2DftInjectedCyclicConvolution implements EffectiveCyclicConvolution {

    private final Power2Dft dft;

    /**
     * 唯一のコンストラクタ.
     * 
     * @throws NullPointerException 引数がnull
     */
    Power2DftInjectedCyclicConvolution(Power2Dft dft) {
        super();
        this.dft = Objects.requireNonNull(dft);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public int calcAcceptableSize(int lower) {
        if (lower > dft.maxAcceptableSize()) {
            throw new IllegalArgumentException("too large: " + lower);
        }

        if (lower <= 1) {
            return 1;
        }

        // lower の切り上げ = lower - 1 の切り捨てを2倍
        return Integer.highestOneBit(lower - 1) << 1;
    }

    /**
     * @apiNote {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public UnaryOperator<double[]> applyPartial(double[] f) {
        final int size = f.length;

        // 引数の検証
        // 大きすぎる場合も例外もスローされる
        if (size != calcAcceptableSize(size)) {
            throw new IllegalArgumentException(
                    "size is not acceptable, size = %s"
                            .formatted(size));
        }

        return new PartialApplyImpl(f);
    }

    /**
     * {@link Power2DftInjectedCyclicConvolution#applyPartial(double[])}
     * の戻り値の実装.
     */
    private final class PartialApplyImpl implements UnaryOperator<double[]> {

        private final int size;
        private final double[] f_dft_re;
        private final double[] f_dft_im;

        /**
         * 内部から呼ばれる.
         * 
         * <p>
         * 引数は呼び出しもとでチェックすること.
         * </p>
         */
        PartialApplyImpl(double[] f) {
            this.size = f.length;
            double[] f_re = f;
            double[] f_im = new double[f.length];

            // F = DFT(f) を計算
            double[][] f_dft = dft.dft(new double[][] { f_re, f_im });
            this.f_dft_re = f_dft[0];
            this.f_dft_im = f_dft[1];
        }

        /**
         * @throws IllegalArgumentException g のサイズが不適の場合
         * @throws NullPointerException 引数がnull
         */
        @Override
        public double[] apply(double[] g) {
            if (g.length != size) {
                throw new IllegalArgumentException(
                        "size mismatch: f.length = %s, g.length = %s"
                                .formatted(size, g.length));
            }

            // G = DFT(g) を計算
            double[] g_re = g;
            double[] g_im = new double[size];
            double[][] g_dft = dft.dft(new double[][] { g_re, g_im });
            double[] g_dft_re = g_dft[0];
            double[] g_dft_im = g_dft[1];

            // H = FG = DFT(f*g) を計算
            double[] h_dft_re = new double[size];
            double[] h_dft_im = new double[size];
            for (int j = 0, len = size; j < len; j++) {
                double f_dft_re_j = f_dft_re[j];
                double f_dft_im_j = f_dft_im[j];
                double g_dft_re_j = g_dft_re[j];
                double g_dft_im_j = g_dft_im[j];

                // 複素数の積 h_dft[j] = f_dft[j] * g_dft[j]
                h_dft_re[j] = f_dft_re_j * g_dft_re_j - f_dft_im_j * g_dft_im_j;
                h_dft_im[j] = f_dft_re_j * g_dft_im_j + f_dft_im_j * g_dft_re_j;
            }

            // H を　h に直す
            double[][] h = dft.idft(new double[][] { h_dft_re, h_dft_im });
            double[] h_re = h[0];
            double invSize = 1d / size;
            for (int j = 0, len = size; j < len; j++) {
                h_re[j] *= invSize;
            }

            return h_re;
        }
    }
}
