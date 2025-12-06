/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.6
 */
package matsu.num.statistics.kerneldensity.output;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import matsu.num.statistics.kerneldensity.KdeGrid2dDto;

/**
 * 区切り文字で区切られた, Matrix 表示の出力を行う {@link Kde2dFormatter}.
 * 
 * <p>
 * 文字列化した結果は,
 * 各行を {@link String} とした {@link Iterable} で表す.
 * </p>
 * 
 * <p>
 * 文字列フォーマットは,
 * 最初の行が <br>
 * {@code <sep>y[0]<sep>y[1]...} <br>
 * であり, 以降, <br>
 * {@code x[j]<sep>density[j][0]<sep>density[j][1]...} <br>
 * が続く
 * ({@code <sep>} は区切り文字). <br>
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
 *     "{@literal <sep>}1.5{@literal <sep>}2.5{@literal <sep>}3.5",    {@literal //} y値の列 
 *     "1.0{@literal <sep>}0.125{@literal <sep>}0.25{@literal <sep>}0.0",    {@literal //} x値とdensity
 *     "2.0{@literal <sep>}0.25{@literal <sep>}0.125{@literal <sep>}0.25"
 * };
 * </pre>
 * 
 * @author Matsuura Y.
 */
public final class MatrixCharSVTextFormatter extends Kde2dFormatter<Iterable<String>> {

    private final char separator;

    /**
     * 区切り文字を与えて, Character Separated Values 文字列出力フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * @param separator 区切り文字
     * @return Character Separated Values フォーマッター
     */
    public static MatrixCharSVTextFormatter of(char separator) {
        return new MatrixCharSVTextFormatter(separator);
    }

    /**
     * 唯一のコンストラクタ.
     * 
     * @param separator 区切り文字
     */
    private MatrixCharSVTextFormatter(char separator) {
        super();
        this.separator = separator;
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

            //　-1のときは, y値を並べる位置とする
            private final AtomicInteger cursor;

            TextOutputIterator() {
                cursor = new AtomicInteger(-1);
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
                    // y値を並べた文字列の出力
                    double[] y = dto.y;
                    StringBuilder sb = new StringBuilder();
                    for (double y_k : y) {
                        sb.append(separator);
                        sb.append(y_k);
                    }
                    return sb.toString();
                }

                // x<sep>density... を表す文字列の出力
                StringBuilder sb = new StringBuilder()
                        .append(dto.x[cursor]);
                for (double d : dto.density[cursor]) {
                    sb.append(separator);
                    sb.append(d);
                }
                return sb.toString();
            }

            /**
             * 与えたカーソルの値が, データ内かどうかを確かめる.
             */
            private boolean hasNextHelper(int cursor) {
                return cursor < dto.sizeX;
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
