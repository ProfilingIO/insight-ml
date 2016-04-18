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
package com.insightml.models.clusters;

import com.insightml.math.distributions.GaussianDistribution;
import com.insightml.math.distributions.IContDistribution;
import com.insightml.math.distributions.Mixture;
import com.insightml.math.statistics.Stats;
import com.insightml.models.clusters.GaussianMixtureModels.Point;

public final class GaussianMixtureModels extends ExpectationMaximization<Point, IContDistribution> {

    @Override
    protected IContDistribution[] init(final Point[] data, final int numComponents) {
        final IContDistribution[] comps = new IContDistribution[numComponents];
        final Stats stats = new Stats();
        for (final Point val : data) {
            stats.add(val.val);
        }
        final double interval = (stats.getMax() - stats.getMin()) / numComponents;
        for (int i = 0; i < numComponents; ++i) {
            comps[i] = new GaussianDistribution(interval * i, interval / 2);
        }
        return comps;
    }

    @Override
    protected double likelihood(final Point point, final IContDistribution dist) {
        return dist.probability(point.val);
    }

    @Override
    protected IContDistribution maximization(final Point[] data, final double[][] points,
            final int c) {
        double mean = 0;
        double softCount = 0;
        for (int p = 0; p < points.length; ++p) {
            if (points[p][c] != 0) {
                mean += points[p][c] * data[p].val;
                softCount += points[p][c];
            }
        }
        if (mean != 0) {
            mean /= softCount;
        }
        double var = 0;
        double diff = 0;
        for (int j = 0; j < points.length; ++j) {
            if (points[j][c] != 0) {
                diff = data[j].val - mean;
                var += points[j][c] * diff * diff;
            }
        }
        if (var != 0) {
            var /= softCount;
        }

        return new GaussianDistribution(mean, Math.sqrt(var));
    }

    public static final class Components extends Mixture {

        public Components(final IContDistribution[] dists, final double[] weights) {
            super(dists, weights);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < dists.length; ++i) {
                builder.append(",N(" + dists[i].expectedValue() + ","
                        + dists[i].standardDeviation() + ")," + getWeight(i));
            }
            return builder.substring(1);
        }
    }

    static final class Point {

        final double val;

        public Point(final double val) {
            this.val = val;
        }
    }

}
