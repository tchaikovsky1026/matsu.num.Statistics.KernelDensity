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
 * テスト用の DFT の素朴実装.
 * 
 * @author Matsuura Y.
 */
public final class NaivePower2DftForTesting implements Power2Dft {

    /**
     * 読み込める配列サイズの最大値.
     */
    private static final int MAX_SIZE = 1 << 28;

    /**
     * 唯一のコンストラクタ.
     */
    public NaivePower2DftForTesting() {
        super();
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public double[][] dft(double[][] signal) {
        if (signal.length != 2) {
            throw new IllegalArgumentException("signal.length != 2");
        }
        return calc(signal[0], signal[1], false);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public double[][] idft(double[][] signal) {
        if (signal.length != 2) {
            throw new IllegalArgumentException("signal.length != 2");
        }
        return calc(signal[0], signal[1], true);
    }

    /**
     * 離散 Fourier 変換, 逆変換を行う. <br>
     * 変換と逆変換の切り替えは, {@code boolean} で行う.
     * 
     * <p>
     * 入力シグナルサイズは2の累乗でなければならない.
     * </p>
     * 
     * <p>
     * シグナルの長さは, 2<sup>25</sup>までは必ず対応している. <br>
     * (これ以上の長さが与えられても, 直ちに例外をスローするわけではない.)
     * </p>
     * 
     * @param real 実部
     * @param imaginary 虚部
     * @param isIt 逆変換の場合はtrue
     * @return 変換結果
     * @throws IllegalArgumentException シグナルが正しい形式でない場合
     * @throws NullPointerException 引数にnullが含まれる場合
     */
    private static double[][] calc(double[] real, double[] imaginary, boolean isIt) {
        validateSignal(real, imaginary);
        int N = real.length;
        // mod Nを計算するとき, 論理積を取ればよい.
        int modN_Mask = N - 1;

        // 回転を用意する
        double[] rot_re = new double[N];
        double[] rot_im = new double[N];
        computeAndWriteRotation(rot_re, rot_im, isIt);

        // 結果用の配列の準備
        double[] result_re = new double[N];
        double[] result_im = new double[N];

        // DFT or IDFTを行う
        // temp は複素数積の結果格納用である.
        // tempを使いまわすことは内部のパイプライン化を阻害する可能性がある.
        double[] temp = new double[2];
        for (int j = 0; j < N; j++) {
            // j*k % Nを表す.
            int jk_mod_N = 0;
            for (int k = 0;
                    k < N;
                    k++, jk_mod_N = (jk_mod_N + j) & modN_Mask) {

                multiplyAndWriteComplex(
                        real[j], imaginary[j],
                        rot_re[jk_mod_N], rot_im[jk_mod_N],
                        temp);
                result_re[k] += temp[0];
                result_im[k] += temp[1];
            }
        }

        return new double[][] { result_re, result_im };
    }

    /**
     * {@code real}, {@code imaginary} の配列サイズが適切かどうかを調べる. <br>
     * 適切でない場合は例外 ({@link IllegalArgumentException},
     * {@link NullPointerException}) をスローする.
     */
    private static void validateSignal(double[] real, double[] imaginary) {
        int size = real.length;
        if (imaginary.length != size) {
            throw new IllegalArgumentException("real.length != imaginary.length");
        }
        if (size > 0 && (size & (size - 1)) != 0) {
            throw new IllegalArgumentException("NOT power of 2");
        }
        if (size > MAX_SIZE) {
            throw new IllegalArgumentException("size is too large: size = " + size);
        }
    }

    /**
     * 回転成分を計算して書き込む: <br>
     * (DFTの場合)
     * {@code (rot_real[j], rot_imaginary[j])} = exp(-2&pi;i(j/N)) <br>
     * (IDFTの場合)
     * {@code (rot_real[j], rot_imaginary[j])} = exp(2&pi;i(j/N))
     */
    private static void computeAndWriteRotation(double[] rot_real, double[] rot_imaginary, boolean isIt) {
        /*
         * cos, sinの計算のところは, x: phi/(2pi) を 1/8 刻みで分岐したほうが相対誤差は小さくなる.
         * 必要ならリファクタリングすること.
         */

        // x = j/N
        int N = rot_real.length;
        for (int j = 0; j < N; j++) {
            double x = (double) j / N;
            double cos = Math.cos(2 * Math.PI * x);
            double sin = Math.sin(2 * Math.PI * x);
            if (!isIt) {
                sin = -sin;
            }
            rot_real[j] = cos;
            rot_imaginary[j] = sin;
        }
    }

    /**
     * 複素数の積を計算し, 結果を配列に書き込む.
     * 
     * @param result 長さ2, result[0] = Re, result[1] = Im
     */
    private static void multiplyAndWriteComplex(
            double re_a, double im_a, double re_b, double im_b,
            double[] result) {

        assert result.length == 2;

        result[0] = re_a * re_b - im_a * im_b;
        result[1] = re_a * im_b + im_a * re_b;
    }
}
