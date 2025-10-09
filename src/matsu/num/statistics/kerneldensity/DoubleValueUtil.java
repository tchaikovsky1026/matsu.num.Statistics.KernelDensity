/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.10.10
 */
package matsu.num.statistics.kerneldensity;

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
}
