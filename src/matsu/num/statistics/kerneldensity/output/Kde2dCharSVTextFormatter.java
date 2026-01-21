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
 * データの要素数は必ず 1 以上である ({@code density[0][0]} が必ず存在する). <br>
 * ラベルエスケープ文字 {@code <escape>} が指定された場合, ラベル行の最初にエスケープ文字が追加される.
 * </p>
 * 
 * @apiNote
 *              以下に, フォーマッターの実行例を示す.
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
 * {@literal //} 出力 (Iterable{@literal <String>} を配列で表記)
 * out = {
 *     "{@literal <escape>}x{@literal <sep>}y{@literal <sep>}density",    {@literal //} ラベル有りの場合, {@literal <escape>} はラベルエスケープを行う場合
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
    private final Character labelEscape;

    /**
     * ラベル無しの Character Separated Values 文字列出力フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * <p>
     * 区切り文字に制限はないが,
     * ほとんどの場合, null文字 {@code \u005cu0000} や改行 {@code \n} は不適切である.
     * </p>
     * 
     * @param separator 区切り文字
     * @return Character Separated Values フォーマッター
     */
    public static Kde2dCharSVTextFormatter labelless(char separator) {
        return new Kde2dCharSVTextFormatter(separator);
    }

    /**
     * ラベル有りの Character Separated Values 文字列出力フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * <p>
     * 区切り文字に制限はないが,
     * ほとんどの場合, null文字 {@code \u005cu0000} や改行 {@code \n} は不適切である.
     * </p>
     * 
     * @param separator 区切り文字
     * @return Character Separated Values フォーマッター
     */
    public static Kde2dCharSVTextFormatter withLabel(char separator) {
        return new Kde2dCharSVTextFormatter(separator, null);
    }

    /**
     * エスケープ文字付きラベルを有する, Character Separated Values フォーマッターを生成する.
     * 
     * <p>
     * 文字列形式はクラス説明の通りである.
     * </p>
     * 
     * <p>
     * 区切り文字, エスケープ文字に制限はないが,
     * ほとんどの場合, null文字 {@code \u005cu0000} や改行 {@code \n} は不適切である.
     * </p>
     * 
     * @param separator 区切り文字
     * @param labelEscape ラベル文字列の先頭に付けるエスケープ文字
     * @return Character Separated Values フォーマッター
     */
    public static Kde2dCharSVTextFormatter withLabelEscaped(char separator, char labelEscape) {
        return new Kde2dCharSVTextFormatter(separator, Character.valueOf(labelEscape));
    }

    /**
     * ラベル無しのコンストラクタ.
     * 
     * @param separator 区切り文字
     */
    private Kde2dCharSVTextFormatter(char separator) {
        super();
        this.separator = separator;
        this.withLabel = false;
        this.labelEscape = null;
    }

    /**
     * ラベルありのコンストラクタ.
     * 
     * <p>
     * エスケープしない場合, nullを渡す.
     * </p>
     * 
     * @param separator 区切り文字
     * @param labelEscape エスケープ文字, nullを許容
     */
    private Kde2dCharSVTextFormatter(char separator, Character labelEscape) {
        super();
        this.separator = separator;
        this.withLabel = true;
        this.labelEscape = labelEscape;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    Iterable<String> format(KdeGrid2dDto dto) {
        return new TextOutputIterable(Objects.requireNonNull(dto));
    }

    /**
     * ラベル行の文字列を生成する非公開メソッド:
     * 
     * @return "{@literal <escape>}x{@literal <sep>}y{@literal <sep>}density"
     */
    private String labelString() {
        String escape = Objects.isNull(labelEscape)
                ? ""
                : String.valueOf(labelEscape.charValue());

        return escape +
                "x" + Character.toString(separator) +
                "y" + Character.toString(separator) +
                "density";
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
                    return labelString();
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
