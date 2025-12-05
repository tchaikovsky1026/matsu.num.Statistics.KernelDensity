/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.5
 */
package matsu.num.statistics.kerneldensity;

/**
 * テスト用に提供される, {@link KdeGrid2dDto} の公開ファクトリ.
 * 
 * @author Matsuura Y.
 */
public final class KdeGrid2dDtoFactoryForTesting {

    private KdeGrid2dDtoFactoryForTesting() {
        // インスタンス化不可
        throw new AssertionError();
    }

    /**
     * 与えた引数で生成する.
     * 
     * <p>
     * 空であってはならない. <br>
     * x.length = density.length でなければならない. <br>
     * 任意の i について, y.length = density[i].length でなければならない. <br>
     * 有限でなければならない. <br>
     * x, y は昇順(できれば等間隔) でなければならない. <br>
     * : {@literal -ea} オプションでアサーションが出る.
     * </p>
     * 
     * @param x x
     * @param y y
     * @param density density
     * @return KdeGrid2dDto
     * @throws NullPointerException 引数がnullの場合
     */
    public static KdeGrid2dDto instanceOf(double[] x, double[] y, double[][] density) {
        return new KdeGrid2dDto(x, y, density);
    }
}
