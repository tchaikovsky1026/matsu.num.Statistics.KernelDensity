/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.17
 */
package matsu.num.statistics.kerneldensity;

/**
 * {@link NaiveFilterZeroFillingConvolutionParallelizable}
 * のpublic Wrapper.
 * 
 * @author Matsuura Y.
 */
public final class NaiveFilterZeroFillingConvolutionPublicWrapper
        implements FilterZeroFillingConvolution {

    private final FilterZeroFillingConvolution wrapped =
            NaiveFilterZeroFillingConvolutionParallelizable.instance();

    /**
     * 唯一のコンストラクタ.
     */
    public NaiveFilterZeroFillingConvolutionPublicWrapper() {
        super();
    }

    @Override
    public PartialApplied applyPartial(double[] filter) {
        return new PartialApplied(wrapped.applyPartial(filter));
    }

    public static final class PartialApplied implements FilterZeroFillingConvolution.PartialApplied {

        private final FilterZeroFillingConvolution.PartialApplied wrapped;

        private PartialApplied(FilterZeroFillingConvolution.PartialApplied wrapped) {
            super();
            this.wrapped = wrapped;
        }

        @Override
        public double[] compute(double[] signal) {
            return wrapped.compute(signal);
        }

        @Override
        public double[] compute(double[] signal, boolean parallel) {
            return wrapped.compute(signal, parallel);
        }

    }

}
