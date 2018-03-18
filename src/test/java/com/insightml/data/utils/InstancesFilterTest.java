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
package com.insightml.data.utils;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.insightml.TestInstance;
import com.insightml.data.samples.ISamples;

public final class InstancesFilterTest {

	@Test
	public void testInstanceFilter() {
		final Iterable<TestInstance> instances = TestInstance.create("labelA", "labelA", "labelA", "labelB", "labelB",
				"labelA");
		final ISamples<TestInstance, ?> filtered = InstancesFilter.filterBySmallestLabelSize(instances);

		Assert.assertEquals(6, Lists.newLinkedList(instances).size());
		Assert.assertEquals(4, filtered.size());
	}

}
