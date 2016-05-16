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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;

import com.insightml.TestInstance;
import com.insightml.data.samples.Samples;

public abstract class AbstractFunctionsTest {

	protected static void test(final ObjectiveFunction<Object, Object> function, final double expectedScore,
			final Serializable expected, final double predicted) {
		test(function, expectedScore, new Serializable[] { expected }, new Double[] { predicted });
	}

	protected static <E, P> void test(final ObjectiveFunction<E, P> function, final double expectedScore,
			final Serializable[] expected, final P[] predicted) {
		final List<TestInstance> instances = new LinkedList<>();
		for (final Serializable exp : expected) {
			instances.add(TestInstance.creat(exp));
		}
		final Samples<TestInstance, E> samples = new Samples<>(instances);
		Assert.assertEquals(expectedScore,
				function.label(predicted, samples.expected(0), samples.weights(0), samples, 0).getMean(), 0.0001);
	}
}
