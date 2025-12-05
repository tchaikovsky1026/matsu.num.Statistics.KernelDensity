/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.5
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
public final class FormattableKdeResult1D {

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
    private FormattableKdeResult1D(KdeGrid1dDto dto) {
        super();
        this.dto = dto;
    }

    /**
     * 与えられたフォーマッターにより推定結果を文字列化する. <br>
     * 出力の形式は型を含め, フォーマッターで指定される.
     * 
     * @param <T> 文字列化した結果を表す型
     * @param formatter フォーマッター
     * @return 推定結果の文字列変換
     */
    public <T> T formatted(Kde1dFormatter<T> formatter) {
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
    public static FormattableKdeResult1D evaluate(KernelDensity1D evaluator, Range range) {
        return new FormattableKdeResult1D(evaluator.evaluateIn(range));
    }
}
