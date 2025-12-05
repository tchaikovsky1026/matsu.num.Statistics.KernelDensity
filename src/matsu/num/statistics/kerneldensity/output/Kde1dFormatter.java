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

/**
 * 1次元のカーネル密度推定の結果を文字列化するフォーマッター.
 * 
 * <p>
 * 文字列化した結果の型は, 型パラメータで定められる.
 * </p>
 * 
 * <p>
 * パッケージ外ではこのクラスを継承することはできない. <br>
 * 適切に実装された具象クラスや生成メソッドが提供される.
 * </p>
 * 
 * @implSpec
 *               パッケージ内で継承する場合, クラスが {@code final} である場合を除いて,
 *               コンストラクタを公開してはいけない. <br>
 *               (継承可能なクラスのコストラクタを公開した場合, パッケージ外で継承される可能性がある.)
 * 
 * @author Matsuura Y.
 * @param <T> 文字列化した結果を表す型
 */
public abstract class Kde1dFormatter<T> {

    /**
     * 唯一のコンストラクタ. <br>
     * パッケージ外での継承を禁止するため, コンストラクタを公開しない.
     */
    Kde1dFormatter() {
        super();
    }

    /**
     * 与えられたDTOの内容を文字列化する.
     * 
     * @implSpec
     *               引数のDTOはミュータブルであるため, 中身を書き換えないようにしなければならない. <br>
     *               文字列に改行を含んではいけない.
     * @param dto 文字列化の元となるDTO
     * @return 変換後の文字列
     * @throws NullPointerException 引数が null の場合
     */
    abstract T format(KdeGrid1dDto dto);
}
