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

/**
 * 1次元のカーネル密度推定の実行を扱うインターフェース.
 * 
 * @implSpec
 *               このインターフェースはモジュール内で実装されるためのものであり,
 *               モジュール外では実装してはいけない.
 * @author Matsuura Y.
 */
public interface KernelDensity1D {

    /**
     * 1次元のカーネル密度推定を実行する.
     * 
     * @param range 結果を出力の区間
     * @return 実行結果
     * @throws NullPointerException 引数が null の場合
     */
    public abstract KdeGrid1dDto compute(Range range);

    /**
     * {@link KernelDensity1D} の生成を扱うインターフェース.
     * 
     * @implSpec
     *               このインターフェースはモジュール内で実装されるためのものであり,
     *               モジュール外では実装してはいけない.
     */
    public static interface Factory {

        /**
         * 与えたデータソースから, カーネル密度推定を生成する.
         * 
         * <p>
         * データソースは値の配列 ({@code double} 型) で与える. <br>
         * データソースには, NaNを含んではいけない (無限大は境界の値で置き換えられる). <br>
         * また, 空であってはいけない. <br>
         * これに違反する場合, {@link IllegalArgumentException} をスローする.
         * </p>
         * 
         * <p>
         * 引数の事前チェックとして, {@link Factory#validateSource(double[])}
         * を提供している.
         * </p>
         * 
         * @param source データソース
         * @return データソースから生成されたカーネル密度推定
         * @throws IllegalArgumentException データソースが空の場合, NaNを含む場合
         * @throws NullPointerException 引数が null の場合
         */
        public abstract KernelDensity1D createOf(double[] source);

        /**
         * データソースが正当であるか
         * ({@link #createOf(double[])} の引数に使用できるかどうか)
         * を検証する.
         * 
         * @param source データソース
         * @return データソースが正当な場合は true
         * @throws NullPointerException 引数が null の場合
         */
        public static boolean validateSource(double[] source) {
            return source.length > 0
                    && Arrays.stream(source).noneMatch(Double::isNaN);
        }
    }
}
