/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.27
 */
package matsu.num.statistics.kerneldensity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 効率的な実装による, フィルタ畳み込み.
 * 
 * @author Matsuura Y.
 */
final class EffectiveFilterZeroFillingConvolution {

    /**
     * 高効率な畳み込みを実行する場合の, フィルタの最低サイズの目安.
     */
    static final int MIN_FILTER_SIZE_FOR_EFFECTIVE = 20;

    /**
     * 高効率な畳み込みを実行する場合の, (filter * signal)の最低サイズの目安.
     */
    static final long MIN_FILTER_TIMES_SIGNAL_SIZE_FOR_EFFECTIVE = 50_000L;

    /**
     * 高効率な巡回畳み込み.
     */
    private final EffectiveCyclicConvolution cyclicConvolution;

    /**
     * 唯一のコンストラクタ.
     *
     * @param cyclicConvolution 巡回畳み込み
     * @throws NullPointerException 引数が null の場合
     */
    EffectiveFilterZeroFillingConvolution(EffectiveCyclicConvolution cyclicConvolution) {
        this.cyclicConvolution = Objects.requireNonNull(cyclicConvolution);
    }

    /**
     * 与えたシグナルに対して, フィルタによる畳み込みを適用する. <br>
     * 畳み込みは外部に0埋めして行う.
     * 
     * <p>
     * フィルタは片側の値を配列でを与える. <br>
     * 与えた配列に対して, <br>
     * {@code filter[length - 1], ... , filter[1], filter[0] (center), filter[1], ... , filter[length - 1]}
     * <br>
     * となる.
     * </p>
     * 
     * <p>
     * {@code filter.length} は 1 以上でなければならない. <br>
     * シグナルサイズは1以上でなければならない.
     * </p>
     * 
     * @param filter フィルタ
     * @param signal シグナル
     * @return 畳み込みの結果
     * @throws IllegalArgumentException 引数が不適の場合
     * @throws NullPointerException 引数がnullの場合
     */
    double[] compute(final double[] filter, final double[] signal) {
        if (filter.length == 0) {
            throw new IllegalArgumentException("filter is empty");
        }
        if (signal.length == 0) {
            throw new IllegalArgumentException("signal is empty");
        }

        return new ConvolutionExecution(filter, signal).compute();
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
         * フィルタが有限であるため, subListの前後に(filter.length - 1) だけ拡張した区間の信号を考えれば十分である.
         * 巡回畳み込みで生じるエイリアシングはこの拡張部分にのみ生じるので, そこをカットすればよい.
         * よって,
         * subListLength = convolutionSize - extendSize * 2
         * が, 値を得られるサブリストの長さとなる (extendSize = filter.length - 1).
         * 信号をsubListLengthで分割し, 両側に拡張した範囲(convolutionSize)でsignalから取り出す.
         * フィルタとの畳み込みを行い, 中央のsubListLength分を取り出す.
         */

        private final double[] signal;

        private final int convolutionSize;
        private final int extendSize;
        private final int subListLength;

        private final Function<double[], double[]> partialAppliedConv;

        /**
         * 唯一のコンストラクタ. <br>
         * filter,signalは必ず正当である.
         */
        ConvolutionExecution(final double[] filter, final double[] signal) {
            this.signal = signal;

            convolutionSize = cyclicConvolution.calcAcceptableSize(filter.length * 6);
            extendSize = filter.length - 1;
            subListLength = convolutionSize - extendSize * 2;

            partialAppliedConv =
                    cyclicConvolution.applyPartial(toConvolutionFilter(filter, convolutionSize));
        }

        /**
         * 畳み込みを計算する.
         */
        double[] compute() {

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
            return tupleOfStartAndSubListEfficientLength.parallelStream()
                    .map(tuple -> {
                        int start = tuple[0];
                        int subListEfficientLength = tuple[1];
                        return computeSubListConvolution(start, subListEfficientLength);
                    })
                    .flatMapToDouble(d -> Arrays.stream(d))
                    .toArray();
        }

        /**
         * 区間: [start, start + subListEfficientLength)
         * の部分のフィルタ畳み込み結果を計算する.
         */
        private double[] computeSubListConvolution(int start, int subListEfficientLength) {

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
    }
}
