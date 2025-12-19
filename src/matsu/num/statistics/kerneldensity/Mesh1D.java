/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.16
 */
package matsu.num.statistics.kerneldensity;

import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * 1次元メッシュを表現する.
 * 
 * @author Matsuura Y.
 */
final class Mesh1D {

    /**
     * 拡張サイズ.
     */
    final int extendSize;

    /**
     * メッシュのx座標.
     */
    final double[] x;

    /**
     * 拡張されたメッシュのx座標.
     * 
     * <p>
     * {@code #x} の外側に {@link #extendSize} 分だけ増やす. <br>
     * 拡張によって, inf が現れる可能性がある.
     * </p>
     */
    final double[] extendX;

    /**
     * ソースを離散化した重み配列. <br>
     * 要素 index は, {@link #extendX} に対応する.
     */
    final double[] weight;

    /**
     * @param range range
     * @param resolution 空間分解能, 正の値
     * @param extendSize 拡張サイズ (フィルタのための拡張)
     */
    Mesh1D(Range range, double resolution, int extendSize, double[] source) {
        assert resolution > 0d;
        assert extendSize >= 0;
        assert source.length > 0;

        // range を resolution間隔で分割したメッシュ配列を構成する.
        this.x = DoubleStream
                .iterate(range.min(), v -> v <= range.max(), v -> v + resolution)
                .toArray();
        this.extendSize = extendSize;

        // xからextendedXを計算する, inf が現れる場合もある
        this.extendX = new double[x.length + 2 * extendSize];
        System.arraycopy(x, 0, extendX, extendSize, x.length);
        for (int i = extendSize - 1; i >= 0; i--) {
            extendX[i] = extendX[i + 1] - resolution;
        }
        for (int i = x.length + extendSize; i < extendX.length; i++) {
            extendX[i] = extendX[i - 1] + resolution;
        }

        /* ソースを反映したweightを構築する. */
        this.weight = new double[extendX.length];
        final double x0 = extendX[0];
        // ソースの各要素を重み1として, weightにaddする.
        for (double v : source) {
            double srcXR = (v - x0) / resolution;
            int i = (int) Math.floor(srcXR);
            double w_i = (i + 1) - srcXR;
            double w_ip1 = srcXR - i;

            if (0 <= i && i < extendX.length) {
                weight[i] += w_i;
            }
            if (-1 <= i && i < extendX.length - 1) {
                weight[i + 1] += w_ip1;
            }
        }

        // 全区間を網羅した場合が総和が1相当になるように正規化
        // (実際は有限区間なので, 総和は1以下になる)
        for (int i = 0; i < weight.length; i++) {
            weight[i] /= source.length;
        }
    }

    /**
     * 長さが extendX と同サイズの配列の両側をカットして,
     * 長さが x と同サイズの配列として返す.
     * 
     * @param src src
     * @return 両側をカットした配列
     * @throws IllegalArgumentException src の長さが extendX と同サイズでない場合
     * @throws NullPointerException 引数が null の場合
     */
    double[] reduceSize(double[] src) {
        if (src.length != extendX.length) {
            throw new IllegalArgumentException("src.length != extendX.length");
        }

        return Arrays.copyOfRange(src, extendSize, extendSize + x.length);
    }
}
