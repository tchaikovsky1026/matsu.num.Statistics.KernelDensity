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

/**
 * 2の累乗のシグナルサイズを受け付ける DFT (離散 Fourier 変換) 機能. <br>
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
 * <p>
 * 読み込むことができるシグナルサイズは具象にゆだねられる. <br>
 * その値は, 抽象メソッド ({@link #maxAcceptableSize()}) により取得可能である. <br>
 * 共通に, 2<sup>25</sup>までは必ず対応している.
 * </p>
 * 
 * @author Matsuura Y.
 */
interface Power2Dft {

    /**
     * このインスタンスの {@link #dft(double[][])}, {@link #idft(double[][])}
     * メソッドが対応できるシグナルサイズの最大値を返す. <br>
     * 必ず 2<sup>25</sup> 以上の2の累乗数である.
     * 
     * <p>
     * このメソッドの戻り値に一致するサイズを持つシグナルは,
     * 必ず {@link #dft(double[][])}, {@link #idft(double[][])}
     * の引数になることができる.
     * </p>
     * 
     * @implSpec
     *               メソッド説明にしたがって実装すること.
     * 
     *               <p>
     *               この戻り値は, インスタンスのライフサイクル全体で不変でなければならない.
     *               </p>
     * 
     * @return シグナルの最大サイズ
     */
    public abstract int maxAcceptableSize();

    /**
     * シグナルを離散 Fourier 変換する.
     * 
     * <p>
     * 入力シグナルは {@code double[2][size]} であり,
     * {@code signal[0]} が実部を,
     * {@code signal[1]} が虚部を表す. <br>
     * {@code size} は (1以上の) 2の累乗でなければならない.
     * </p>
     * 
     * <p>
     * 対応可能なシグナルの長さの最大値は, {@link #maxAcceptableSize()} の戻り値で規定される.
     * </p>
     * 
     * <p>
     * 出力結果 ({@code double[][] result}) は {@code double[2][size]} であり,
     * {@code result[0]} が実部を,
     * {@code result[1]} が虚部を表す.
     * </p>
     * 
     * @param signal シグナル
     * @return 離散 Fourier 変換の結果
     * @throws IllegalArgumentException シグナルが正しい形式でない場合 (シグナルサイズが大きすぎる場合を含む)
     * @throws NullPointerException 引数にnullが含まれる場合
     */
    public abstract double[][] dft(double[][] signal);

    /**
     * シグナルを逆離散 Fourier 変換する.
     * 
     * <p>
     * 入力シグナルは {@code double[2][size]} であり,
     * {@code signal[0]} が実部を,
     * {@code signal[1]} が虚部を表す. <br>
     * {@code size} は (1以上の) 2の累乗でなければならない.
     * </p>
     * 
     * <p>
     * 対応可能なシグナルの長さの最大値は, {@link #maxAcceptableSize()} の戻り値で規定される.
     * </p>
     * 
     * <p>
     * 出力結果 ({@code double[][] result}) は {@code double[2][size]} であり,
     * {@code result[0]} が実部を,
     * {@code result[1]} が虚部を表す.
     * </p>
     * 
     * @param signal シグナル
     * @return 離散 Fourier 変換の結果
     * @throws IllegalArgumentException シグナルが正しい形式でない場合 (シグナルサイズが大きすぎる場合を含む)
     * @throws NullPointerException 引数にnullが含まれる場合
     */
    public abstract double[][] idft(double[][] signal);
}
