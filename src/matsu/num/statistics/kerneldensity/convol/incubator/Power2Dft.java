/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.14
 */
package matsu.num.statistics.kerneldensity.convol.incubator;

/**
 * 2の累乗の信号サイズを受け付ける DFT (離散 Fourier 変換) 機能. <br>
 * 逆変換も同時に扱う.
 * 
 * <p>
 * 離散 Fourier 変換は, <br>
 * F<sub>k</sub> = &Sigma;<sub>j=0</sub><sup>N-1</sup> f<sub>j</sub>
 * exp(-2&pi;i(jk/N)) <br>
 * 逆離散 Fourier 変換は, <br>
 * f<sub>j</sub> = &Sigma;<sub>k=0</sub><sup>N-1</sup> F<sub>k</sub>
 * exp(2&pi;i(jk/N)) <br>
 * である (i は虚数単位). <br>
 * 変換 {@literal ->} 逆変換により N 倍になる.
 * </p>
 * 
 * @author Matsuura Y.
 */
interface Power2Dft {

    /**
     * シグナルを離散 Fourier 変換する.
     * 
     * <p>
     * 入力シグナルは, {@code double[2][size]} であり,
     * {@code signal[0]} が実部を,
     * {@code signal[1]} が虚部を表す. <br>
     * {@code size} は2の累乗でなければならない.
     * </p>
     * 
     * <p>
     * シグナルの長さは, 2<sup>25</sup>までは必ず対応している. <br>
     * (これ以上の長さが与えられても, 直ちに例外をスローするわけではない.)
     * </p>
     * 
     * @param signal シグナル
     * @return 離散 Fourier 変換の結果
     * @throws IllegalArgumentException シグナルが正しい形式でない場合
     * @throws NullPointerException 引数にnullが含まれる場合
     */
    double[][] dft(double[][] signal);

    /**
     * シグナルを逆離散 Fourier 変換する.
     * 
     * <p>
     * 入力シグナルは, {@code double[2][size]} であり,
     * {@code signal[0]} が実部を,
     * {@code signal[1]} が虚部を表す.
     * </p>
     * 
     * <p>
     * シグナルの長さは, 2<sup>25</sup>までは必ず対応している. <br>
     * (これ以上の長さが与えられても, 直ちに例外をスローするわけではない.)
     * </p>
     * 
     * @param signal シグナル
     * @return 離散 Fourier 変換の結果
     * @throws IllegalArgumentException シグナルが正しい形式でない場合
     * @throws NullPointerException 引数にnullが含まれる場合
     */
    double[][] idft(double[][] signal);
}
