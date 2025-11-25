/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.24
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
    static final int MIN_FILTER_SIZE_FOR_EFFECTIVE = 10;

    /**
     * 高効率な畳み込みを実行する場合の, シグナルの最低サイズの目安.
     */
    static final int MIN_SIGNAL_SIZE_FOR_EFFECTIVE = 50;

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
    double[] compute(double[] filter, double[] signal) {
        if (filter.length == 0) {
            throw new IllegalArgumentException("filter is empty");
        }
        if (signal.length == 0) {
            throw new IllegalArgumentException("signal is empty");
        }

        /*
         * 以下で, フィルタ畳み込みを巡回畳み込みを用いて実現する.
         * 
         * 巡回畳み込みに還元するには, フィルタサイズの3倍以上の信号に分割しなければならない.
         * ここでは, フィルタサイズの6倍以上の長さを畳み込みサイズ(convolutionSize)とする.
         * (オーバーラップは半分程度になる).
         * 
         * subListLength = convolutionSize - extendSize * 2
         * が, 値を得られるサブリストの長さとなる (extendSize = filter.length - 1).
         * 信号をsubListLengthで分割し, 両側に拡張した範囲(convolutionSize)でsignalから取り出す.
         * フィルタとの畳み込みを行い, 中央のsubListLength分を取り出す.
         */

        final int convolutionSize = cyclicConvolution.calcAcceptableSize(filter.length * 6);
        final int extendSize = filter.length - 1;
        final int subListLength = convolutionSize - extendSize * 2;

        Function<double[], double[]> partialAppliedConv =
                cyclicConvolution.applyPartial(toConvolutionFilter(filter, convolutionSize));

        List<double[]> outList = new ArrayList<>();
        for (int start = 0; start < signal.length; start += subListLength) {
            // サブリストの正味の長さ, 普通はsubListLengthと同等だが, 信号の末尾まで行く場合は短くなる
            int subListEfficientLength = Math.min(signal.length - start, subListLength);

            double[] subList = toSubList(
                    signal,
                    start - extendSize, start + subListLength + extendSize);
            double[] partialOut = partialAppliedConv.apply(subList);
            double[] cutPartialOut =
                    Arrays.copyOfRange(partialOut, extendSize, extendSize + subListEfficientLength);

            outList.add(cutPartialOut);
        }

        return outList.stream()
                .flatMapToDouble(d -> Arrays.stream(d))
                .toArray();
    }

    /**
     * シグナルから [fromInclusive, toExclusive) を切り出したサブリストを得る. <br>
     * シグナルの範囲外は0埋めされる.
     */
    private static double[] toSubList(
            double[] signal, int fromInclusive, int toExclusive) {

        double[] out = new double[toExclusive - fromInclusive];
        int startInclusive = Math.max(fromInclusive, 0);
        int endExclusive = Math.min(signal.length, toExclusive);
        System.arraycopy(signal, startInclusive, out, startInclusive - fromInclusive, endExclusive - startInclusive);

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
