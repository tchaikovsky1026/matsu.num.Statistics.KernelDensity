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
 * 区切り文字で区切られた {@link Kde2dTextFormatter}.
 * 
 * <p>
 * 文字列フォーマットは, ラベル有りの場合は先頭行にラベルであり,
 * 以降, <br>
 * {@code x[j]<sep>y[k]<sep>density[j][k]} <br>
 * が続く
 * ({@code <sep>} は区切り文字). <br>
 * イテレーション順は, {@code j} が外側, {@code k} が内側である.
 * </p>
 * 
 * <p>
 * ラベル要素は {@code "x"}, {@code "y"}, {@code "density"} である.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class Kde2dCharSVFormatter extends Kde2dTextFormatter {

    private final char separator;
    private final boolean withLabel;

    /**
     * ラベル無しの Character Separated Values フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * @param separator 区切り文字
     * @return Character Separated Values フォーマッター
     */
    public static Kde2dTextFormatter labelless(char separator) {
        return new Kde2dCharSVFormatter(separator, false);
    }

    /**
     * ラベル有りの Character Separated Values フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * @param separator 区切り文字
     * @return Character Separated Values フォーマッター
     */
    public static Kde2dTextFormatter withLabel(char separator) {
        return new Kde2dCharSVFormatter(separator, true);
    }

    /**
     * 唯一のコンストラクタ.
     * 
     * @param separator 区切り文字
     */
    private Kde2dCharSVFormatter(char separator, boolean withLabel) {
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
