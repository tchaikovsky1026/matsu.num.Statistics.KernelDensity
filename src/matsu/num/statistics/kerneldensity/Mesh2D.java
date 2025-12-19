/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.1
 */
package matsu.num.statistics.kerneldensity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * 2次元メッシュを表現する.
 * 
 * @author Matsuura Y.
 */
final class Mesh2D {

    private static final double[][] EMPTY_DOUBLE_DOUBLE_ARRAY = new double[0][];

    /**
     * X方向の拡張サイズ.
     */
    final int extendSizeX;

    /**
     * Y方向の拡張サイズ.
     */
    final int extendSizeY;

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
     * メッシュのy座標.
     */
    final double[] y;

    /**
     * 拡張されたメッシュのy座標.
     * 
     * <p>
     * {@code #y} の外側に {@link #extendSize} 分だけ増やす. <br>
     * 拡張によって, inf が現れる可能性がある.
     * </p>
     */
    final double[] extendY;

    /**
     * ソースを離散化した重み配列. <br>
     * 要素 index は, [{@link #extendX}][{@link #extendY}] に対応する.
     */
    final double[][] weight;

    /**
     * @param rangeX rangeX
     * @param rangeY rangeY
     * @param resolutionX Xの空間分解能, 正の値
     * @param resolutionY Yの空間分解能, 正の値
     * @param extendSize 拡張サイズ (フィルタのための拡張)
     */
    Mesh2D(Range rangeX, Range rangeY, double resolutionX, double resolutionY,
            int extendSizeX, int extendSizeY, Kde2DSourceDto source) {
        assert resolutionX > 0d;
        assert resolutionY > 0d;
        assert extendSizeX >= 0;
        assert extendSizeY >= 0;

        // range を resolution間隔で分割したメッシュ配列を構成する.
        this.x = DoubleStream
                .iterate(rangeX.min(), v -> v <= rangeX.max(), v -> v + resolutionX)
                .toArray();
        this.y = DoubleStream
                .iterate(rangeY.min(), v -> v <= rangeY.max(), v -> v + resolutionY)
                .toArray();
        this.extendSizeX = extendSizeX;
        this.extendSizeY = extendSizeY;

        // Issue: 以下は, メソッドに切り出すとよい.
        // xからextendedXを計算する, inf が現れる場合もある
        this.extendX = new double[x.length + 2 * extendSizeX];
        System.arraycopy(x, 0, extendX, extendSizeX, x.length);
        for (int i = extendSizeX - 1; i >= 0; i--) {
            extendX[i] = extendX[i + 1] - resolutionX;
        }
        for (int i = x.length + extendSizeX; i < extendX.length; i++) {
            extendX[i] = extendX[i - 1] + resolutionX;
        }

        // yからextendedYを計算する, inf が現れる場合もある
        this.extendY = new double[y.length + 2 * extendSizeY];
        System.arraycopy(y, 0, extendY, extendSizeY, y.length);
        for (int i = extendSizeY - 1; i >= 0; i--) {
            extendY[i] = extendY[i + 1] - resolutionY;
        }
        for (int i = y.length + extendSizeY; i < extendY.length; i++) {
            extendY[i] = extendY[i - 1] + resolutionY;
        }

        /* ソースを反映したweightを構築する. */
        this.weight = new double[extendX.length][extendY.length];
        final double x0 = extendX[0];
        final double y0 = extendY[0];
        // ソースの各要素を重み1として, weightにaddする.
        for (int i = 0, len = source.size; i < len; i++) {
            double vx = source.x[i];
            double vy = source.y[i];

            // (vx, vy) を格子点座標系 (srcXR, srcYR) に直す
            double srcXR = (vx - x0) / resolutionX;
            double srcYR = (vy - y0) / resolutionY;

            // (srcXR, srcYR) を格子点に重みを割り振る
            // (srcXR, srcYR)が負になる可能性に注意して, floorを使う
            int j = (int) Math.floor(srcXR);
            int k = (int) Math.floor(srcYR);
            double w_jk = ((j + 1) - srcXR) * ((k + 1) - srcYR);
            double w_jkp1 = ((j + 1) - srcXR) * (srcYR - k);
            double w_jp1k = (srcXR - j) * ((k + 1) - srcYR);
            double w_jp1kp1 = (srcXR - j) * (srcYR - k);

            if (0 <= j && j < extendX.length
                    && 0 <= k && k < extendY.length) {
                weight[j][k] += w_jk;
            }
            if (0 <= j && j < extendX.length
                    && -1 <= k && k < extendY.length - 1) {
                weight[j][k + 1] += w_jkp1;
            }
            if (-1 <= j && j < extendX.length - 1
                    && 0 <= k && k < extendY.length) {
                weight[j + 1][k] += w_jp1k;
            }
            if (-1 <= j && j < extendX.length - 1
                    && -1 <= k && k < extendY.length - 1) {
                weight[j + 1][k + 1] += w_jp1kp1;
            }
        }

        // 全区間を網羅した場合が総和が1相当になるように正規化
        // (実際は有限区間なので, 総和は1以下になる)
        for (int j = 0; j < weight.length; j++) {
            double[] weight_j = weight[j];
            for (int k = 0; k < weight_j.length; k++) {
                weight_j[k] /= source.size;
            }
        }
    }

    /**
     * 長さが (extendX, extendY) と同サイズの配列の両側をカットして,
     * 長さが (x, y) と同サイズの配列として返す.
     * 
     * @param src src
     * @return 両側をカットした配列
     * @throws IllegalArgumentException src の長さが不正の場合
     * @throws NullPointerException 引数が null の場合
     */
    double[][] reduceSize(double[][] src) {
        if (src.length != extendX.length) {
            throw new IllegalArgumentException("src.length != extendX.length");
        }

        List<double[]> outList = new ArrayList<double[]>(extendX.length);
        for (int j = extendSizeX; j < extendX.length - extendSizeX; j++) {
            double[] src_j = src[j];

            if (src_j.length != extendY.length) {
                throw new IllegalArgumentException("src[j].length != extendY.length");
            }
            outList.add(Arrays.copyOfRange(src_j, extendSizeY, extendSizeY + y.length));
        }

        return outList.toArray(EMPTY_DOUBLE_DOUBLE_ARRAY);
    }
}
