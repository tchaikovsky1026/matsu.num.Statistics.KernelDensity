/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.15
 */
package matsu.num.statistics.kerneldensity;

import java.util.function.UnaryOperator;

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
 * シグナルの片方が固定されているようなケースに対応するため,
 * このインターフェースは, 明示的な {@link #apply(double[], double[])} のほかに,
 * シグナル <i>f</i> を先に定めてカリー化した関数を返す {@link #applyPartial(double[])} を用意する.
 * </p>
 * 
 * <p>
 * このインターフェースは, モジュール内で使用するために外部からインジェクションされるために用意されている. <br>
 * (実装提供者を除いて) モジュール外からメソッドコール<b>してはならない</b>.
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
     * 与えた値を下回らない最小値を返す. <br>
     * 値を返せない場合は例外がスローされる.
     * 
     * <p>
     * 次が {@code true} である. <br>
     * {@code calcAcceptableSize(calcAcceptableSize(v)) == calcAcceptableSize(v)}
     * </p>
     * 
     * @implSpec
     *               適切に使用される限り, 0や負の数が与えられることはないが,
     *               おそらく 1 を返すのが良い. <br>
     *               例外をスローしてはならない.
     * 
     *               <p>
     *               例外をスローしない場合, 戻り値の長さを持つ配列は {@link #applyPartial(double[])}
     *               の引数として適切である. <br>
     *               逆に, {@link #applyPartial(double[])} が受け入れ可能な長さ,
     *               およびそれ以下の値が与えられた場合, 例外をスローしてはならない.
     *               </p>
     * 
     * @param lower 受け入れ可能サイズの下限 (inclusive)
     * @return 受け入れ可能サイズ
     * @throws IllegalArgumentException 受け入れ可能サイズが返せない場合
     */
    public abstract int calcAcceptableSize(int lower);

    /**
     * 畳み込みの片方のシグナル {@code f} を与えて,
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
     * {@code f} はメソッドコール中は変更してはならない
     * (変更された場合は, 結果は保証されない).
     * </p>
     * 
     * <p>
     * シグナルの長さは, 2<sup>25</sup>までは必ず対応している
     * (これ以上の長さが与えられても, 直ちに例外をスローするわけではない).
     * </p>
     * 
     * <p>
     * 戻り値である {@link UnaryOperator} に関するさらなる契約は,
     * "APIのノート" を参照すること.
     * </p>
     * 
     * <p>
     * <u><i><b>モジュール内での利用</b></i></u> <br>
     * モジュール内での利用では, 上記の条件は必ず満たされている. <br>
     * さらに, {@code f} の要素を変更しないことが保証されるため,
     * 実装において引数 {@code f} をコピーする必要は無い. <br>
     * <u><i>
     * モジュール内では, このように利用されなければならない.
     * </i></u>
     * </p>
     * 
     * @apiNote
     *              <b>戻り値 {@link UnaryOperator}</b> <br>
     *              戻り値の {@link UnaryOperator} は,
     *              {@link UnaryOperator#apply(Object) apply(g)}
     *              メソッドのコールにより畳み込み: ({@code f*g}) を計算する. <br>
     *              コールしたときに
     *              {@code f.length == g.length}
     *              が {@code true} でない場合は例外 ({@link IllegalArgumentException})
     *              がスローされる.
     * 
     *              <p>
     *              {@code g} はメソッドコール中は変更してはならない
     *              (変更された場合は, 結果は保証されない).
     *              </p>
     * 
     *              <p>
     *              <u><i><b>{@link UnaryOperator} のモジュール内での利用</b></i></u> <br>
     *              モジュール内での利用では, 上記の条件は必ず満たされている. <br>
     *              さらに, {@code g} の要素を変更しないことが保証されるため,
     *              実装において引数 {@code g} をコピーする必要は無い. <br>
     *              <u><i>
     *              モジュール内では, このように利用されなければならない. </i></u>
     *              </p>
     * 
     *              <p>
     *              <b>{@link UnaryOperator} の実装要件:</b><br>
     *              メソッドの説明に従って実装しなければならない. <br>
     *              すなわち, 引数のチェックを必ず行い, 不適切の場合は例外をスローしなければならない.
     *              </p>
     * 
     * @implSpec
     *               メソッドの説明に従って実装しなければならない. <br>
     *               すなわち, 引数のチェックを必ず行い, 不適切の場合は例外をスローしなければならない.
     * 
     * @param f {@code f}
     * @return {@code g -> (f*g)} なる関数
     * @throws IllegalArgumentException 引数の長さが受け入れ可能でない場合,
     *             長さが大きすぎる場合
     * @throws NullPointerException 引数に null が含まれる場合
     */
    public abstract UnaryOperator<double[]> applyPartial(double[] f);

    /**
     * 与えた {@code f}, {@code g} について, 巡回畳み込みを計算する. <br>
     * 計算コストは O(NlogN) 程度である.
     * 
     * <p>
     * {@code this.applyPartial(f).apply(g)}
     * のコールと同一のメソッド契約を持つ. <br>
     * すなわち, 以下のようになる.
     * </p>
     * 
     * <p>
     * 与える {@code f}, {@code g} の長さは同一かつ, 受け入れ可能でなければならない. <br>
     * すなわち, <br>
     * {@code f.length == g.length
     *  && f.length == calcAcceptableSize(f.length)} <br>
     * が {@code true} であることが必要である.
     * </p>
     * 
     * <p>
     * {@code f}, {@code g} はメソッドコール中は変更してはならない
     * (変更された場合は, 結果は保証されない).
     * </p>
     * 
     * <p>
     * 与えられる {@code f}, {@code g} はサイズは, 2<sup>25</sup>までは必ず対応している
     * (これ以上の長さが与えられても, 直ちに例外をスローするわけではない).
     * </p>
     * 
     * <p>
     * <u><i><b>モジュール内での利用</b></i></u> <br>
     * モジュール内での利用では, 上記の条件は必ず満たされている. <br>
     * さらに, {@code f}, {@code g} の要素を変更しないことが保証されるため,
     * 実装において引数 {@code f}, {@code g} をコピーする必要は無い. <br>
     * <u><i>
     * モジュール内では, このように利用されなければならない.
     * </i></u>
     * </p>
     * 
     * @implSpec
     *               メソッドの説明に従って実装しなければならない. <br>
     *               デフォルト実装は, <br>
     *               {@code return this.applyPartial(f).apply(g);} <br>
     *               であり, 多くの場合はデフォルト実装で十分である.
     * 
     * @param f {@code f}
     * @param g {@code g}
     * @return 畳み込みの結果
     * @throws IllegalArgumentException 引数の長さが受け入れ可能でない場合,
     *             {@code f}, {@code g} の長さが異なる場合,
     *             長さが大きすぎる場合
     * @throws NullPointerException 引数に null が含まれる場合
     */
    public default double[] apply(double[] f, double[] g) {
        return this.applyPartial(f).apply(g);
    }

}
