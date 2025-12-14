/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package matsu.num.statistics.kerneldensity.convol.incubator;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link NaivePower2DftForTesting} のテスト.
 */
@RunWith(Enclosed.class)
final class NaivePower2DftForTestingTest {

    public static class DFT_サイズ4 {

        @Test
        public void test_実部のみデータ() {
            double[] sig_re = { 1, 2, 3, 4 };
            double[] sig_im = { 0, 0, 0, 0 };

            double[] expected_re = { 10, -2, -2, -2 };
            double[] expected_im = { 0, 2, 0, -2 };

            double[][] result = new NaivePower2DftForTesting()
                    .dft(new double[][] { sig_re, sig_im });
            double[] result_re = result[0];
            double[] result_im = result[1];
            // 実部
            for (int i = 0; i < expected_re.length; i++) {
                assertThat(result_re[i], is(closeTo(expected_re[i], 1E-12)));
            }
            // 虚部
            for (int i = 0; i < expected_im.length; i++) {
                assertThat(result_im[i], is(closeTo(expected_im[i], 1E-12)));
            }
        }

        @Test
        public void test_虚部のみデータ() {
            double[] sig_re = { 0, 0, 0, 0 };
            double[] sig_im = { 1, 2, 3, 4 };

            double[] expected_re = { 0, -2, 0, 2 };
            double[] expected_im = { 10, -2, -2, -2 };

            double[][] result = new NaivePower2DftForTesting()
                    .dft(new double[][] { sig_re, sig_im });
            double[] result_re = result[0];
            double[] result_im = result[1];
            // 実部
            for (int i = 0; i < expected_re.length; i++) {
                assertThat(result_re[i], is(closeTo(expected_re[i], 1E-12)));
            }
            // 虚部
            for (int i = 0; i < expected_im.length; i++) {
                assertThat(result_im[i], is(closeTo(expected_im[i], 1E-12)));
            }
        }
    }

    public static class IDFT_サイズ4 {

        @Test
        public void test_実部のみデータ() {
            double[] sig_re = { 1, 2, 3, 4 };
            double[] sig_im = { 0, 0, 0, 0 };

            double[] expected_re = { 10, -2, -2, -2 };
            double[] expected_im = { 0, -2, 0, 2 };

            double[][] result = new NaivePower2DftForTesting()
                    .idft(new double[][] { sig_re, sig_im });
            double[] result_re = result[0];
            double[] result_im = result[1];
            // 実部
            for (int i = 0; i < expected_re.length; i++) {
                assertThat(result_re[i], is(closeTo(expected_re[i], 1E-12)));
            }
            // 虚部
            for (int i = 0; i < expected_im.length; i++) {
                assertThat(result_im[i], is(closeTo(expected_im[i], 1E-12)));
            }
        }

        @Test
        public void test_虚部のみデータ() {
            double[] sig_re = { 0, 0, 0, 0 };
            double[] sig_im = { 1, 2, 3, 4 };

            double[] expected_re = { 0, 2, 0, -2 };
            double[] expected_im = { 10, -2, -2, -2 };

            double[][] result = new NaivePower2DftForTesting()
                    .idft(new double[][] { sig_re, sig_im });
            double[] result_re = result[0];
            double[] result_im = result[1];
            // 実部
            for (int i = 0; i < expected_re.length; i++) {
                assertThat(result_re[i], is(closeTo(expected_re[i], 1E-12)));
            }
            // 虚部
            for (int i = 0; i < expected_im.length; i++) {
                assertThat(result_im[i], is(closeTo(expected_im[i], 1E-12)));
            }
        }
    }

    public static class DFT_IDFT_サイズ4 {

        /*
         * DFT -> IDFT で元に戻る (N倍) になることを確かめる.
         */

        @Test
        public void test_実部のみデータ() {
            double[] sig_re = { 1, 2, 3, 4 };
            double[] sig_im = { 0, 0, 0, 0 };

            double[] expected_re = { 4, 8, 12, 16 };
            double[] expected_im = { 0, 0, 0, 0 };

            var dftModule = new NaivePower2DftForTesting();
            double[][] result = dftModule
                    .idft(dftModule.dft(new double[][] { sig_re, sig_im }));
            double[] result_re = result[0];
            double[] result_im = result[1];
            // 実部
            for (int i = 0; i < expected_re.length; i++) {
                assertThat(result_re[i], is(closeTo(expected_re[i], 1E-12)));
            }
            // 虚部
            for (int i = 0; i < expected_im.length; i++) {
                assertThat(result_im[i], is(closeTo(expected_im[i], 1E-12)));
            }
        }

        @Test
        public void test_虚部のみデータ() {
            double[] sig_re = { 0, 0, 0, 0 };
            double[] sig_im = { 1, 2, 3, 4 };

            double[] expected_re = { 0, 0, 0, 0 };
            double[] expected_im = { 4, 8, 12, 16 };

            var dftModule = new NaivePower2DftForTesting();
            double[][] result = dftModule
                    .idft(dftModule.dft(new double[][] { sig_re, sig_im }));
            double[] result_re = result[0];
            double[] result_im = result[1];
            // 実部
            for (int i = 0; i < expected_re.length; i++) {
                assertThat(result_re[i], is(closeTo(expected_re[i], 1E-12)));
            }
            // 虚部
            for (int i = 0; i < expected_im.length; i++) {
                assertThat(result_im[i], is(closeTo(expected_im[i], 1E-12)));
            }
        }
    }

    public static class IDFT_DFT_サイズ4 {

        /*
         * IDFT -> DFT で元に戻る (N倍) になることを確かめる.
         */

        @Test
        public void test_実部のみデータ() {
            double[] sig_re = { 1, 2, 3, 4 };
            double[] sig_im = { 0, 0, 0, 0 };

            double[] expected_re = { 4, 8, 12, 16 };
            double[] expected_im = { 0, 0, 0, 0 };

            var dftModule = new NaivePower2DftForTesting();
            double[][] result = dftModule
                    .dft(dftModule.idft(new double[][] { sig_re, sig_im }));
            double[] result_re = result[0];
            double[] result_im = result[1];
            // 実部
            for (int i = 0; i < expected_re.length; i++) {
                assertThat(result_re[i], is(closeTo(expected_re[i], 1E-12)));
            }
            // 虚部
            for (int i = 0; i < expected_im.length; i++) {
                assertThat(result_im[i], is(closeTo(expected_im[i], 1E-12)));
            }
        }

        @Test
        public void test_虚部のみデータ() {
            double[] sig_re = { 0, 0, 0, 0 };
            double[] sig_im = { 1, 2, 3, 4 };

            double[] expected_re = { 0, 0, 0, 0 };
            double[] expected_im = { 4, 8, 12, 16 };

            var dftModule = new NaivePower2DftForTesting();
            double[][] result = dftModule
                    .dft(dftModule.idft(new double[][] { sig_re, sig_im }));
            double[] result_re = result[0];
            double[] result_im = result[1];
            // 実部
            for (int i = 0; i < expected_re.length; i++) {
                assertThat(result_re[i], is(closeTo(expected_re[i], 1E-12)));
            }
            // 虚部
            for (int i = 0; i < expected_im.length; i++) {
                assertThat(result_im[i], is(closeTo(expected_im[i], 1E-12)));
            }
        }
    }
}
