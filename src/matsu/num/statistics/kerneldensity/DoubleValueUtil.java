/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.10.14
 */
package matsu.num.statistics.kerneldensity;

import java.util.Arrays;
import java.util.Spliterators;

/**
 * {@code double} 値に関するユーティリティクラス. <br>
 * パッケージ外には公開されない.
 * 
 * @author Matsuura Y.
 */
final class DoubleValueUtil {

    private DoubleValueUtil() {
        // インスタンス化不可
        throw new AssertionError();
    }

    /**
     * 無限大の値を有限の境界値に修正する.
     * 
     * @param v 値
     * @return v が無限大の場合は修正した値, それ以外の場合は v と同一値
     */
    static double correctInfinite(double v) {
        if (Double.isFinite(v)) {
            return v;
        }
        if (v == Double.NEGATIVE_INFINITY) {
            return -Double.MAX_VALUE;
        }
        if (v == Double.POSITIVE_INFINITY) {
            return Double.MAX_VALUE;
        }
        return v;
    }

    /**
     * double 配列がソート済みかどうかを変底する. <br>
     * 基準は, {@code Double.compare(v1,v2) <= 0}
     * 
     * @param v 配列
     * @return ソート済みなら true
     * @throws NullPointerException 引数がnullの場合
     */
    static boolean isSorted(double[] v) {
        if (v.length <= 1) {
            return true;
        }

        var ite = Spliterators.iterator(Arrays.spliterator(v));
        double former = ite.nextDouble();
        for (; ite.hasNext();) {
            double latter = ite.nextDouble();
            if (Double.compare(former, latter) > 0) {
                return false;
            }
            former = latter;
        }

        return true;
    }

    /**
     * 与えた配列の絶対値最大を計算する. <br>
     * 空の場合は0が返る.
     * 
     * @param v 配列
     * @return 絶対値最大
     */
    static double absMax(double[] v) {
        double absMax = 0d;
        for (double e : v) {
            absMax = Math.max(absMax, Math.abs(e));
        }
        return absMax;
    }

    /**
     * 与えた配列の平均値を計算する. <br>
     * 無限大を含む場合は結果は不定である. <br>
     * 空の場合は NaN が返る.
     * 
     * @param v 配列
     * @param absMax absMax(v)
     * @return vの要素の平均
     */
    static double average(double[] v, double absMax) {
        if (v.length == 0) {
            return Double.NaN;
        }

        final double largeLimit = 6.668014432879854274e+240; // 2^800
        if (absMax >= largeLimit) {
            return averageHuge(v);
        }

        double currentAverage = 0d;
        // 2回平均化を試みる:精度向上の期待
        for (int c = 0; c < 2; c++) {
            double sum = 0d;
            for (double e : v) {
                sum += e - currentAverage;
            }
            currentAverage += sum / v.length;
        }

        return currentAverage;
    }

    /**
     * vの要素が大きすぎる場合の, vの平均の計算.
     * 
     * <p>
     * 引数には空でない配列が渡されなければならない.
     * </p>
     * 
     * <p>
     * 実装の詳細: <br>
     * (この説明は古いかもしれない) <br>
     * 要素を2^800 (正確に, およそ 1E+240) で割って平均を計算し,
     * 最後に戻す.
     * </p>
     */
    private static double averageHuge(double[] v) {

        final double largeCoeff = 6.668014432879854274e+240; // 2^800
        final double invLargeCoeff = 1d / largeCoeff; // 2^(-800)

        double currentAverage = 0d;
        // 2回平均化を試みる:精度向上の期待
        for (int c = 0; c < 2; c++) {
            final double modifiedCurrentAverage = currentAverage * invLargeCoeff;
            double sum = 0d;
            for (double e : v) {
                sum += e * invLargeCoeff - modifiedCurrentAverage;
            }
            currentAverage += (sum / v.length) * largeCoeff;
        }

        return currentAverage;
    }

    /**
     * 与えた配列の, center に対する二乗平均平方根 (RMS) を計算する. <br>
     * RMSの性質上, 有限の値しか含まない場合であっても, 無限大が返る場合がある
     * (巨大要素と center が逆符号の場合). <br>
     * 無限大を含む場合は結果は不定である. <br>
     * 空の場合は NaN が返る.
     * 
     * <p>
     * center に average を与えた場合は, (n で割るタイプの) 標準偏差が返る.
     * </p>
     * 
     * @param v 配列
     * @param center 中心
     * @param absMax absMax(v)
     * @return vの要素の center に対する RMS
     */
    static double rms(double[] v, double center, double absMax) {
        if (v.length == 0) {
            return Double.NaN;
        }

        final double largeLimit = 2.58224987808690859e+120; // 2^400
        if (absMax >= largeLimit || Math.abs(center) >= largeLimit) {
            return rmsHuge(v, center);
        }

        double sum = 0d;
        for (double e : v) {
            double diff = e - center;
            sum += diff * diff;
        }
        return Math.sqrt(sum / v.length);
    }

    /**
     * vの要素が大きすぎる場合の, vのRMSの計算.
     * 
     * <p>
     * 引数には空でない配列が渡されなければならない.
     * </p>
     * 
     * <p>
     * 実装の詳細: <br>
     * (この説明は古いかもしれない) <br>
     * 要素を2^800 (正確に, およそ 1E+240) で割ってRMSを計算し,
     * 最後に戻す.
     * </p>
     */
    private static double rmsHuge(double[] v, double center) {

        final double largeCoeff = 6.668014432879854274e+240; // 2^800
        final double invLargeCoeff = 1d / largeCoeff; // 2^(-800)

        double modifiedCenter = center * invLargeCoeff;
        double sum = 0d;
        for (double e : v) {
            double mdiff = e * invLargeCoeff - modifiedCenter;
            sum += mdiff * mdiff;
        }
        return Math.sqrt((sum / v.length)) * largeCoeff;
    }
}
