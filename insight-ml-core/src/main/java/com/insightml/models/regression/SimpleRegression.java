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
package com.insightml.models.regression;

public final class SimpleRegression {

    private double sumX;
    private double sumXX;
    private double sumY;
    private double sumXY;

    private double n;

    private double xbar;
    private double ybar;

    private final boolean hasIntercept;

    public SimpleRegression(final boolean includeIntercept) {
        hasIntercept = includeIntercept;
    }

    public void addData(final double x, final double y, final double weight) {
        if (n == 0) {
            xbar = x * weight;
            ybar = y * weight;
        } else if (hasIntercept) {
            final double fact1 = 1.0 + n;
            final double fact2 = n / (1.0 + n);
            final double dx = x - xbar;
            final double dy = y - ybar;
            sumXX += dx * dx * fact2 * weight;
            sumXY += dx * dy * fact2 * weight;
            xbar += dx / fact1 * weight;
            ybar += dy / fact1 * weight;
        }
        if (!hasIntercept) {
            sumXX += x * x * weight;
            sumXY += x * y * weight;
        }
        sumX += x * weight;
        sumY += y * weight;
        n += weight;
    }

    public double[] regress() {
        final double slope = sumXY / sumXX;
        if (hasIntercept) {
            return new double[] {(sumY - slope * sumX) / n, slope };
        }
        return new double[] {slope };
    }

}
