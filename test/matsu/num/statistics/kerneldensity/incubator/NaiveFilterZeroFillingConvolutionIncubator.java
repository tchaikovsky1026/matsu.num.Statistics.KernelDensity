/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.11.27
 */
package matsu.num.statistics.kerneldensity.incubator;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

/**
 * 素朴な実装による, フィルタ畳み込み (並列化のための Incubator).
 * 
 * <p>
 * パフォーマンス計測の結果, 
 * フィルタサイズ: 20以上, 
 * (フィルタサイズ * signalサイズ): 50_000以上
 * で並列化に効果があるようである.
 * </p>
 * 
 * @author Matsuura Y.
 */
final class NaiveFilterZeroFillingConvolutionIncubator {

    /**
     * 並列処理を実行するかどうかのフラグ.
     */
    private final boolean parallel;

    /**
     * デフォルトの設定でのコンストラクタ.
     */
    NaiveFilterZeroFillingConvolutionIncubator() {
        this(false);
    }

    /**
     * 標準のコンストラクタ. <br>
     * 並列実行するかどうかのフラグを与える.
     * 
     * @param parallel 並列実行するかのフラグ
     */
    NaiveFilterZeroFillingConvolutionIncubator(boolean parallel) {
        super();
        this.parallel = parallel;
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
         * 以下について, 並列化を試みる.
         * j の範囲を複数の分割し, double[] を複数得て, 最後に総和を計算する.
         * 総和の計算効率を考えると, spliteratorからストリームを生成し, parallelに処理するのが良い.
         */
        return new ConvolutionExecution(filter, signal).compute();
    }

    /**
     * フィルタ畳み込みの実体.
     */
    private final class ConvolutionExecution {

        private final double[] filter;
        private final double[] signal;

        ConvolutionExecution(double[] filter, double[] signal) {
            super();
            this.filter = filter;
            this.signal = signal;
        }

        double[] compute() {
            // 以下のストリームにより並列操作を実現する
            return StreamSupport.stream(new IntRangeSpliterator(0, signal.length), parallel)
                    .map(this::computeEach)
                    .collect(new DoubleArraySumCollector());
        }

        /**
         * シグナルの, 区間: [start, end) の範囲だけ非ゼロとみなしたときの,
         * フィルタ畳み込みを計算する. <br>
         * start: inclusive, end: exclusive <br>
         * 畳み込みの結果は LocalSparce配列として表現する.
         * 
         * @param range 区間, range[0] = start, range[1] = end
         * @return 畳み込み結果
         */
        private LocalSparceDoubleArray computeEach(int[] range) {
            int start = range[0];
            int end = range[1];

            /*
             * 素朴にフィルタによる畳み込みを実行する.
             * 
             * signalの [start,end)の範囲が非零として畳み込みをした場合,
             * 前方向に (filter.length - 1) だけ拡張されるため,
             * 結果の非零は max(0, start - filter.length + 1) が
             * 開始位置 (inclusive) となる.
             * また, 後方向に (filter.length - 1) だけ拡張されるため,
             * 結果の非零は min(signal.length, end + filter.length - 1) が
             * 終了位置 (exclusive) となる.
             */
            int offset = Math.max(0, start - filter.length + 1);
            int size = Math.min(signal.length, end + filter.length - 1) - offset;
            double[] out = new double[size];
            for (int j = start; j < end; j++) {
                double v = signal[j];

                out[j - offset] += v * filter[0];
                for (int i = 1, len = Math.min(filter.length, signal.length - j); i < len; i++) {
                    out[j + i - offset] += v * filter[i];
                }
                for (int i = 1, len = Math.min(filter.length, j + 1); i < len; i++) {
                    out[j - i - offset] += v * filter[i];
                }
            }

            return new LocalSparceDoubleArray(offset, out);
        }
    }

