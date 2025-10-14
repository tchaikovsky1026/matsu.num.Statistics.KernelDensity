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
}
