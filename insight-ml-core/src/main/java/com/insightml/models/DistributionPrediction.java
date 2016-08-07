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

import java.util.List;

import com.insightml.math.statistics.Stats;

public final class DistributionPrediction {
	private final Stats prediction;
	private final Object debug;

	public DistributionPrediction(final Stats prediction, final Object debug) {
		this.prediction = prediction;
		this.debug = debug;
	}

	public Stats getPrediction() {
		return prediction;
	}

	public Object getDebug() {
		return debug;
	}

	public void add(final DistributionPrediction o) {
		prediction.add(o.prediction);
		((List) debug).add(o.debug);
	}
}
