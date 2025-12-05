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

import matsu.num.statistics.kerneldensity.KdeGrid1dDto;

/**
 * 区切り文字で区切られた {@link Kde1dFormatter}. <br>
 * 
 * <p>
 * 文字列化した結果は,
 * 各行を {@link String} とした {@link Iterable} で表す.
 * </p>
 * 
 * <p>
 * 文字列フォーマットは, ラベル有りの場合は先頭行にラベルであり,
 * 以降, <br>
 * {@code x[i]<sep>density[i]} <br>
 * が続く
 * ({@code <sep>} は区切り文字).
 * </p>
 * 
 * <p>
 * ラベル要素は {@code "x"}, {@code "density"} である.
 * </p>
 * 
 * @author Matsuura Y.
 */
public final class Kde1dCharSVTextFormatter extends Kde1dFormatter<Iterable<String>> {

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
    public static Kde1dCharSVTextFormatter labelless(char separator) {
        return new Kde1dCharSVTextFormatter(separator, false);
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
    public static Kde1dCharSVTextFormatter withLabel(char separator) {
        return new Kde1dCharSVTextFormatter(separator, true);
    }

    /**
     * 唯一のコンストラクタ.
     * 
     * @param separator 区切り文字
     */
    private Kde1dCharSVTextFormatter(char separator, boolean withLabel) {
        super();
        this.separator = separator;
        this.withLabel = withLabel;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    Iterable<String> format(KdeGrid1dDto dto) {
        return new TextOutputIterable(Objects.requireNonNull(dto));
    }

    /**
     * Iterable の戻り値.
     */
    private final class TextOutputIterable implements Iterable<String> {

        private final KdeGrid1dDto dto;

        /**
         * 非公開のコンストラクタ.
         */
        TextOutputIterable(KdeGrid1dDto dto) {
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
                    return "x" + Character.toString(separator) + "density";
                }
                return dto.x[cursor] + Character.toString(separator) + dto.density[cursor];
            }

            /**
             * 与えたカーソルの値が, データ内かどうかを確かめる.
             */
            private boolean hasNextHelper(int cursor) {
                return cursor < dto.size;
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
