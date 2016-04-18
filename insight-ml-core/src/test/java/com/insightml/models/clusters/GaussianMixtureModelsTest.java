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

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Assert;
import org.junit.Test;

import com.insightml.math.Vectors;
import com.insightml.math.distributions.IContDistribution;
import com.insightml.models.clusters.GaussianMixtureModels;
import com.insightml.models.clusters.GaussianMixtureModels.Components;
import com.insightml.models.clusters.GaussianMixtureModels.Point;
import com.insightml.utils.types.Triple;

public final class GaussianMixtureModelsTest {

    @Test
    public void test() {
        final Well19937c rnd = new Well19937c(0);
        final double[] data =
                Vectors.append(new NormalDistribution(rnd, 10, 5).sample(50),
                        new NormalDistribution(rnd, 4000, 100).sample(75));
        final Point[] points = new Point[data.length];
        for (int i = 0; i < points.length; ++i) {
            points[i] = new Point(data[i]);
        }
        final Triple<IContDistribution[], double[], double[][]> out =
                new GaussianMixtureModels().run(points, 2, 10);
        final Components result = new Components(out.getFirst(), out.getSecond());
        System.err.println(result);
        Assert.assertEquals(-685.3993, result.incompleteLogLikelihood(data), 0.0001);
        Assert.assertEquals(-690.2276, result.bic(data), 0.0001);
    }
}
