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

public final class MutualInformationTest extends AbstractFeatureStatsTest {

	@Override
	public void test(final ISamples<?, Double> inst) {
		final MutualInformation mi = new MutualInformation(0.25);
		final FeatureStatistics instances = new FeatureStatistics(inst, 0);

		Assert.assertEquals(0.6522, mi.compute(instances, 0, "f1"), 0.0001);
		Assert.assertEquals(0.6458, mi.compute(instances, 1, "f2"), 0.0001);
		Assert.assertEquals(0.6409, mi.compute(instances, 2, "f3"), 0.0001);
		Assert.assertEquals(0.6458, mi.compute(instances, 3, "f4"), 0.0001);
		Assert.assertEquals(0.6437, mi.compute(instances, 4, "f5"), 0.0001);
	}
}
