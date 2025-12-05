/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.4
 */
package matsu.num.statistics.kerneldensity.output;

import matsu.num.statistics.kerneldensity.KdeGrid1dDto;
import matsu.num.statistics.kerneldensity.KernelDensity1D;
import matsu.num.statistics.kerneldensity.Range;

/**
 * 文字列化に特化した, 1次元のカーネル密度推定の結果を扱うクラス.
 * 
 * <p>
 * このクラスは, 1次元のカーネル密度推定の結果を文字列化するために用意されている. <br>
 * {@link KernelDensity1D#evaluateIn(Range) } メソッドにより生成された推定結果を属性として持ち,
 * 多種フォーマットでの文字列出力を行う.
 * </p>
 * 
 * <p>
 * このクラスはイミュータブルであり, 関数的である.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class TextKdeResult1D {

    private final KdeGrid1dDto dto;

    /**
     * 非公開のコンストラクタ.
     * 
     * <p>
     * 外部からDTOを与えるのは危険なので, 非公開である. <br>
     * このインスタンスの生成には, DTOを生成する機構を与えるようにする
     * ({@link #evaluate(KernelDensity1D, Range)} 経由で呼ばれる).
     * </p>
     * 
     * @param dto 文字列出力の元となるカーネル密度推定の結果
     */
    private TextKdeResult1D(KdeGrid1dDto dto) {
        super();
        this.dto = dto;
    }

    /**
     * 与えられたフォーマッターにより推定結果を文字列化する.
     * 
     * <p>
     * 文字列は改行を含まず, 各行を {@link String} とした {@link Iterable} で表す.
     * </p>
     * 
     * @param formatter フォーマッター
     * @return 推定結果の文字列変換
     */
    public Iterable<String> formatted(Kde1dTextFormatter formatter) {
        return formatter.format(dto);
    }

    /**
     * 1次元のカーネル密度推定を実行し, 文字列化が可能な推定結果オブジェクトを返す.
     * 
     * <p>
     * 引数の {@link KernelDensity1D}, {@link Range} インスタンスをもとに
     * {@link KernelDensity1D#evaluateIn(Range)}
     * を実行し, その結果に相当するインスタンスを生成する.
     * </p>
     * 
     * @param evaluator 1次元のカーネル密度推定
     * @param range 推定する区間
     * @return 推定結果
     * @throws NullPointerException 引数にnullが含まれる場合
     */
    public static TextKdeResult1D evaluate(KernelDensity1D evaluator, Range range) {
        return new TextKdeResult1D(evaluator.evaluateIn(range));
    }
}
