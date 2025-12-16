/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.15
 */
package matsu.num.statistics.kerneldensity.conv;

/**
 * FFT (Cooley–Tukey algorithm) により {@link Power2Dft} を実装する.
 * 
 * @author Matsuura Y.
 */
final class Power2Fft extends SkeletalPower2Dft implements Power2Dft {

    // 2^{28} まで対応する
    private static final int MAX_SIZE_LB = 28;

    /**
     * 唯一のコンストラクタ.
     */
    Power2Fft() {
        super(MAX_SIZE_LB);
    }

    @Override
    double[][] transform(double[] signal_re, double[] signal_im, boolean isIt) {

        int N = signal_re.length;

        // 回転を用意する
        // このアルゴリズムでは位相 < piまでしか使用しないので, 不要な生成は行わない
        double[] rot_re = new double[N >>> 1];
        double[] rot_im = new double[N >>> 1];
        computeAndWriteRotation(N, rot_re, rot_im, isIt);

        /*
         * 以下は, Chat-GPTによるコード.
         * テストは実行済み.
         */

        // 作業用バッファ（in-place FFT）
        // 初期値はシグナルである
        double[] sr = signal_re.clone();
        double[] si = signal_im.clone();

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
