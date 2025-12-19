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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 効率的な実装による, フィルタ畳み込み.
 * 
 * @author Matsuura Y.
 */
final class EffectiveFilterZeroFillingConvolution
        implements FilterZeroFillingConvolution {

    /**
     * 高効率な畳み込みを実行する場合の, フィルタの最低サイズの目安.
     */
    private static final int MIN_FILTER_SIZE_FOR_EFFECTIVE = 100;

    /**
     * 高効率な畳み込みを実行する場合の, (filter * signal)の最低サイズの目安.
     */
    private static final long MIN_FILTER_TIMES_SIGNAL_SIZE_FOR_EFFECTIVE = 500_000L;

    /**
     * 高効率な巡回畳み込み.
     */
    private final EffectiveCyclicConvolution cyclicConvolution;

    /**
     * 非公開のコンストラクタ.
     *
     * <p>
     * {@code null} であってはいけない. <br>
     * 引数は呼び出しもとでチェックすること.
     * </p>
     */
    private EffectiveFilterZeroFillingConvolution(EffectiveCyclicConvolution cyclicConvolution) {
        this.cyclicConvolution = cyclicConvolution;
    }

    /**
     * このクラスの計算方法を使用すべきかどうかを判定する.
     */
    static boolean shouldBeUsed(double[] filter, double[] signal) {
        return filter.length >= MIN_FILTER_SIZE_FOR_EFFECTIVE
                && (long) filter.length * signal.length >= MIN_FILTER_TIMES_SIGNAL_SIZE_FOR_EFFECTIVE;
    }

    /**
     * 巡回畳み込みのインスタンスを与えて, このクラスのインスタンスを返す.
     * 
     * @param cyclicConvolution 巡回畳み込み
     * @return インスタンス
     * @throws NullPointerException 引数が null の場合
     */
    static FilterZeroFillingConvolution instanceOf(
            EffectiveCyclicConvolution cyclicConvolution) {

        return new EffectiveFilterZeroFillingConvolution(
                Objects.requireNonNull(cyclicConvolution));
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public PartialApplied applyPartial(double[] filter) {
        double[] filterCopy = filter.clone();

        if (filterCopy.length == 0) {
            throw new IllegalArgumentException("filter is empty");
        }
        if (!Arrays.stream(filterCopy).allMatch(v -> (Double.isFinite(v) && v >= 0d))) {
            throw new IllegalArgumentException("filter values are invalid");
        }

        return new PartialApplied(filterCopy);
    }

    /**
     * {@link EffectiveFilterZeroFillingConvolution#applyPartial(double[])}
     * の実装.
     */
    private final class PartialApplied implements FilterZeroFillingConvolution.PartialApplied {

        private final ConvolutionExecution convolution;

        /**
         * 非公開コンストラクタ.
         * 引数チェックは行われていない.
         */
        private PartialApplied(double[] filter) {
            super();
            this.convolution = new ConvolutionExecution(filter);
        }

        /**
         * @throws IllegalArgumentException {@inheritDoc}
         * @throws NullPointerException {@inheritDoc}
         */
        @Override
        public double[] compute(double[] signal) {
            return compute(signal, true);
        }

        /**
         * @throws IllegalArgumentException {@inheritDoc}
         * @throws NullPointerException {@inheritDoc}
         */
        @Override
        public double[] compute(double[] signal, boolean parallel) {
            if (signal.length == 0) {
                throw new IllegalArgumentException("signal is empty");
            }

            return convolution.compute(signal, parallel);
        }

        /**
         * フィルタ畳み込みの実体.
         */
        private final class ConvolutionExecution {

            /*
             * フィルタ畳み込みを巡回畳み込みを用いて実現する.
             * 
             * 巡回畳み込みに還元するには, フィルタサイズの3倍以上の信号に分割しなければならない.
             * ここでは, フィルタサイズの6倍以上の長さを畳み込みサイズ(convolutionSize)とする.
             * (オーバーラップは半分程度になる).
             * 
             * 
             * signalのフィルタ畳み込みのうち, ある区間(subList)の結果は,
             * フィルタが有限であるため, subListの前後に(filter.length - 1)
             * だけ拡張した区間の信号を考えれば十分である.
             * 巡回畳み込みで生じるエイリアシングはこの拡張部分にのみ生じるので, そこをカットすればよい.
             * よって,
             * subListLength = convolutionSize - extendSize * 2
             * が, 値を得られるサブリストの長さとなる (extendSize = filter.length - 1).
             * 信号をsubListLengthで分割し, 両側に拡張した範囲(convolutionSize)でsignalから取り出す.
             * フィルタとの畳み込みを行い, 中央のsubListLength分を取り出す.
             */

            private final int convolutionSize;
            private final int extendSize;
            private final int subListLength;

            private final Function<double[], double[]> partialAppliedConv;

            ConvolutionExecution(final double[] filter) {

                convolutionSize = cyclicConvolution.calcAcceptableSize(filter.length * 6);
                extendSize = filter.length - 1;
                subListLength = convolutionSize - extendSize * 2;

                partialAppliedConv =
                        cyclicConvolution.applyPartial(toConvolutionFilter(filter, convolutionSize));
            }

            /**
             * 畳み込みを計算する.
             */
            double[] compute(double[] signal, boolean parallel) {

                if (signal.length == 0) {
                    throw new IllegalArgumentException("signal is empty");
                }
                if (!Arrays.stream(signal).allMatch(v -> (Double.isFinite(v) && v >= 0d))) {
                    throw new IllegalArgumentException("signal values are invalid");
                }

                return new ExecutionInner(signal).compute(parallel);
            }

            /**
             * 与えたフィルタ(片側)を, 畳み込み用に変換する.
             */
            private static double[] toConvolutionFilter(double[] filterOneSide, int convolutionSize) {
                double[] out = Arrays.copyOf(filterOneSide, convolutionSize);

                for (int i = 1; i < filterOneSide.length; i++) {
                    out[convolutionSize - i] = filterOneSide[i];
                }

                return out;
            }

            /**
             * 計算用内部クラス.
             */
            private final class ExecutionInner {

                private final double[] signal;

                /**
                 * 唯一のコンストラクタ. <br>
                 * filter,signalは必ず正当である.
                 */
                ExecutionInner(final double[] signal) {
                    this.signal = signal;
                }

                /**
                 * 畳み込みを計算する.
                 */
                double[] compute(boolean parallel) {

                    // タプルの作成: [start, subListEfficientLength]
                    //   subListEfficientLength: サブリストの正味の長さ　(最終結果に残す長さ), 
                    //   普通はsubListLengthと同等だが, 信号の末尾まで行く場合は短くなる
                    List<int[]> tupleOfStartAndSubListEfficientLength =
                            new ArrayList<>(signal.length / subListLength);
                    for (int start = 0; start < signal.length; start += subListLength) {
                        int subListEfficientLength = Math.min(signal.length - start, subListLength);
                        tupleOfStartAndSubListEfficientLength.add(new int[] { start, subListEfficientLength });
                    }

                    /*
                     * 以下の処理を並列化に処理する.
                     * 
                     * 1.
                     * 区間: [start, start + subListEfficientLength)
                     * の部分のフィルタ畳み込み結果を計算する.
                     * 2.
                     * flatMapとtoArrayにより, 部分結果を結合する
                     */
                    Stream<int[]> stream = tupleOfStartAndSubListEfficientLength.stream();
                    if (parallel) {
                        stream = stream.parallel();
                    }

                    // 畳み込みを実行し, 結果用配列とする
                    double[] out = stream
                            .map(tuple -> {
                                int start = tuple[0];
                                int subListEfficientLength = tuple[1];
                                return computeSubListConvolution(start, subListEfficientLength);
                            })
                            .flatMapToDouble(d -> Arrays.stream(d))
                            .toArray();

                    // 負の値を修正
                    double negativeAbsMax = Arrays.stream(out)
                            .filter(v -> v < 0d)
                            .map(Math::abs)
                            .max().orElse(0d);
                    for (int i = 0, len = out.length; i < len; i++) {
                        double v = out[i];
                        out[i] = v >= negativeAbsMax ? v : 0d;
                    }

                    return out;
                }

                /**
                 * 区間: [start, start + subListEfficientLength)
                 * の部分のフィルタ畳み込み結果を計算する.
                 */
                private double[] computeSubListConvolution(
                        int start, int subListEfficientLength) {

                    // 長さがconvolutionSizeのシグナルを得る
                    double[] partialConvolutionSignal = cutSignal(
                            start - extendSize, start + subListLength + extendSize);
                    double[] partialOut = partialAppliedConv.apply(partialConvolutionSignal);
                    // 必要部分の切り出し
                    double[] cutPartialOut =
                            Arrays.copyOfRange(partialOut, extendSize, extendSize + subListEfficientLength);

                    return cutPartialOut;
                }

                /**
                 * シグナルから [fromInclusive, toExclusive) を切り出した配列を得る. <br>
                 * シグナルの範囲外は0埋めされる.
                 */
                private double[] cutSignal(int fromInclusive, int toExclusive) {

                    double[] out = new double[toExclusive - fromInclusive];
                    int startInclusive = Math.max(fromInclusive, 0);
                    int endExclusive = Math.min(signal.length, toExclusive);
                    System.arraycopy(
                            signal, startInclusive, out, startInclusive - fromInclusive, endExclusive - startInclusive);

                    return out;
                }
            }
        }
    }
}
