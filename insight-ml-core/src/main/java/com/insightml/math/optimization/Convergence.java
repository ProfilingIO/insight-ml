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
package com.insightml.math.optimization;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;

final class Convergence extends SimpleValueChecker {

	final int maxIt;
	final Double trainMax;

	private int it;

	Convergence(final int maxIt, final Double trainMax, final double precision) {
		super(precision, precision);
		this.maxIt = maxIt;
		this.trainMax = trainMax;
	}

	@Override
	public boolean converged(final int iteration, final PointValuePair previous, final PointValuePair current) {
		if (++it >= maxIt) {
			return true;
		}
		if (trainMax != null && current.getValue() >= trainMax) {
			return true;
		}
		return super.converged(iteration, previous, current);
	}

}