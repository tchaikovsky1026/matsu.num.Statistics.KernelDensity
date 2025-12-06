/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.5
 */
package matsu.num.statistics.kerneldensity.output;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import matsu.num.statistics.kerneldensity.KdeGrid2dDto;

/**
 * 区切り文字で区切られた文字列出力を行う {@link Kde2dFormatter}.
 * 
 * <p>
 * 文字列化した結果は,
 * 各行を {@link String} とした {@link Iterable} で表す.
 * </p>
 * 
 * <p>
 * 文字列フォーマットは, ラベル有りの場合は先頭行にラベルであり,
 * 以降, <br>
 * {@code x[j]<sep>y[k]<sep>density[j][k]} <br>
 * が続く
 * ({@code <sep>} は区切り文字). <br>
 * イテレーション順は, {@code j} が外側, {@code k} が内側である. <br>
 * ラベル要素は {@code "x"}, {@code "y"}, {@code "density"} である. <br>
 * 以下に例を示す.
 * </p>
 * 
 * <pre>
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
 * {@literal //} 出力 (Iterable{@literal <String>} を配列で表記)
 * out = {
 *     "x{@literal <sep>}y{@literal <sep>}density",    {@literal //} ラベル有りの場合
 *     "1.0{@literal <sep>}1.5{@literal <sep>}0.125",
 *     "1.0{@literal <sep>}2.5{@literal <sep>}0.25",
 *     "1.0{@literal <sep>}3.5{@literal <sep>}0.0",
 *     "2.0{@literal <sep>}1.5{@literal <sep>}0.25",
 *     "2.0{@literal <sep>}2.5{@literal <sep>}0.125",
 *     "2.0{@literal <sep>}3.5{@literal <sep>}0.25"
 * };
 * </pre>
 * 
 * @author Matsuura Y.
 */
public final class Kde2dCharSVTextFormatter extends Kde2dFormatter<Iterable<String>> {

    private final char separator;
    private final boolean withLabel;

    /**
     * ラベル無しの Character Separated Values 文字列出力フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * @param separator 区切り文字
     * @return Character Separated Values フォーマッター
     */
    public static Kde2dCharSVTextFormatter labelless(char separator) {
        return new Kde2dCharSVTextFormatter(separator, false);
    }

    /**
     * ラベル有りの Character Separated Values 文字列出力フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * @param separator 区切り文字
     * @return Character Separated Values フォーマッター
     */
    public static Kde2dCharSVTextFormatter withLabel(char separator) {
        return new Kde2dCharSVTextFormatter(separator, true);
    }

    /**
     * 唯一のコンストラクタ.
     * 
     * @param separator 区切り文字
     */
    private Kde2dCharSVTextFormatter(char separator, boolean withLabel) {
        super();
        this.separator = separator;
        this.withLabel = withLabel;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    Iterable<String> format(KdeGrid2dDto dto) {
        return new TextOutputIterable(Objects.requireNonNull(dto));
    }

    /**
     * Iterable の戻り値.
     */
    private final class TextOutputIterable implements Iterable<String> {

        private final KdeGrid2dDto dto;

        /**
         * 非公開のコンストラクタ.
         */
        TextOutputIterable(KdeGrid2dDto dto) {
            super();
            this.dto = dto;
        }

        @Override
        public Iterator<String> iterator() {
            return new TextOutputIterator();
        }

        /**
         * Iterator の実装.
         */
        private final class TextOutputIterator implements Iterator<String> {

            //　-1のときはラベル位置とする
            private final AtomicInteger cursor;

            TextOutputIterator() {
                cursor = new AtomicInteger(
                        withLabel ? -1 : 0);
            }

            @Override
            public boolean hasNext() {
                return hasNextHelper(cursor.get());
            }

            @Override
            public String next() {
                int cursor = this.cursor.getAndIncrement();

                if (!hasNextHelper(cursor)) {
                    throw new NoSuchElementException();
                }
                if (cursor == -1) {
                    return "x" + Character.toString(separator) +
                            "y" + Character.toString(separator) +
                            "density";
                }

                int cursorX = cursor / dto.sizeY;
                int cursorY = cursor - dto.sizeY * cursorX;

                return dto.x[cursorX] + Character.toString(separator) +
                        dto.y[cursorY] + Character.toString(separator) +
                        dto.density[cursorX][cursorY];
            }

            /**
             * 与えたカーソルの値が, データ内かどうかを確かめる.
             */
            private boolean hasNextHelper(int cursor) {
                return cursor < dto.sizeX * dto.sizeY;
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
