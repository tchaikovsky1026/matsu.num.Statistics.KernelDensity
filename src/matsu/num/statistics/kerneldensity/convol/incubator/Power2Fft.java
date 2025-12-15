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
 * FFT (Cooley–Tukey algorithm) により {@link Power2Dft} を実装する.
 * 
 * @author Matsuura Y.
 */
final class Power2Fft implements Power2Dft {

    /**
     * 読み込める配列サイズの最大値.
     */
    private static final int MAX_SIZE = 1 << 28;

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

        // 回転を用意する
        // このアルゴリズムでは位相piまでしか使用しないので, 不要な生成は行わない
        double[] rot_re = new double[N >>> 1];
        double[] rot_im = new double[N >>> 1];
        computeAndWriteRotation(N, rot_re, rot_im, isIt);

        /*
         * 以下は, Chat-GPTによるコード.
         * テストは実行済み.
         */

        // 作業用バッファ（in-place FFT）
        // 初期値はシグナルである
        double[] sr = real.clone();
        double[] si = imaginary.clone();

        // =========================
        // bit-reversal permutation
        // =========================
        for (int i = 1, j = 0; i < N; i++) {
            int bit = N >>> 1;
            while ((j & bit) != 0) {
                j ^= bit;
                bit >>>= 1;
            }
            j |= bit;

            if (i < j) {
                double tmp_re = sr[i];
                sr[i] = sr[j];
                sr[j] = tmp_re;

                double tmp_im = si[i];
                si[i] = si[j];
                si[j] = tmp_im;
            }
        }

        // =========================
        // FFT main loop
        // =========================
        for (int m = 2; m <= N; m <<= 1) {
            int half_m = m >>> 1;
            int step = N / m; // 回転因子のインデックス間隔

            for (int k = 0; k < N; k += m) {

                for (int j = 0; j < half_m; j++) {
                    int rotIndex = j * step;
                    double wr = rot_re[rotIndex];
                    double wi = rot_im[rotIndex];

                    int i0 = k + j;
                    int i1 = i0 + half_m;

                    // 複素数の乗算 t =  w * s
                    double tr = wr * sr[i1] - wi * si[i1];
                    double ti = wr * si[i1] + wi * sr[i1];

                    sr[i1] = sr[i0] - tr;
                    si[i1] = si[i0] - ti;

                    sr[i0] += tr;
                    si[i0] += ti;
                }
            }
        }

        return new double[][] { sr, si };
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
    private static void computeAndWriteRotation(int N, double[] rot_real, double[] rot_imaginary, boolean isIt) {
        /*
         * cos, sinの計算のところは, x: phi/(2pi) を 1/8 刻みで分岐したほうが相対誤差は小さくなる.
         * 必要ならリファクタリングすること.
         */

        // x = j/N
        int size = rot_real.length;
        for (int j = 0; j < size; j++) {
            double x = (double) j / N;
            double cos = cos2pi(x);
            double sin = sin2pi(x);
            if (!isIt) {
                sin = -sin;
            }
            rot_real[j] = cos;
            rot_imaginary[j] = sin;
        }
    }

    /**
     * cos(2*pi*x) を返す. <br>
     * x は0以上1未満でなければならない.
     */
    private static double cos2pi(double x) {
        assert 0 <= x && x < 1;

        // 一周を2分割する
        int division_2 = (int) (2 * x);

        return switch (division_2) {
            case 0 -> Math.sin(2 * Math.PI * (0.25 - x));
            case 1 -> Math.sin(2 * Math.PI * (x - 0.75));
            default -> throw new IllegalArgumentException("Unexpected value: " + division_2);
        };
    }

    /**
     * sin(2*pi*x) を返す. <br>
     * x は0以上1未満でなければならない.
     */
    private static double sin2pi(double x) {
        assert 0 <= x && x < 1;

        // 一周を4分割する
        int division_4 = (int) (4 * x);

        return switch (division_4) {
            case 0 -> Math.sin(2 * Math.PI * x);
            case 1, 2 -> Math.sin(2 * Math.PI * (0.5 - x));
            case 3 -> Math.sin(2 * Math.PI * (x - 1));
            default -> throw new IllegalArgumentException("Unexpected value: " + division_4);
        };
    }
}
