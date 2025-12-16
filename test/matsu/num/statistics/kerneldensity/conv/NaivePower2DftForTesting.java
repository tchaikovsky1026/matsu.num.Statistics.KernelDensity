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
 * テスト用の DFT の素朴実装.
 * 
 * @author Matsuura Y.
 */
public final class NaivePower2DftForTesting
        extends SkeletalPower2Dft
        implements Power2Dft {

    // 2^{28} まで対応する
    private static final int MAX_SIZE_LB = 28;

    /**
     * 唯一のコンストラクタ.
     */
    public NaivePower2DftForTesting() {
        super(MAX_SIZE_LB);
    }

    @Override
    double[][] transform(double[] signal_re, double[] signal_im, boolean isIt) {

        int N = signal_re.length;
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
                        signal_re[j], signal_im[j],
                        rot_re[jk_mod_N], rot_im[jk_mod_N],
                        temp);
                result_re[k] += temp[0];
                result_im[k] += temp[1];
            }
        }

        return new double[][] { result_re, result_im };
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
