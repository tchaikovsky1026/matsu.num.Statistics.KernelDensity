/*
 * Copyright © 2025 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

/*
 * 2025.12.18
 */
package matsu.num.statistics.kerneldensity;

import matsu.num.statistics.kerneldensity.conv.CyclicConvolutions;

/**
 * {@link EffectiveFilterZeroFillingConvolution}
 * のpublic Wrapper.
 * 
 * @author Matsuura Y.
 */
public final class EffectiveFilterZeroFillingConvolutionPublicWrapper
        implements FilterZeroFillingConvolution {
    private final FilterZeroFillingConvolution wrapped =
            EffectiveFilterZeroFillingConvolution.instanceOf(CyclicConvolutions.fftBased());

    /**
     * 唯一のコンストラクタ.
     */
    public EffectiveFilterZeroFillingConvolutionPublicWrapper() {
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
