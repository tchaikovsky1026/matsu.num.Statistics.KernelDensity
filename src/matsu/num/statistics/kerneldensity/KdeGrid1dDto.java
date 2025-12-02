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

import java.util.Arrays;

/**
 * {@link KernelDensity1D} による1次元カーネル密度推定の結果を返すための転送用クラス.
 * 
 * <p>
 * 1次元カーネル密度推定の結果は,
 * (<i>x</i>, {@code density}) のような値である. <br>
 * ここで, 確率値 {@code density} は連続確率変数の確率密度に対応するが,
 * 単位は [x]<sup>-1</sup> でなく, 無次元量である:
 * 配列の (範囲外を含む) 全空間での {@code density} の総和が 1 になるような量である.
 * </p>
 * 
 * <p>
 * 結果の転送にのみ使用される. <br>
 * 可変なフィールドを持っているが, モジュール内からは参照しないため,
 * モジュール外で書き換えることは問題ない.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class KdeGrid1dDto {

    /**
     * 結果のサイズを表す {@code int} 値.
     */
    public final int size;

    /**
     * 結果の <i>x</i> の列. <br>
     * 長さは {@link #size} に一致する.
     */
    public final double[] x;

    /**
     * 結果の確率値の列. <br>
     * 長さは {@link #size} に一致する.
     */
    public final double[] density;

    /**
     * 唯一のコンストラクタ.
     * 
     * <p>
     * 空であってはならない. <br>
     * x.length = density.length でなければならない. <br>
     * 有限でなければならない. <br>
     * x は昇順(できれば等間隔) でなければならない.
     * </p>
     * 
     * @param x x
     * @param density density
     * @throws NullPointerException 引数がnullの場合
     */
    KdeGrid1dDto(double[] x, double[] density) {
        super();

        assert isValid(x, density) : "isValid(x, density)";

        this.size = x.length;
        this.x = x;
        this.density = density;
    }

    /**
     * 引数が適切かどうかを判定するメソッド. <br>
     * アサーション内で呼ばれる. <br>
     * 以下をすべて満たした場合にのみ true を返す.
     * 
     * <p>
     * 空でない. <br>
     * x.length = density.length である. <br>
     * 値は有限である. <br>
     * x は昇順である.
     * </p>
     * 
     * @param x x
     * @param density density
     * @throws NullPointerException 引数がnullの場合
     */
    private static boolean isValid(double[] x, double[] density) {
        return x.length > 0
                && x.length == density.length
                && Arrays.stream(x).allMatch(Double::isFinite)
                && Arrays.stream(density).allMatch(Double::isFinite)
                && DoubleValueUtil.isSorted(x);
    }
}
