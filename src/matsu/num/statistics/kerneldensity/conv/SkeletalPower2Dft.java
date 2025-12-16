/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.15
 */
package matsu.num.statistics.kerneldensity.conv;

/**
 * {@link Power2Dft} インターフェースの骨格実装クラス.
 * 
 * <p>
 * {@link Power2Dft} インターフェースの各種メソッドの引数のバリデーションをこの骨格実装で行う. <br>
 * 実装ユーザーは, このクラスに定義された抽象メソッドを実装すればよい.
 * </p>
 * 
 * @apiNote
 *              モジュール内で用意されたこの抽象クラスのサブクラスは,
 *              将来的にこの抽象クラスを継承しなくなる可能性がある. <br>
 *              したがってモジュール外では, この抽象クラスを変数宣言の型として用いてはならず,
 *              {@code instanceof} でサブタイプ判定, キャストなどはしてはならない.
 * 
 * @author Matsuura Y.
 */
abstract class SkeletalPower2Dft implements Power2Dft {

    private final int maxAcceptableSize;

    /**
     * 唯一のコンストラクタ.
     * 
     * <p>
     * {@link Power2Dft#maxAcceptableSize()} で返す値を初期化するために,
     * その値の2を底とする対数 {@code lbMaxAcceptableSize} を与える. <br>
     * {@code maxAcceptableSize} は 2<sup>25</sup> から 2<sup>30</sup> の範囲であるため,
     * {@code lbMaxAcceptableSize} は25以上30以下である.
     * </p>
     * 
     * @implSpec
     *               {@code lbMaxAcceptableSize} は具象クラスで閉じるようにするべきである. <br>
     *               (すなわち, 公開APIに {@code lbMaxAcceptableSize} の入力を求めるべきではない.)
     * 
     * @param lbMaxAcceptableSize maxAcceptableSize の2を底とする対数の値
     * @throws AssertionError {@code lbMaxAcceptableSize} は25以上30以下でない場合
     */

    /*
     * 将来, この抽象クラスが public になった場合,
     * コンストラクタのアクセス修飾子は protected とする.
     */
    SkeletalPower2Dft(int lbMaxAcceptableSize) {
        super();
        if (!(25 <= lbMaxAcceptableSize && lbMaxAcceptableSize <= 30)) {
            throw new AssertionError("error");
        }
        this.maxAcceptableSize = 1 << lbMaxAcceptableSize;
    }

    @Override
    public final int maxAcceptableSize() {
        return maxAcceptableSize;
    }

    /**
     * @apiNote {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public final double[][] dft(double[][] signal) {
        if (signal.length != 2) {
            throw new IllegalArgumentException("signal.length != 2");
        }
        double[] sr = signal[0];
        double[] si = signal[1];
        validateSignal(sr, si);

        return transform(sr, si, false);
    }

    /**
     * @apiNote {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public final double[][] idft(double[][] signal) {
        if (signal.length != 2) {
            throw new IllegalArgumentException("signal.length != 2");
        }
        double[] sr = signal[0];
        double[] si = signal[1];
        validateSignal(sr, si);

        return transform(sr, si, true);
    }

    /**
     * {@code real}, {@code imaginary} の配列サイズが適切かどうかを調べる. <br>
     * 適切でない場合は例外 ({@link IllegalArgumentException},
     * {@link NullPointerException}) をスローする.
     */
    private void validateSignal(double[] sr, double[] si) {
        int size = sr.length;
        if (si.length != size) {
            throw new IllegalArgumentException("real.length != imaginary.length");
        }
        if (size > 0 && (size & (size - 1)) != 0) {
            throw new IllegalArgumentException("NOT power of 2");
        }
        if (size > maxAcceptableSize()) {
            throw new IllegalArgumentException("size is too large: size = " + size);
        }
    }

    /**
     * 離散 Fourier 変換, 逆変換の両方を担う内部向け抽象メソッド. <br>
     * 変換と逆変換の切り替えは, {@code isIt:boolean} で行う.
     * 
     * <p>
     * {@link #dft(double[][])}, {@link #idft(double[][])} の変換の実体である. <br>
     * それらが呼ばれたとき, 引数のバリデーションが行われ, 正当な場合にのみこのメソッドがコールされる. <br>
     * シグナルの実部と虚部のサイズは等しく, 2の累乗であり {@link #maxAcceptableSize()} 以下であることが保証される.
     * <br>
     * 出力結果 {@code result:double[][]} は {@code double[2][size]} であり,
     * {@code result[0]} が実部を,
     * {@code result[1]} が虚部を表す.
     * </p>
     * 
     * @apiNote
     *              いかなる場合であっても, このクラス以外からこのメソッドを直接コールしてはならない.
     * 
     * @implSpec
     *               引数は呼び出し元の配列の参照と同一であるので, 値を書き換えてはならない. <br>
     *               継承先でアクセス修飾子を緩和してはならない.
     * 
     * @param signal_re シグナルの実部
     * @param signal_im シグナルの虚部
     * @param isIt 変換と逆変換の切り替えを行う {@code boolean},
     *            {@code false} の場合は変換を, {@code true} の場合は逆変換を行う
     * @return 変換結果
     */

    /*
     * 将来, この抽象クラスが public になった場合,
     * この抽象メソッドのアクセス修飾子は protected とする.
     */
    abstract double[][] transform(double[] signal_re, double[] signal_im, boolean isIt);

}
