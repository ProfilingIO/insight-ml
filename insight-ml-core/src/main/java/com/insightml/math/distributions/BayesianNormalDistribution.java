/*
 * Copyright (C) 2016 Stefan Henß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.insightml.math.distributions;

import org.apache.commons.math3.util.FastMath;

import com.insightml.math.statistics.Stats;
import com.insightml.math.types.Interval;

public final class BayesianNormalDistribution extends AbstractGaussian {

    private static final long serialVersionUID = -2071215251528854401L;

    private final double mean;
    private final double precision;

    public BayesianNormalDistribution(final double mean0, final double stddev0, final Stats stats) {
        final double var0 = stddev0 * stddev0;
        final int n = (int) stats.getN();
        if (n == 0) {
            mean = mean0;
            precision = 1.0 / var0;
        } else {
            final double var = stats.variance();
            mean = n * var0 / (n * var0 + var) * stats.getMean() + var / (n * var0 + var) * mean0;
            precision = n * 1.0 / var + 1.0 / var0;
        }
    }

    @Override
    public double expectedValue() {
        return mean;
    }

    public double getPrecision() {
        return precision;
    }

    @Override
    public double standardDeviation() {
        return FastMath.sqrt(1.0 / getPrecision());
    }

    @Override
    public double maxLikelihood() {
        return expectedValue();
    }

    @Override
    public double probability(final double d) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double logLikelihood(final double d) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Interval confidenceInterval(final double factorStddev) {
        final double stddev = standardDeviation();
        final double rangeMax = mean + stddev * factorStddev;
        final double rangeMin = mean - stddev * factorStddev;
        return new Interval(rangeMin, rangeMax);
    }

}
