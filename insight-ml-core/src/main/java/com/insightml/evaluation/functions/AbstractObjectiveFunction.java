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
package com.insightml.evaluation.functions;

import com.insightml.math.distributions.IDiscreteDistribution;
import com.insightml.utils.types.AbstractModule;

public abstract class AbstractObjectiveFunction<E, T> extends AbstractModule implements IObjectiveFunction<E, T> {
	private static final long serialVersionUID = -7212322445994504764L;

	@Override
	public double normalize(final double score) {
		return score;
	}

	protected static final double[] toDouble(final Object prediction, final Object label) {
		double pred = 0;
		double actual = 0;
		if (label instanceof Boolean) {
			actual = (Boolean) label ? 1.0 : 0.0;
			pred = ((Number) prediction).doubleValue();
		} else if (label instanceof String) {
			actual = 1.0;
			pred = ((IDiscreteDistribution<Object>) prediction).get(label);
		} else {
			actual = ((Number) label).doubleValue();
			pred = prediction instanceof IDiscreteDistribution
					? ((IDiscreteDistribution<Double>) prediction).getMax().getFirst()
					: ((Number) prediction).doubleValue();
		}
		return new double[] { pred, actual };
	}

}
