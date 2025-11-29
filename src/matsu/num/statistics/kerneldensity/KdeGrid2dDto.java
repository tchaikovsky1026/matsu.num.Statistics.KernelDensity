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
 * {@link KernelDensity2D} による結果を返すための結果転送用クラス.
 * 
 * <p>
 * 結果の転送にのみ使用される. <br>
 * 可変なフィールドを持っているが, モジュール内からは参照しないため,
 * モジュール外で書き換えることは問題ない.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class KdeGrid2dDto {

    /**
     * 結果の <i>x</i> のサイズを表す {@code int} 値.
     */
    public final int sizeX;

    /**
     * 結果の <i>y</i> のサイズを表す {@code int} 値.
     */
    public final int sizeY;

    /**
     * 結果の <i>x</i> の列. <br>
     * 長さは {@link #sizeX} に一致する.
     */
    public final double[] x;

    /**
     * 結果の <i>y</i> の列. <br>
     * 長さは {@link #sizeY} に一致する.
     */
    public final double[] y;

    /**
     * 結果の確率値の列. <br>
     * 構造は {@code density[x_index][y_index]} である. <br>
     * それぞれの配列の長さは {@link #sizeX}, {@link #sizeY} に一致する.
     * 
     * <p>
     * 配列の配列は {@code final} フィールドでないので,
     * mutability に注意.
     * </p>
     */
    public final double[][] density;

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
     * @param y y
     * @param density density
     * @throws NullPointerException 引数がnullの場合
     */
    KdeGrid2dDto(double[] x, double[] y, double[][] density) {
        super();

        assert isValid(x, y, density) : "isValid(x, y, density)";

        this.sizeX = x.length;
        this.sizeY = y.length;
        this.x = x;
        this.y = y;
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
     * 任意の i について, y.length = density[i].length である. <br>
     * 値は有限である. <br>
     * x, y は昇順である.
     * </p>
     * 
     * @param x x
     * @param y y
     * @param density density
     * @throws NullPointerException 引数がnullの場合
     */
    private static boolean isValid(double[] x, double[] y, double[][] density) {
        if (!(x.length > 0 && y.length > 0)) {
            return false;
        }

        if (!(x.length == density.length
                && Arrays.stream(x).allMatch(Double::isFinite)
                && Arrays.stream(y).allMatch(Double::isFinite)
                && DoubleValueUtil.isSorted(x)
                && DoubleValueUtil.isSorted(y))) {
            return false;
        }

        for (int i = 0; i < x.length; i++) {
            if (density[i].length != y.length) {
                return false;
            }
            if (!Arrays.stream(density[i]).allMatch(Double::isFinite)) {
                return false;
            }
        }

        return true;
    }
}
