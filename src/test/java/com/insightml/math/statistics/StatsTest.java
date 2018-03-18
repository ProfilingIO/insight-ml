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
package com.insightml.math.statistics;

import org.junit.Assert;
import org.junit.Test;

public class StatsTest {

	@Test
	public void test() {
		Stats stats = new Stats();
		Assert.assertEquals(0, stats.getN());
		Assert.assertEquals(0.0, stats.getSum(), .0001);

		stats.add(-1);
		stats.add(3);

		Assert.assertEquals(-1.0, stats.getMin(), .0001);
		Assert.assertEquals(1.0, stats.getMean(), .0001);
		Assert.assertEquals(3.0, stats.getMax(), .0001);
		Assert.assertEquals(2, stats.getN());
		Assert.assertEquals(2.0, stats.getSum(), .0001);
		Assert.assertEquals(8.0, stats.variance(), .0001);
		Assert.assertEquals(2.8284, stats.getStandardDeviation(), .0001);

		final double[] vals = new double[] { 600, 470, 170, 430, 300 };
		stats = new Stats(vals).copy();
		Assert.assertEquals(394, stats.getMean(), .0001);
		Assert.assertEquals(27130, stats.variance(), .0001);
		Assert.assertEquals(164.7119, stats.getStandardDeviation(), .0001);

		stats = new Stats(new double[] { 600, 470, 170 }).copy();
		stats.add(new Stats(new double[] { 430, 300 }));
		Assert.assertEquals(394, stats.getMean(), .0001);
		Assert.assertEquals(27130, stats.variance(), .0001);
		Assert.assertEquals(164.7119, stats.getStandardDeviation(), .0001);
	}

}
