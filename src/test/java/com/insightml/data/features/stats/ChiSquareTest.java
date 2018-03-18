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
package com.insightml.data.features.stats;

import java.util.Map;

import org.junit.Ignore;

import com.insightml.data.samples.ISamples;

@Ignore
public final class ChiSquareTest extends AbstractFeatureStatsTest {

	@Override
	public void test(final ISamples<?, Double> inst) {
		final ChiSquare mi = new ChiSquare(0.25);
		final FeatureStatistics instances = new FeatureStatistics(inst, 0);
		final Map<String, Double> result = mi.run(instances);

		System.err.println(result);
	}

}
