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

import org.junit.Ignore;
import org.junit.Test;

public final class GiniTest extends AbstractFunctionsTest {

	@Test
	@Ignore
	public void testGiniMetric() {
		final Gini gini = new Gini(true);

		test(gini, 1.0, new Double[] { 1.0, 2.0, 3.0 }, new Double[] { 10.0, 20.0, 30.0 });
		test(gini, -1.0, new Double[] { 1.0, 2.0, 3.0 }, new Double[] { 30.0, 20.0, 10.0 });
		test(gini, -1.0, new Double[] { 1.0, 2.0, 3.0 }, new Double[] { .0, .0, .0 });
		test(gini, 1.0, new Double[] { 3.0, 2.0, 1.0 }, new Double[] { .0, .0, .0 });
		test(gini, -0.8, new Double[] { 1.0, 2.0, 4.0, 3.0 }, new Double[] { .0, .0, .0, .0 });
		test(gini, 1, new Double[] { 2.0, 1.0, 4.0, 3.0 }, new Double[] { 0.0, 0.0, 2.0, 1.0 });
		test(gini, 0, new Double[] { 0.0, 20.0, 40.0, 0.0, 10.0 }, new Double[] { 40.0, 40.0, 10.0, 5.0, 5.0, });
		test(gini, 0.6, new Double[] { 40.0, 0.0, 20.0, 0.0, 10.0 }, new Double[] { 1000000.0, 40.0, 40.0, 5.0, 5.0, });
		test(gini, 1, new Double[] { 40.0, 20.0, 10.0, 0.0, 0.0 }, new Double[] { 40.0, 20.0, 10.0, 0.0, 0.0, });
		test(gini, -0.33333333, new Double[] { 1.0, 1.0, 0.0, 1.0 }, new Double[] { 0.86, 0.26, 0.52, 0.32, });
	}
}
