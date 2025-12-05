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
 * テスト用に提供される, {@link KdeGrid1dDto} の公開ファクトリ.
 * 
 * @author Matsuura Y.
 */
public final class KdeGrid1dDtoFactoryForTesting {

    private KdeGrid1dDtoFactoryForTesting() {
        // インスタンス化不可
        throw new AssertionError();
    }

    /**
     * 与えた引数で生成する.
     * 
     * <p>
     * 空であってはならない. <br>
     * x.length = density.length でなければならない. <br>
     * 有限でなければならない. <br>
     * x は昇順(できれば等間隔) でなければならない. <br>
     * : {@literal -ea} オプションでアサーションが出る.
     * </p>
     * 
     * @param x x
     * @param density density
     * @return KdeGrid1dDto
     * @throws NullPointerException 引数がnullの場合
     */
    public static KdeGrid1dDto instanceOf(double[] x, double[] density) {
        return new KdeGrid1dDto(x, density);
    }
}
