/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/**
 * カーネル密度推定を扱うモジュール.
 * 
 * <p>
 * このモジュールの主要なパッケージは次である. <br>
 * {@link matsu.num.statistics.kerneldensity}
 * はカーネル密度推定 (1次元, 2次元) を実行するためのインターフェース, 実装が用意されている. <br>
 * {@link matsu.num.statistics.kerneldensity.output}
 * はカーネル密度推定の結果の出力をサポートする.
 * </p>
 * 
 * <p>
 * 詳しくは, 各パッケージの説明文を参照すること.
 * </p>
 * 
 * <p>
 * <i>依存モジュール:</i> <br>
 * (無し)
 * </p>
 * 
 * @author Matsuura Y.
 * @version 1.1.0
 */
module matsu.num.Statistics.KernelDensity {

    exports matsu.num.statistics.kerneldensity;
    exports matsu.num.statistics.kerneldensity.output;
}
