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

import matsu.num.statistics.kerneldensity.KdeGrid2dDto;

/**
 * 2次元のカーネル密度推定の結果フォーマッター.
 * 
 * <p>
 * フォーマットした結果の型は, 型パラメータで定められる.
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
 * @param <T> フォーマットした結果を表す型
 */
public abstract class Kde2dFormatter<T> {

    /**
     * 唯一のコンストラクタ. <br>
     * パッケージ外での継承を禁止するため, コンストラクタを公開しない.
     */
    Kde2dFormatter() {
        super();
    }

    /**
     * 与えられたDTOの内容をフォーマットする.
     * 
     * @implSpec
     *               引数のDTOはミュータブルであるため, 中身を書き換えないようにしなければならない.
     * @param dto フォーマットの元となるDTO
     * @return 変換後
     * @throws NullPointerException 引数が null の場合
     */
    abstract T format(KdeGrid2dDto dto);
}
