/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.12
 */
package matsu.num.statistics.kerneldensity;

/**
 * 素朴な実装における巡回畳み込みを扱う.
 * 
 * <p>
 * 以下, メモである.
 * </p>
 * 
 * <p>
 * カーネル密度推定で適切な窓関数を使う場合, フィルタサイズはそこまで大きくならない. <br>
 * したがって, 素朴な実装でも問題ない可能性は十分ある. <br>
 * ただし, 素朴な実装の場合, フィルタに合わせて信号を切り出す必要がないので,
 * この実装は不要になるかもしれない.
 * </p>
 * 
 * <p>
 * この実装は実用的でないかもしれないが, テスト用として維持する意義はあると思われる.
 * </p>
 * 
 * @author Matsuura Y.
 */
final class NaiveCyclicConvolution {

    /**
     * インスタンスを生成する.
     */
    NaiveCyclicConvolution() {
    }

    /**
     * 配列 f と g の巡回畳み込み (f*g) を計算する. <br>
     * (f*g)[i] = \sum_{j = 0}^{n - 1} f[j] g[i-j] <br>
     * サイズは暫定的に, 2^29以下を強制する.
     * 
     * @param f f
     * @param g g
     * @return (f*g)
     * @throws IllegalArgumentException サイズが異なる場合, サイズが大きすぎる場合
     * @throws NullPointerException 引数に null が含まれる場合
     */
    double[] compute(double[] f, double[] g) {
        final int size = f.length;

        // 引数の検証
        if (g.length != size) {
            throw new IllegalArgumentException(
                    "f.length != g.length: f.length = %s, g.length = %s"
                            .formatted(f.length, g.length));
        }
        if (size > (1 << 29)) {
            throw new IllegalArgumentException(
                    "size is too large: size = %s".formatted(size));
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
