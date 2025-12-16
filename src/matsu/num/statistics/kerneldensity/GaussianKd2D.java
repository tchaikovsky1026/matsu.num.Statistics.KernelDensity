/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.16
 */
package matsu.num.statistics.kerneldensity;

import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;

/**
 * ガウシアンをカーネル関数とする, 2次元のカーネル密度推定.
 * 
 * <p>
 * {@link GaussianKd2D} によるカーネル密度推定では,
 * {@link GaussianKd2D.BandWidthRule BandWidthRule}
 * と
 * {@link GaussianKd2D.ResolutionRule ResolutionRule}
 * をオプションとして指定する. <br>
 * {@link GaussianKd2D.BandWidthRule BandWidthRule}
 * は, カーネル関数のバンド幅の計算に関するルールである. <br>
 * {@link GaussianKd2D.ResolutionRule ResolutionRule}
 * は, 結果出力の分解能に関するルールである.
 * </p>
 * 
 * <p>
 * {@link GaussianKd2D} の生成を行うのは, {@link GaussianKd2D.Factory} クラスのインスタンスである.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class GaussianKd2D implements KernelDensity2D {

    /**
     * 結果出力のメッシュの各軸方向の最大値(概算).
     */
    private static final int MAX_MESH = 500;

    private final BandWidthRule bandWidthRule;
    private final ResolutionRule resolutionRule;
    private final FilterZeroFillingConvolutionFacade convolution;

    private final Kde2DSourceDto source;

    /**
     * Xのカーネルバンド幅, Double.MIN_NORMAL以上である.
     */
    private final double bandWidthX;

    /**
     * Yのカーネルバンド幅, Double.MIN_NORMAL以上である.
     */
    private final double bandWidthY;

    /**
     * 非公開の唯一のコンストラクタ. <br>
     * {@link Factory#createOf(double[])} から呼ばれるために用意. <br>
     * 引数は, {@link Factory#createOf(double[])} の契約を満たした状態で渡される.
     * 
     * <p>
     * 配列はこのコンストラクタ内部ではコピーされない. <br>
     * 配列の要素が変更される可能性がある場合, 呼び出しもとでコピーを取らなければならない.
     * </p>
     */
    private GaussianKd2D(Kde2DSourceDto source, GaussianKd2D.Factory factory) {
        super();

        this.bandWidthRule = factory.bandWidthRule;
        this.resolutionRule = factory.resolutionRule;
        this.convolution = new FilterZeroFillingConvolutionFacade(factory.effectiveCyclicConvolution);

        this.source = source;
        this.bandWidthX = Math.max(
                bandWidthRule.computeBandwidth(source.x),
                1E-300);
        this.bandWidthY = Math.max(
                bandWidthRule.computeBandwidth(source.y),
                1E-300);
    }

    /**
     * @throws NullPointerException {@inheritDoc }
     */
    @Override
    public KdeGrid2dDto evaluateIn(Range rangeX, Range rangeY) {
        // resolutionScale のデフォルトは定数だが, 範囲が広すぎる場合は粗くする.
        // XYは異なるフィルタを使用する: 片方の「粗さ」の影響がもう片方に伝播しないようにするため
        final double filterResolutionScaleX =
                Math.max(
                        resolutionRule.resolutionScale,
                        rangeX.halfWidth() / (MAX_MESH * 0.5d * bandWidthX));

        final double filterResolutionScaleY =
                Math.max(
                        resolutionRule.resolutionScale,
                        rangeY.halfWidth() / (MAX_MESH * 0.5d * bandWidthY));

        final double resolutionX = bandWidthX * filterResolutionScaleX;
        final double resolutionY = bandWidthY * filterResolutionScaleY;

        // フィルタを計算し, フィルタ畳み込みを用意
        final double[] filterOneSideX = GaussianFilterComputation.compute(filterResolutionScaleX);
        final double[] filterOneSideY = GaussianFilterComputation.compute(filterResolutionScaleY);
        UnaryOperator<double[]> convToSignalX = convolution.applyPartial(filterOneSideX);
        UnaryOperator<double[]> convToSignalY = convolution.applyPartial(filterOneSideY);

        int extendSizeX = filterOneSideX.length - 1;
        int extendSizeY = filterOneSideY.length - 1;
        final Mesh2D mesh2d = new Mesh2D(
                rangeX, rangeY, resolutionX, resolutionY, extendSizeX, extendSizeY, source);
        final double[][] weight = mesh2d.weight;
        final int lenX = mesh2d.extendX.length;
        final int lenY = mesh2d.extendY.length;

        // 各Xについて, y方向にConv
        double[][] convY = new double[lenX][];
        for (int j = 0; j < lenX; j++) {
            convY[j] = convToSignalY.apply(weight[j]);
        }

        // 転置 -> x方向にConv -> 転置
        double[][] convXY = new double[lenX][lenY];
        for (int k = 0; k < lenY; k++) {
            // y:k についてx方向を配列化
            double[] signal_k = new double[lenX];
            for (int j = 0; j < lenX; j++) {
                signal_k[j] = convY[j][k];
            }

            // y:k のconv
            double[] convX_k = convToSignalX.apply(signal_k);

            // (x,y) 配列に代入
            for (int j = 0; j < lenX; j++) {
                convXY[j][k] = convX_k[j];
            }
        }

        return new KdeGrid2dDto(mesh2d.x, mesh2d.y, mesh2d.reduceSize(convXY));
    }

    /**
     * {@link GaussianKd2D} のファクトリを扱う.
     * 
     * <p>
     * {@link GaussianKd2D} は,
     * {@link EffectiveCyclicConvolution} のインスタンスをインジェクションすることで高速に動作する. <br>
     * このファクトリは, インジェクションを
     * {@link #withConvolutionBy(EffectiveCyclicConvolution)}
     * メソッドにより行う.
     * </p>
     */
    public static final class Factory implements KernelDensity2D.Factory {

        private final BandWidthRule bandWidthRule;
        private final ResolutionRule resolutionRule;
        private final EffectiveCyclicConvolution effectiveCyclicConvolution;

        /**
         * 唯一の非公開コンストラクタ.
         * 
         * @throws NullPointerException 引数にnullが含まれる場合
         */
        private Factory(BandWidthRule bandWidthRule, ResolutionRule resolutionRule,
                EffectiveCyclicConvolution effectiveCyclicConvolution) {
            super();

            this.bandWidthRule = Objects.requireNonNull(bandWidthRule);
            this.resolutionRule = Objects.requireNonNull(resolutionRule);
            this.effectiveCyclicConvolution = effectiveCyclicConvolution;
        }

        /**
         * @throws IllegalArgumentException {@inheritDoc }
         * @throws NullPointerException {@inheritDoc }
         */
        @Override
        public GaussianKd2D createOf(Kde2DSourceDto source) {
            Kde2DSourceDto srcCopy = source.copy();
            if (!KernelDensity2D.Factory.validateSource(srcCopy)) {
                throw new IllegalArgumentException("illegal: source is invalid");
            }
            return new GaussianKd2D(srcCopy, this);
        }

        /**
         * オプションである {@link EffectiveCyclicConvolution}
         * を与えたものに変更し, 新しいインスタンスとして返す. <br>
         * {@code null} を与えた場合,
         * {@link EffectiveCyclicConvolution} を使用しない動作となる.
         * 
         * @param other インジェクションするインスタンス
         *            ({@code null} を許容)
         * @return 置き換えられた新しい {@code Factory} インスタンス
         */
        public Factory withConvolutionBy(EffectiveCyclicConvolution other) {
            return new Factory(bandWidthRule, resolutionRule, other);
        }

        /**
         * デフォルトルールの {@link Factory GaussianKd1D.Factory} を返す.
         * 
         * <p>
         * デフォルトルールは,
         * {@link BandWidthRule#STANDARD BandWidthRule.SCOTT_RULE},
         * {@link ResolutionRule#STANDARD ResolutionRule.STANDARD}
         * である.
         * </p>
         * 
         * @return デフォルトルールのファクトリ
         */
        public static Factory withDefaultRule() {
            return of(BandWidthRule.STANDARD, ResolutionRule.STANDARD);
        }

        /**
         * 推定ルールを与えて {@link Factory GaussianKd1D.Factory} を構築する.
         * 
         * @param bandWidthRule バンド幅に関するルール
         * @param resolutionRule 空間分解能に関するルール
         * @return 指定したルールを持つファクトリ
         * @throws NullPointerException 引数にnullが含まれる場合
         */
        public static Factory of(BandWidthRule bandWidthRule, ResolutionRule resolutionRule) {
            return new Factory(bandWidthRule, resolutionRule, null);
        }
    }

    /**
     * ガウシアン2次元のカーネル密度推定での, バンド幅の設定ルールを扱う列挙型.
     * 
     * <p>
     * <i>
     * <u>
     * 将来のバージョンで列挙定数が追加される可能性がある. <br>
     * モジュール外で, {@code switch} 文 &middot; {@code switch} 式,
     * {@code if} 文での分岐に使用することを強く非推奨とする. <br>
     * やむを得ず {@code switch} 文 &middot; {@code switch} 式に使用する場合は,
     * 将来の拡張に備えて {@code default} 節を必ず記述すること. <br>
     * インスタンスをシリアライズすることは, 強く非推奨である.
     * </u>
     * </i>
     * </p>
     */
    public static enum BandWidthRule {

        /**
         * 標準のバンド幅計算ルールを表すシングルトンインスタンス.
         */
        STANDARD(da -> Math.min(
                DoubleValueUtil.std(da) / Math.pow(da.length, 1d / 6),
                Double.MAX_VALUE));

        /**
         * データ列を与えてバンド幅を返す関数.
         * 
         * <p>
         * 関数の引数には, サイズは1以上であり, 有限値であることが保証されている配列が与えられるとしてよい. <br>
         * 戻り値は0以上の有限の数である必要がある.
         * </p>
         */
        private final ToDoubleFunction<double[]> bandwidthComputer;

        private BandWidthRule(ToDoubleFunction<double[]> bandwidthComputer) {
            this.bandwidthComputer = bandwidthComputer;
        }

        /**
         * ソースとなるデータ点を与えて, バンド幅を計算する. <br>
         * 戻り値は, 0以上の有限の数である.
         * 
         * <p>
         * クラス外から呼ばれることは想定されていないので, 非公開である. <br>
         * 引数は, サイズは1以上であり, 有限値であることが保証されている.
         * </p>
         * 
         * @param source ソース, サイズは1以上の有限値配列
         * @return バンド幅, 0以上の数
         */
        double computeBandwidth(double[] source) {
            return this.bandwidthComputer.applyAsDouble(source);
        }
    }

    /**
     * ガウシアン2次元のカーネル密度推定での, 計算の空間分解能を扱う列挙型.
     * 
     * <p>
     * <i>
     * <u>
     * 将来のバージョンで列挙定数が追加される可能性がある. <br>
     * モジュール外で, {@code switch} 文 &middot; {@code switch} 式,
     * {@code if} 文での分岐に使用することを強く非推奨とする. <br>
     * やむを得ず {@code switch} 文 &middot; {@code switch} 式に使用する場合は,
     * 将来の拡張に備えて {@code default} 節を必ず記述すること. <br>
     * インスタンスをシリアライズすることは, 強く非推奨である.
     * </u>
     * </i>
     * </p>
     */
    public static enum ResolutionRule {

        /**
         * 標準の空間分解能を表すシングルトンインスタンス.
         */
        STANDARD(0.375d),

        /**
         * 低い空間分解能を表すシングルトンインスタンス.
         */
        LOW(0.75d),

        /**
         * 高い空間分解能を表すシングルトンインスタンス.
         */
        HIGH(0.15d);

        /**
         * 分解能スケール.
         */
        final double resolutionScale;

        /**
         * @param resolutionScale 分解能スケール, 1以下
         */
        private ResolutionRule(double resolutionScale) {
            this.resolutionScale = resolutionScale;
        }
    }
}
