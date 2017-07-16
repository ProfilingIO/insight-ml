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
package com.insightml.models.trees;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.Vectors;
import com.insightml.math.statistics.Stats;
import com.insightml.models.AbstractDoubleLearner;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Arguments;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;

public final class RegTree extends AbstractDoubleLearner<Double> {
	private final boolean parallelize;

	private static final ExecutorService executor = Executors.newFixedThreadPool(16);

	public RegTree(final IArguments arguments) {
		super(arguments);
		parallelize = true;
	}

	public RegTree(final int depth, final int minobs, final boolean parallelize) {
		super(new Arguments("depth", String.valueOf(depth), "minObs", String.valueOf(minobs)));
		this.parallelize = parallelize;
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("depth", 4.0, 1, 24);
		args.add("minObs", 10.0, 1, 20000);
		return args;
	}

	@Override
	public TreeModel run(final LearnerInput<? extends Sample, ? extends Double> input) {
		Check.state(input.valid == null);
		final ISamples<Sample, Double> train = (ISamples<Sample, Double>) input.getTrain();
		final Stats sRoot = new Stats();
		sRoot.add(0, Vectors.sum(train.weights(input.labelIndex)));
		final TreeNode root = new TreeNode(sRoot);
		final SplitFinderContext context = new SplitFinderContext(train, (int) argument("depth"),
				(int) argument("minObs"), input.labelIndex);
		final boolean[] subset = new boolean[train.size()];
		for (int i = 0; i < subset.length; ++i) {
			subset[i] = true;
		}
		new GrowJob(root, context, subset, 1, parallelize).compute();
		return new TreeModel(root, train.featureNames());
	}

	public static ThresholdSplitFinder createThresholdSplitFinder(final SplitFinderContext context,
			final boolean[] subset) {
		int samples = 0;
		double weightSum = 0;
		double labelSum = 0;
		for (int i = 0; i < context.weights.length; ++i) {
			if (subset == null || subset[i]) {
				++samples;
				weightSum += context.weights[i];
				labelSum += context.expected[i] * context.weights[i];
			}
		}
		return new ThresholdSplitFinder(context, subset, samples, labelSum, weightSum);
	}

}
