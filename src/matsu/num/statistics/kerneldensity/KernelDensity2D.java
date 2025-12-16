/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.29
 */
package matsu.num.statistics.kerneldensity;

import java.util.Arrays;

/**
 * 2次元のカーネル密度推定の実行を扱うインターフェース.
 * 
 * <p>
 * 推定を完了するために, データソースと出力範囲はユーザーからの入力を必須とする. <br>
 * このインターフェースは, インスタンスの生成時にデータ点が紐づけられ,
 * {@link #evaluateIn(Range, Range)} メソッドのコール時に出力範囲を与えて結果を出力する. <br>
 * したがって, {@link #evaluateIn(Range, Range)} はどのような引数でも
 * (出力範囲や分解能の違いを除いた) 同等な結果を返す. <br>
 * 出力の分解能は陽に定めることはできず, 出力範囲をもとに自動的に計算される.
 * </p>
 * 
 * <p>
 * {@link KernelDensity2D} のインスタンスの生成は,
 * {@link KernelDensity2D.Factory} を経由して行う. <br>
 * {@link KernelDensity2D.Factory#createOf(Kde2DSourceDto)}
 * メソッドのコール時にデータソースを渡すことで,
 * {@link KernelDensity2D} のインスタンスを得る.
 * </p>
 * 
 * <p>
 * {@link KernelDensity2D},
 * {@link KernelDensity2D.Factory} では,
 * カーネル関数の種類やバンド幅, 出力の空間分解能について定めない. <br>
 * {@link KernelDensity2D.Factory} の具象クラスにおいて,
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
public interface KernelDensity2D {

    /**
     * 与えられた x, y の範囲において, 確率値をカーネル密度推定する.
     * 
     * @param rangeX 推定する x の区間
     * @param rangeY 推定する y の区間
     * @return 推定結果
     * @throws NullPointerException 引数に null が含まれる場合
     */
    public abstract KdeGrid2dDto evaluateIn(Range rangeX, Range rangeY);

    /**
     * {@link KernelDensity2D} の生成を扱うインターフェース.
     * 
     * <p>
     * カーネル密度推定の全体の流れについては, {@link KernelDensity2D} の説明を参照.
     * </p>
     * 
     * <p>
     * {@link #createOf(Kde2DSourceDto)} メソッドは,
     * データソースを与えて {@link KernelDensity2D} のインスタンスを生成する. <br>
     * {@link KernelDensity2D} のインターフェース説明の通り,
     * カーネルのバンド幅に関するルールなどは,
     * {@link KernelDensity2D.Factory} の具層クラスの属性 (フィールド) として扱われる.
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
         * データソースは {@link Kde2DSourceDto} 型で与える. <br>
         * データソースには, NaNを含んではいけない (無限大は境界の値で置き換えられる). <br>
         * これに違反する場合, {@link IllegalArgumentException} をスローする.
         * </p>
         * 
         * <p>
         * データソースはメソッドコール中は変更してはならない. <br>
         * (変更された場合は, 結果は保証されない.)
         * </p>
         * 
         * <p>
         * 引数の事前チェックとして, {@link Factory#validateSource(Kde2DSourceDto)}
         * を提供している.
         * </p>
         * 
         * @param source データソース
         * @return データソースから生成されたカーネル密度推定
         * @throws IllegalArgumentException データソースが空の場合, NaNを含む場合
         * @throws NullPointerException 引数が null の場合
         */
        public abstract KernelDensity2D createOf(Kde2DSourceDto source);

        /**
         * データソースが正当であるか
         * ({@link #createOf(Kde2DSourceDto)} の引数に使用できるかどうか)
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
        public static boolean validateSource(Kde2DSourceDto source) {
            return Arrays.stream(source.x).noneMatch(Double::isNaN)
                    && Arrays.stream(source.y).noneMatch(Double::isNaN);
        }
    }
}