    /**
     * LocalSparceDoubleArray のストリームの総和を計算する.
     * ストリーム内の要素が, うまく重なるように順番になっていると効率が良くなる.
     */
    private static final class DoubleArraySumCollector
            implements Collector<LocalSparceDoubleArray, LocalSparceDoubleArray[], double[]> {

        DoubleArraySumCollector() {
            super();
        }

        @Override
        public Supplier<LocalSparceDoubleArray[]> supplier() {
            return () -> new LocalSparceDoubleArray[] { LocalSparceDoubleArray.IDENTITY };
        }

        @Override
        public BiConsumer<LocalSparceDoubleArray[], LocalSparceDoubleArray> accumulator() {
            return (con, v) -> {
                con[0] = con[0].addedTo(v);
            };
        }

        @Override
        public BinaryOperator<LocalSparceDoubleArray[]> combiner() {
            return (con1, con2) -> {
                con1[0] = con1[0].addedTo(con2[0]);
                return con1;
            };
        }

        @Override
        public Function<LocalSparceDoubleArray[], double[]> finisher() {
            return con -> con[0].toArray();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.noneOf(Characteristics.class);
        }
    }

    /**
     * 局所的に非ゼロ値を持つ {@code double} 配列を表現する. <br>
     * イミュータブルである.
     */
    private static final class LocalSparceDoubleArray {

        /**
         * 単位元 (すなわち不定サイズかつ0埋めされたもの) を表すシングルトン.
         */
        static final LocalSparceDoubleArray IDENTITY = new LocalSparceDoubleArray();

        private final int start;
        private final double[] entry;

        /**
         * このインスタンスが単位元であるかどうかを表す.
         */
        private final boolean identity;

        /**
         * 意味のある配列を表現する公開コンストラクタ.
         * 
         * @param start 非ゼロの開始位置 0以上
         * @param entry 空でない
         */
        LocalSparceDoubleArray(int start, double[] entry) {
            super();

            assert start >= 0;
            assert entry.length >= 1;

            this.identity = false;
            this.start = start;
            this.entry = entry;
        }

        /**
         * 内部から呼ばれる.
         */
        private LocalSparceDoubleArray() {
            super();

            this.identity = true;
            this.start = 0;
            this.entry = new double[0];
        }

        /**
         * 配列の各成分の和を計算して返す.
         */
        LocalSparceDoubleArray addedTo(LocalSparceDoubleArray other) {
            if (this.identity) {
                return other;
            }
            if (other.identity) {
                return this;
            }

            var con1 = this;
            var con2 = other;
            if (con1.start > con2.start) {
                var conTemp = con1;
                con1 = con2;
                con2 = conTemp;
            }

            int offset = con2.start - con1.start;
            double[] con1Entry = con1.entry;
            double[] con2Entry = con2.entry;

            double[] newEntry = Arrays.copyOf(
                    con1Entry,
                    Math.max(con1Entry.length, con2Entry.length + offset));
            for (int i = 0, len = con2Entry.length; i < len; i++) {
                newEntry[i + offset] += con2Entry[i];
            }

            return new LocalSparceDoubleArray(con1.start, newEntry);
        }

        /**
         * このインスタンスの配列表現を返す. <br>
         * このインスタンスが IDENTITY の場合, nullを返す.
         */
        double[] toArray() {
            if (identity) {
                return null;
            }

            double[] out = new double[start + entry.length];
            System.arraycopy(entry, 0, out, start, entry.length);
            return out;
        }

    }

    /**
     * int の区間を表す Spliterator. <br>
     * 要素数は常に1である.
     */
    private static final class IntRangeSpliterator implements Spliterator<int[]> {

        private static final int MIN_SPLITTABLE_SIZE = 128;

        /*
         * 区間: [start, end) を表す. <br>
         * start: inclusive, end: exclusive
         */
        private int start;
        private int end;

        /**
         * tryAdvanceが呼ばれたか (rangeが使用されたか)を表す.
         */
        private boolean consumed = false;

        IntRangeSpliterator(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean tryAdvance(Consumer<? super int[]> action) {
            if (!consumed) {
                consumed = true;
                action.accept(new int[] { start, end });
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<int[]> trySplit() {
            // 処理を終えている場合はスプリット不可
            if (consumed) {
                return null;
            }

            int start = this.start;
            int end = this.end;

            if (end - start < MIN_SPLITTABLE_SIZE) {
                return null;
            }

            int mid = start + ((end - start) >>> 1);
            Spliterator<int[]> out = new IntRangeSpliterator(start, mid);
            this.start = mid;
            return out;
        }

        @Override
        public long estimateSize() {
            return consumed ? 0 : end - start;
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED |
                    Spliterator.NONNULL;
        }
    }
}
