/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/**
 * カーネル密度推定の実行を行うパッケージ.
 * 
 * <p>
 * カーネル密度推定の計算には, データソースとして <br>
 * <i>x</i><sub>1</sub>, <i>x</i><sub>2</sub>, ... (1次元) <br>
 * (<i>x</i><sub>1</sub>, <i>y</i><sub>1</sub>),
 * (<i>x</i><sub>2</sub>, <i>y</i><sub>2</sub>), ... (2次元) <br>
 * のような値が必要である. <br>
 * また, 結果を出力するために, 出力範囲が要求される.
 * </p>
 * 
 * <p>
 * このモジュールでの機能は, データソースを与えて推定器をインスタンス化し,
 * その後に結果出力範囲を与えて推定結果を得る, という構成である. <br>
 * 推定器は
 * {@link matsu.num.statistics.kerneldensity.KernelDensity1D KernelDensity1D},
 * {@link matsu.num.statistics.kerneldensity.KernelDensity1D KernelDensity2D}
 * やその具象クラスにより表現され,
 * 推定結果は
 * {@link matsu.num.statistics.kerneldensity.KdeGrid1dDto KdeGrid1dDto},
 * {@link matsu.num.statistics.kerneldensity.KdeGrid2dDto KdeGrid2dDto}
 * のような転送用クラスで表現される.
 * </p>
 */
package matsu.num.statistics.kerneldensity;
