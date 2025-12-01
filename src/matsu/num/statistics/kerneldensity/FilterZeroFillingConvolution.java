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

/**
 * フィルタ畳み込みを表現するインターフェース.
 * 
 * <p>
 * イミュータブルで関数的である.
 * </p>
 * 
 * <p>
 * パッケージ内に隠ぺいする.
 * </p>
 * 
 * @author Matsuura Y.
 */
interface FilterZeroFillingConvolution {

    /**
     * 与えたフィルタにより, {@link PartialApplied}
     * ({@code signal -> (フィルタ畳み込み結果)})
     * を構築する.
     * 
     * <p>
     * フィルタは片側の値を配列でを与える. <br>
     * 与えた配列に対して, <br>
     * {@code filter[length - 1], ... , filter[1], filter[0] (center), filter[1], ... , filter[length - 1]}
     * <br>
     * となる.
     * </p>
     * 
     * <p>
     * フィルタサイズは1以上でなければならない.
     * </p>
     * 
     * <p>
     * 引数の配列は, 内部で防御的コピーされるため, 呼び出しもとで書き換えてよい.
     * </p>
     * 
     * @implSpec 引数をコピーすること.
     * 
     * @param filter フィルタ
     * @return {@link PartialApplied} ({@code signal -> (フィルタ畳み込み結果)})
     * @throws IllegalArgumentException 引数が不適の場合
     * @throws NullPointerException 引数がnullの場合
     */
    public abstract PartialApplied applyPartial(double[] filter);

    /**
     * フィルタを属性として持ち,
     * {@code signal -> (フィルタ畳み込み結果)}
     * という変換を表す.
     * 
     * <p>
     * イミュータブルで関数的である.
     * </p>
     * 
     * <p>
     * パッケージ内に隠ぺいする.
     * </p>
     */
    public static interface PartialApplied {

        /**
         * 並列化を自動判定して {@link #compute(double[], boolean)} メソッドを実行する.
         * 
         * <p>
         * 仕様は {@link #compute(double[], boolean)} メソッドに従う.
         * </p>
         * 
         * @param signal シグナル
         * @return 畳み込みの結果
         * @throws IllegalArgumentException
         *             {@link #compute(double[], boolean)} の通り
         * @throws NullPointerException
         *             {@link #compute(double[], boolean)} の通り
         */
        public abstract double[] compute(double[] signal);

        /**
         * 与えたシグナルに対して, フィルタによる畳み込みを適用する. <br>
         * 畳み込みは外部に0埋めして行う.
         * 
         * <p>
         * この処理は並列計算でき, それをするかどうかは引数 {@code parallel} で指定する.
         * </p>
         * 
         * <p>
         * シグナルサイズは1以上でなければならない.
         * </p>
         * 
         * @param signal シグナル
         * @param parallel 並列計算するかどうか
         * @return 畳み込みの結果
         * @throws IllegalArgumentException 引数が不適の場合
         * @throws NullPointerException 引数がnullの場合
         */
        public abstract double[] compute(double[] signal, boolean parallel);
    }
}
