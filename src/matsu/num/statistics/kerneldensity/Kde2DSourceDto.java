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

/**
 * 2次元のカーネル密度推定 ({@link KernelDensity2D}) のデータソースを表現する転送用クラス.
 * 
 * <p>
 * データソース: <br>
 * (x<sub>0</sub>, y<sub>0</sub>),
 * (x<sub>1</sub>, y<sub>1</sub>), ... <br>
 * をこのクラスでは <br>
 * {@code x} = {x<sub>0</sub>, x<sub>1</sub>, ... } <br>
 * {@code y} = {y<sub>0</sub>, y<sub>1</sub>, ... } <br>
 * と表現する.
 * </p>
 * 
 * <p>
 * データの転送にのみ使用されるため, ミュータブルに設計されている. <br>
 * 公開APIとしては,
 * {@link KernelDensity2D.Factory#createOf(Kde2DSourceDto)}
 * の引数としてのみ使用される. <br>
 * ユーザーは, コンストラクタにより固定サイズ ({@link #size}) のインスタンスを生成し,
 * フィールド {@link #x}, {@link #y} の要素を置き換えることでデータソースを構築する.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class Kde2DSourceDto {

    /**
     * データソースの個数.
     */
    public final int size;

    /**
     * データソースの x 座標値を表す配列. <br>
     * 配列の長さは {@link #size} である.
     */
    public final double[] x;

    /**
     * データソースの y 座標値を表す配列. <br>
     * 配列の長さは {@link #size} である.
     */
    public final double[] y;

    /**
     * データソースの個数 ({@code size}) を与えてインスタンスを生成する. <br>
     * 唯一のコンストラクタであり, データソースは全て {@code 0d} で初期化される.
     * 
     * <p>
     * {@code size >= 1} でなければならない. <br>
     * そうでないならば, 例外がスローされる.
     * </p>
     * 
     * @param size データソースの個数
     * @throws IllegalArgumentException size が不適な場合
     */
    public Kde2DSourceDto(int size) {
        super();

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "size is invalid: size = %s".formatted(size));
        }

        this.size = size;
        this.x = new double[size];
        this.y = new double[size];
    }

    /**
     * 内部から呼ばれるコピーコンストラクタ.
     */
    private Kde2DSourceDto(Kde2DSourceDto src) {
        super();
        this.size = src.size;
        this.x = src.x.clone();
        this.y = src.y.clone();
    }

    /**
     * このインスタンスのコピーを生成して返す.
     * 
     * @return {@code this} のコピー
     */
    public Kde2DSourceDto copy() {
        return new Kde2DSourceDto(this);
    }
}
