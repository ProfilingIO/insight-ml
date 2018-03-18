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

import com.insightml.utils.types.AbstractClass;

public class Mixture extends AbstractClass {

    protected final IContDistribution[] dists;
    private final double[] weights;

    public Mixture(final IContDistribution[] dists, final double[] weights) {
        this.dists = dists;
        this.weights = weights;
    }

    protected final double getWeight(final int i) {
        return weights[i];
    }

    public final double incompleteLogLikelihood(final double[] data) {
        double sum = 0;
        for (final double element : data) {
            double sum2 = 0;
            for (int j = 0; j < dists.length; ++j) {
                sum2 += weights[j] * dists[j].probability(element);
            }
            sum += Math.log(sum2);
        }
        return sum;
    }

    public final double bic(final double[] data) {
        return incompleteLogLikelihood(data) - dists.length * 0.5 * Math.log(data.length);
    }
}
