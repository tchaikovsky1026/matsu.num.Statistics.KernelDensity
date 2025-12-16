/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.18
 */
package matsu.num.statistics.kerneldensity;

import java.util.Arrays;

/**
 * 1次元のカーネル密度推定の実行を扱うインターフェース.
 * 
 * <p>
 * 推定を完了するために, (サイズ1以上の) データソースと出力範囲はユーザーからの入力を必須とする. <br>
 * このインターフェースは, インスタンスの生成時にデータ点が紐づけられ,
 * {@link #evaluateIn(Range)} メソッドのコール時に出力範囲を与えて結果を出力する. <br>
 * したがって, {@link #evaluateIn(Range)} はどのような引数でも
 * (出力範囲や分解能の違いを除いた) 同等な結果を返す. <br>
 * 出力の分解能は陽に定めることはできず, 出力範囲をもとに自動的に計算される.
 * </p>
 * 
 * <p>
 * {@link KernelDensity1D} のインスタンスの生成は,
 * {@link KernelDensity1D.Factory} を経由して行う. <br>
 * {@link KernelDensity1D.Factory#createOf(double[])}
 * メソッドのコール時にデータソースを渡すことで,
 * {@link KernelDensity1D} のインスタンスを得る.
 * </p>
 * 
 * <p>
 * {@link KernelDensity1D},
 * {@link KernelDensity1D.Factory} では,
 * カーネル関数の種類やバンド幅, 出力の空間分解能について定めない. <br>
 * {@link KernelDensity1D.Factory} の具象クラスにおいて,
 * それらについての設定を扱っている場合がある.
 * </p>
 * 
 * <p>
 * このインターフェースのサブタイプはイミュータブルで関数的である.
 * </p>
 * 
 * @implSpec
 *               このインターフェースはモジュール内で実装されるために用意されており,
 *               モジュール外では実装してはいけない. <br>
 *               モジュール内で実装する場合でも, イミュータブルで関数的でなければならない.
 * @author Matsuura Y.
 */
public interface KernelDensity1D {

    /**
     * 与えられた範囲において, 確率値をカーネル密度推定する.
     * 
     * @param range 推定する区間
     * @return 推定結果
     * @throws NullPointerException 引数が null の場合
     */
    public abstract KdeGrid1dDto evaluateIn(Range range);

    /**
     * {@link KernelDensity1D} の生成を扱うインターフェース.
     * 
     * <p>
     * カーネル密度推定の全体の流れについては, {@link KernelDensity1D} の説明を参照.
     * </p>
     * 
     * <p>
     * {@link #createOf(double[])} メソッドは,
     * データソースを与えて {@link KernelDensity1D} のインスタンスを生成する. <br>
     * {@link KernelDensity1D} のインターフェース説明の通り,
     * カーネルのバンド幅に関するルールなどは,
     * {@link KernelDensity1D.Factory} の具層クラスの属性 (フィールド) として扱われる.
     * </p>
     * 
     * <p>
     * このインターフェースのサブタイプはイミュータブルで関数的である.
     * </p>
     * 
     * @implSpec
     *               このインターフェースはモジュール内で実装されるために用意されており,
     *               モジュール外では実装してはいけない. <br>
     *               モジュール内で実装する場合でも, イミュータブルで関数的でなければならない.
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
         * データソースはメソッドコール中は変更してはならない. <br>
         * (変更された場合は, 結果は保証されない.)
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
         * <p>
         * データソースはメソッドコール中は変更してはならない. <br>
         * (変更された場合は, 結果は保証されない.)
         * </p>
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
