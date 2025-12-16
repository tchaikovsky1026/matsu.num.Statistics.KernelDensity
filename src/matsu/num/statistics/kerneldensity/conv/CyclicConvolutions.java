/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.16
 */
package matsu.num.statistics.kerneldensity.conv;

import matsu.num.statistics.kerneldensity.EffectiveCyclicConvolution;

/**
 * {@link EffectiveCyclicConvolution} の実装提供に関わるユーティリティクラス.
 * 
 * @author Matsuura Y.
 */
public final class CyclicConvolutions {

    private CyclicConvolutions() {
        // インスタンス化不可
        throw new AssertionError();
    }

    /**
     * 高速 Fourier 変換に基づく, 巡回畳み込み計算器を返す.
     * 
     * @return 巡回畳み込み計算器
     */
    public static EffectiveCyclicConvolution fftBased() {
        // staticフィールドの遅延初期化を使う
        return FftBasedCyclicConvolutionHolder.INSTANCE;
    }

    /**
     * fftBased の巡回畳み込みホルダー.
     */
    private static final class FftBasedCyclicConvolutionHolder {
        static final EffectiveCyclicConvolution INSTANCE =
                new Power2DftInjectedCyclicConvolution(new Power2Fft());
    }
}
