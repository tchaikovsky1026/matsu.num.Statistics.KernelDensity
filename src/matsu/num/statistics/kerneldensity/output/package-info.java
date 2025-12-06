/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/**
 * カーネル密度推定の結果出力をサポートするパッケージ.
 * 
 * <p>
 * カーネル密度推定の結果を, 例えば文字列で出力する機能を有する. <br>
 * 推定結果を格納する
 * {@link matsu.num.statistics.kerneldensity.output.FormattableKdeResult1D
 * FormattableKdeResult1D},
 * {@link matsu.num.statistics.kerneldensity.output.FormattableKdeResult2D
 * FormattableKdeResult2D}
 * などに,
 * {@link matsu.num.statistics.kerneldensity.output.Kde1dFormatter
 * Kde1dFormatter},
 * {@link matsu.num.statistics.kerneldensity.output.Kde2dFormatter
 * Kde2dFormatter}
 * のようなフォーマッターを渡して出力結果を得る.
 * </p>
 */
package matsu.num.statistics.kerneldensity.output;
