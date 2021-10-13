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
package com.insightml.evaluation.simulation;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.Pair;

import com.insightml.data.samples.Sample;
import com.insightml.utils.Utils;

public final class SplitSimulation<I extends Sample> extends AbstractSimulation<I> {

	private static final long serialVersionUID = -2994345013519681199L;

	private final double trainFraction;

	public SplitSimulation(final double trainFraction, final SimulationResultConsumer database) {
		super((int) (trainFraction * 100) + "/" + (int) ((1 - trainFraction) * 100) + " Split", database, null);
		this.trainFraction = trainFraction;
	}

	@Override
	public String getDescription() {
		return "Trains on " + trainFraction + " and evaluates on " + (1 - trainFraction);
	}

	@Override
	public <E, P> ISimulationResults<E, P>[] run(final Iterable<I> train, final SimulationSetup<I, E, P> setup) {
		final Pair<Iterable<I>, List<I>> split = split(train, trainFraction, Utils.random());
		return run(() -> split.getFirst(), () -> split.getSecond(), setup);
	}

	public static <S extends Sample> Pair<Iterable<S>, List<S>> split(final Iterable<S> instances,
			final double trainFraction, final Random random) {
		if (trainFraction == 1.0) {
			return new Pair<>(instances, null);
		}
		final List<S> train = new LinkedList<>();
		final List<S> test = new LinkedList<>();
		for (final S sample : instances) {
			if (random.nextDouble() < trainFraction) {
				train.add(sample);
			} else {
				test.add(sample);
			}
		}
		return new Pair<>(train, test);
	}
}
