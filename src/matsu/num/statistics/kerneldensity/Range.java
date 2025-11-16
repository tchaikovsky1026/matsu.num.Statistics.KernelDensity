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

import static matsu.num.statistics.kerneldensity.DoubleValueUtil.*;

/**
 * {@code double} 値で表現された閉区間 (min &le; <i>x</i> &le; max) を表現するクラス.
 * 
 * <p>
 * min, max は有限の値を扱う. <br>
 * min, max の値に基づくequalityを提供する.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class Range {

    // min <= max が不変条件
    private final double min;
    private final double max;

    /**
     * 非公開の唯一のコンストラクタ.
     * 
     * <p>
     * 有限の {@code min <= max} でなければならない. <br>
     * コンストラクタではバリデーションされていないので, 呼び出し元で行うこと.
     * </p>
     */
    private Range(double min, double max) {
        super();
        this.min = min;
        this.max = max;
    }

    /**
     * 区間の最小値 (min) を返す.
     * 
     * @return min
     */
    public double min() {
        return min;
    }

    /**
     * 区間の最大値 (max) を返す.
     * 
     * @return max
     */
    public double max() {
        return max;
    }

    /**
     * この区間の半幅を返す.
     * 
     * @return 半幅
     */
    public double halfWidth() {
        double out = max - min;
        return Double.isFinite(out)
                ? 0.5 * out
                : 0.5 * max - 0.5 * min;
    }

    /**
     * 自身と与えられたインスタンスが等価かどうかを判定する.
     * 
     * <p>
     * equality はクラス説明文の通り.
     * </p>
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Range target)) {
            return false;
        }

        return Double.compare(this.min, target.min) == 0
                && Double.compare(this.max, target.max) == 0;
    }

    /**
     * このインスタンスのハッシュコードを返す.
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Double.hashCode(min);
        result = 31 * result + Double.hashCode(max);

        return result;
    }

    /**
     * このインスタンスの文字列表現を返す.
     * 
     * <p>
     * 文字列表現は明確に規定されておらず, バージョン間の互換性も担保されていない. <br>
     * おそらく, 次のような形式である. <br>
     * {@code Range[%min, %max]}
     * </p>
     */
    @Override
    public String toString() {
        return "Range[%s, %s]"
                .formatted(this.min(), this.max());
    }

    /**
     * 与えた min と max を持つ {@link Range} オブジェクトを返す.
     * 
     * <p>
     * 引数について, min &le; max であることが必要である. <br>
     * そうでない場合は例外がスローされる (NaN の場合も). <br>
     * 無限大の場合は境界の値に置き換えられる.
     * </p>
     * 
     * @param min 区間の最小値
     * @param max 区間の最大値
     * @return [min, max]
     * @throws IllegalArgumentException min &le; max でない場合
     */
    public static Range of(double min, double max) {
        if (!(min <= max)) {
            throw new IllegalArgumentException(
                    "illegal: NOT min <= max: min = %s, max = %s"
                            .formatted(min, max));
        }
        return new Range(correctInfinite(min), correctInfinite(max));
    }
}
