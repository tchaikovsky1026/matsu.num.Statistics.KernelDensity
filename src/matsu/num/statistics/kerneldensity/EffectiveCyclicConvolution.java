/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.25
 */
package matsu.num.statistics.kerneldensity;

import java.util.function.Function;

/**
 * 巡回畳み込みを効率的に実行するインターフェース.
 * 
 * <p>
 * このインターフェースは, "効率的に"巡回畳み込みを実行することを表す. <br>
 * "効率的に"とは, 計算コストが O(NlogN) 程度であることを表す. <br>
 * 受け入れられるサイズは, 具象クラスで指定可能である.
 * </p>
 * 
 * <p>
 * シグナルの片方が固定されているようなケースで巡回畳み込みを行うことに対応するため,
 * このインターフェースは, 明示的な {@link #apply(double[], double[])} のほかに,
 * f を先に定めてカリー化した関数を返す {@link #applyPartial(double[])} を用意する.
 * </p>
 * 
 * <p>
 * このインターフェースは, モジュール内で使用するために外部からインジェクションされるために用意されている. <br>
 * モジュール外からメソッドコールをするような使い方は不適である.
 * </p>
 * 
 * @implSpec
 *               このインターフェースの実装においては,
 *               畳み込みの計算コストが O(NlogN) 程度になるように実装されなければならない.
 * 
 * @author Matsuura Y.
 */
public interface EffectiveCyclicConvolution {

    /**
     * 畳み込みのシグナルとして受け入れ可能なサイズについて,
     * 与えた値を下回らない最小値を返す.
     * 
     * <p>
     * 次が {@code true} である. <br>
     * {@code calcAcceptableSize(calcAcceptableSize(v)) == calcAcceptableSize(v)}
     * </p>
     * 
     * @param lower 受け入れ可能サイズの下限 (inclusive)
     * @return 受け入れ可能サイズ
     */
    public abstract int calcAcceptableSize(int lower);

    /**
     * 畳み込みの片方のシグナル f を与えて,
     * {@code g -> (f*g)}
     * という関数を返す.
     * 
     * <p>
     * 与えるシグナルの長さは受け入れ可能でなければならない. <br>
     * すなわち, <br>
     * {@code f.length == calcAcceptableSize(f.length)} <br>
     * が {@code true} であることが必要である.
     * </p>
     * 
     * <p>
     * シグナルの長さは, 2<sup>25</sup>までは必ず対応している. <br>
     * (これ以上の長さが与えられても, 直ちに例外をスローするわけではない.)
     * </p>
     * 
     * <p>
     * 戻り値の {@link Function} は,
     * {@link Function#apply(Object) apply(double[] g)}
     * メソッドのコールにより畳み込み: (f*g) を計算する. <br>
     * コールしたときに
     * {@code f.length == g.length}
     * が {@code true} でない場合は例外 ({@link IllegalArgumentException}) がスローされる.
     * </p>
     * 
     * @param f f
     * @return {@code g -> (f*g)} なる関数
     * 
     * @implSpec
     *               戻り値となる {@link Function} の実装において,
     *               引数 f をコピーする必要は無い
     *               (モジュール内での利用においては f は変更されないことを保証する).
     * 
     * @throws IllegalArgumentException 引数の長さが受け入れ可能でない場合,
     *             長さが大きすぎる場合
     * @throws NullPointerException 引数に null が含まれる場合
     */
    public abstract Function<double[], double[]> applyPartial(double[] f);

    /**
     * 与えた f, g について, 巡回畳み込みを計算する. <br>
     * 計算コストは O(NlogN) 程度である.
     * 
     * <p>
     * {@code this.applyPartial(f).apply(g)}
     * のコールと同一のメソッド契約を持つ. <br>
     * すなわち, 以下のようになる.
     * </p>
     * 
     * <p>
     * 与える f, g の長さは同一かつ, 受け入れ可能でなければならない. <br>
     * すなわち, <br>
     * {@code f.length == g.length
     *  && f.length == calcAcceptableSize(f.length)} <br>
     * が {@code true} であることが必要である.
     * </p>
     * 
     * <p>
     * 与えられる f, g はサイズは, 2<sup>25</sup>までは必ず対応している. <br>
     * (これ以上の長さが与えられても, 直ちに例外をスローするわけではない.)
     * </p>
     * 
     * @param f f
     * @param g g
     * @return 畳み込みの結果
     * @throws IllegalArgumentException 引数の長さが受け入れ可能でない場合,
     *             f と g の長さが異なる場合,
     *             長さが大きすぎる場合
     * @throws NullPointerException 引数に null が含まれる場合
     */
    public default double[] apply(double[] f, double[] g) {
        return this.applyPartial(f).apply(g);
    }

}
