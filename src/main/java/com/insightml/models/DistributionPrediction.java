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
package com.insightml.models;

import com.google.common.base.MoreObjects;
import com.insightml.math.statistics.IStats;

public final class DistributionPrediction {
	private final IStats prediction;
	private final Object debug;

	public DistributionPrediction(final IStats prediction, final Object debug) {
		this.prediction = prediction;
		this.debug = debug;
	}

	public IStats getPrediction() {
		return prediction;
	}

	public Object getDebug() {
		return debug;
	}

	@Override
	public String toString() {
		final MoreObjects.ToStringHelper builder = MoreObjects.toStringHelper(this).add("prediction", prediction);
		if (debug != null) {
			builder.add("debug", debug);
		}
		return builder.toString();
	}
}
