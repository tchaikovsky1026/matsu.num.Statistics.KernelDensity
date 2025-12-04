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

/**
 * 1次元のカーネル密度推定の結果を文字列化するフォーマッター.
 * 
 * <p>
 * パッケージ外ではこのクラスを継承することはできない. <br>
 * 適切に実装された具象クラスや生成メソッドが提供される.
 * </p>
 * 
 * @author Matsuura Y.
 */
public abstract class Kde1dTextFormatter {

    /**
     * 唯一のコンストラクタ. <br>
     * パッケージ外での継承を禁止するため, コンストラクタを公開しない.
     */
    Kde1dTextFormatter() {
        super();
    }

    /**
     * 与えられたDTOの内容を文字列化する.
     * 
     * <p>
     * 文字列は改行を含まず, 各行を {@link String} とした {@link Iterable} で表す.
     * </p>
     * 
     * @implSpec
     *               引数のDTOはミュータブルであるため, 中身を書き換えないようにしなければならない. <br>
     *               文字列に改行を含んではいけない.
     * @param dto 文字列化の元となるDTO
     * @return 変換後の文字列
     * @throws NullPointerException 引数が null の場合
     */
    abstract Iterable<String> format(KdeGrid1dDto dto);
}
