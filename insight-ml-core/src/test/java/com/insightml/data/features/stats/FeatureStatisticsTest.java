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

import org.junit.Assert;

import com.insightml.data.samples.ISamples;

public final class FeatureStatisticsTest extends AbstractFeatureStatsTest {

	@Override
	public void test(final ISamples<?, Double> instances) {
		final FeatureStatistics stats = new FeatureStatistics(instances, 0);
		final String str = stats.toString("f2");
		Assert.assertTrue(str.length() + "", str.length() > 95);
	}

}
