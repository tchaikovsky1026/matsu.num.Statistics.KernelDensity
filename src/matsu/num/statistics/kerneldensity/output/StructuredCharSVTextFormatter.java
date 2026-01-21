/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2026.1.21
 */
package matsu.num.statistics.kerneldensity.output;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import matsu.num.statistics.kerneldensity.KdeGrid2dDto;

/**
 * 区切り文字で区切られた, 構造化された文字列出力を行う {@link Kde2dFormatter}.
 * 
 * <p>
 * 文字列化した結果は,
 * 各行を {@link String} とした入れ子の {@link Iterable} で表す.
 * </p>
 * 
 * <p>
 * 文字列フォーマットは, 外側 iterator が {@code x} に対する iteration であり, <br>
 * {@code x_index = j} についての内側 iterator は <br>
 * {@code x[j]<sep>y[k]<sep>density[j][k]} <br>
 * についての {@code k} に関する iteration である
 * ({@code <sep>} は区切り文字). <br>
 * データの要素数は必ず 1 以上である ({@code density[0][0]} が必ず存在する).
 * </p>
 * 
 * <p>
 * 内側の {@link Iterable} からは,
 * {@link Iterator} を1度しか生成できない. <br>
 * 2度目の {@link Iterable#iterator()} の実行時に
 * {@link IllegalStateException} がスローされる.
 * </p>
 * 
 * @apiNote
 *              結果を出力する目的で実行されるため,
 *              外側の iteration の実行において "全データを1回だけ出力する" ことが求められる. <br>
 *              これを実現するため, 内部の {@link Iterable} は1度しか実行できないようにした. <br>
 *              {@literal Iterable<Iterator<String>>}
 *              とすれば型としてこの仕様を実現できるが,
 *              拡張 {@code for} 文での使用を容易にするため, 内側を {@link Iterable} とした.
 * 
 *              <p>
 *              以下に, フォーマッターの実行例を示す.
 *              </p>
 * 
 *              <pre>
 * {@literal //} 元データ
 * int sizeX = 2;
 * int sizeY = 3;
 * double[] x = { 1.0, 2.0 };
 * double[] y = { 1.5, 2.5, 3.5 };
 * double[][] density = {
 *     { 0.125, 0.25, 0.0 },
 *     { 0.25, 0.125, 0.25 }
 * };
 * 
 * {@literal //} 出力 {@literal (Iterable<Iterable<String>>} を2次元配列で表記)
 * out = {
 *     {
 *         "1.0{@literal <sep>}1.5{@literal <sep>}0.125",
 *         "1.0{@literal <sep>}2.5{@literal <sep>}0.25",
 *         "1.0{@literal <sep>}3.5{@literal <sep>}0.0"
 *     }, {
 *         "2.0{@literal <sep>}1.5{@literal <sep>}0.25",
 *         "2.0{@literal <sep>}2.5{@literal <sep>}0.125",
 *         "2.0{@literal <sep>}3.5{@literal <sep>}0.25"
 *     }
 * };
 * </pre>
 * 
 * @author Matsuura Y.
 */
public final class StructuredCharSVTextFormatter extends Kde2dFormatter<Iterable<Iterable<String>>> {

    private final char separator;

    /**
     * 区切り文字を与えて, Character Separated Values 文字列出力フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * <p>
     * 区切り文字に制限はないが,
     * ほとんどの場合, 改行 {@code \n} は不適切である.
     * </p>
     * 
     * @param separator 区切り文字
     * @return Character Separated Values フォーマッター
     */
    public static StructuredCharSVTextFormatter of(char separator) {
        return new StructuredCharSVTextFormatter(separator);
    }

    /**
     * 唯一のコンストラクタ.
     * 
     * @param separator 区切り文字
     */
    private StructuredCharSVTextFormatter(char separator) {
        super();
        this.separator = separator;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    Iterable<Iterable<String>> format(KdeGrid2dDto dto) {
        return new OuterIterable(Objects.requireNonNull(dto));
    }

    private final class OuterIterable implements Iterable<Iterable<String>> {

        private final KdeGrid2dDto dto;

        /**
         * 非公開のコンストラクタ.
         */
        OuterIterable(KdeGrid2dDto dto) {
            super();
            this.dto = dto;
        }

        @Override
        public Iterator<Iterable<String>> iterator() {
            return new OuterIterator();
        }

        /**
         * 外側のイテレータ. <br>
         * x-indexをイテレートする.
         */
        private final class OuterIterator implements Iterator<Iterable<String>> {

            //　-1のときは, y値を並べる位置とする
            private final AtomicInteger cursorX;

            OuterIterator() {
                super();
                cursorX = new AtomicInteger(0);
            }

            @Override
            public boolean hasNext() {
                return hasNextHelper(cursorX.get());
            }

            @Override
            public Iterable<String> next() {
                int cursorX = this.cursorX.getAndIncrement();

                if (!hasNextHelper(cursorX)) {
                    throw new NoSuchElementException();
                }

                return new InnerIterable(cursorX);
            }

            /**
             * 与えたカーソルの値が, データ内かどうかを確かめる.
             */
            private boolean hasNextHelper(int cursorX) {
                return cursorX < dto.sizeX;
            }

            /**
             * remove 不可.
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        }

        private final class InnerIterable implements Iterable<String> {

            private final int cursorX;
            private final AtomicBoolean used;

            InnerIterable(int cursorX) {
                super();
                this.cursorX = cursorX;
                this.used = new AtomicBoolean(false);
            }

            @Override
            public Iterator<String> iterator() {
                // すでにイテレータが生成されていた場合は例外がスローされる
                boolean used = this.used.getAndSet(true);
                if (used) {
                    throw new IllegalStateException("This Iterable already has been iterated.");
                }

                return new InnerIterator();
            }

            private final class InnerIterator implements Iterator<String> {

                //　-1のときは, y値を並べる位置とする
                private final AtomicInteger cursorY;

                InnerIterator() {
                    super();
                    cursorY = new AtomicInteger(0);
                }

                @Override
                public boolean hasNext() {
                    return hasNextHelper(cursorY.get());
                }

                @Override
                public String next() {
                    int cursorY = this.cursorY.getAndIncrement();

                    if (!hasNextHelper(cursorY)) {
                        throw new NoSuchElementException();
                    }

                    return dto.x[cursorX] + Character.toString(separator) +
                            dto.y[cursorY] + Character.toString(separator) +
                            dto.density[cursorX][cursorY];
                }

                /**
                 * 与えたカーソルの値が, データ内かどうかを確かめる.
                 */
                private boolean hasNextHelper(int cursorY) {
                    return cursorY < dto.sizeY;
                }

                /**
                 * remove 不可.
                 */
                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove");
                }
            }
        }
    }
}
