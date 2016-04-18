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

import com.insightml.math.Maths;
import com.insightml.math.types.Interval;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.ui.SimpleFormatter;

public abstract class AbstractGaussian extends AbstractClass implements IGaussian {

	private static final long serialVersionUID = 885218738915640600L;

	@Override
	public double bhattacharyyaDistance(final IGaussian other) {
		final double var1 = Maths.pow(standardDeviation(), 2);
		final double var2 = Maths.pow(other.standardDeviation(), 2);

		final double left = 1.0 / 4 * FastMath.log(1.0 / 4 * (var1 / var2 + var2 / var1 + 2));
		return left + 1.0 / 4 * (Maths.pow(expectedValue() - other.expectedValue(), 2) / (var1 + var2));
	}

	@Override
	public final double significance(final double precision) {
		final double stddev = standardDeviation();
		return stddev <= precision ? 1 : precision / stddev;
	}

	@Override
	public double position(final double value, final double timesStddev) {
		return confidenceInterval(timesStddev).relativePosition(value);
	}

	@Override
	public final String toStringInterval(final int precision) {
		return toStringInterval(this, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 2, precision);
	}

	public static final String toStringInterval(final IContDistribution gaussian, final double min, final double max,
			final int timesStddev, final int precision) {
		final SimpleFormatter format = new SimpleFormatter(precision, false);
		final double stddev = gaussian.standardDeviation();
		if (stddev < 0.01 || Double.isNaN(stddev)) {
			return format.format(gaussian.expectedValue());
		}
		final Interval conf = gaussian.confidenceInterval(timesStddev);
		return format.format(Math.max(min, conf.getStart())) + " \u2013 " + format.format(Math.min(max, conf.getEnd()));
	}
}
