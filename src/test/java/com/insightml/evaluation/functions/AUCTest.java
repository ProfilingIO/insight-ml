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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.insightml.utils.types.collections.DoubleLinkedList;

public final class AUCTest {

	@Test
	public void testAuc() {
		final DoubleLinkedList[] scores = { new DoubleLinkedList(), new DoubleLinkedList() };
		final Random gen = new Random(0);
		for (int i = 0; i < 100000; i++) {
			scores[0].add(gen.nextGaussian());
			scores[1].add(gen.nextGaussian() + 1);
		}
		Assert.assertEquals(0.76, AUC.auc(scores), 0.001);
	}

	@Test
	public void testTies() {
		final DoubleLinkedList[] scores = { new DoubleLinkedList(), new DoubleLinkedList() };
		final Random gen = new Random(0);
		for (int i = 0; i < 100000; i++) {
			scores[0].add(gen.nextGaussian());
			scores[1].add(gen.nextGaussian() + 1);
		}

		// ties outside the normal range could cause index out of range
		scores[0].add(5.0);
		scores[0].add(5.0);
		scores[0].add(5.0);
		scores[0].add(5.0);

		scores[1].add(5.0);
		scores[1].add(5.0);
		scores[1].add(5.0);

		Assert.assertEquals(0.76, AUC.auc(scores), 0.001);
	}

	@Test
	public void testNormalize() {
		final AUC auc = new AUC();
		Assert.assertEquals(1, auc.normalize(1), 0.0001);
		Assert.assertEquals(0.75, auc.normalize(0.75), 0.0001);
		Assert.assertEquals(0.5, auc.normalize(0.5), 0.0001);
		Assert.assertEquals(0, auc.normalize(0), 0.0001);
	}

}
