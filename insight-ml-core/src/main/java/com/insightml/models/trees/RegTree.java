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

public class RegTree extends AbstractDoubleLearner<Double> {
	private final SplitCriterionFactory splitCriterionFactory;
	private final boolean parallelize;

	public RegTree(final IArguments arguments) {
		this(arguments, MseSplitCriterion::create);
	}

	public RegTree(final IArguments arguments, final SplitCriterionFactory splitCriterionFactory) {
		super(arguments);
		this.splitCriterionFactory = splitCriterionFactory;
		parallelize = true;
	}

	public RegTree(final int depth, final int minobs, final boolean parallelize) {
		this(depth, minobs, MseSplitCriterion::create, parallelize);
	}

	public RegTree(final int depth, final int minobs, final SplitCriterionFactory splitCriterionFactory,
			final boolean parallelize) {
		super(new Arguments("depth", String.valueOf(depth), "minObs", String.valueOf(minobs)));
		this.parallelize = parallelize;
		this.splitCriterionFactory = splitCriterionFactory;
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("depth", 4.0, 1, 24);
		args.add("minObs", 10.0, 1, 20000);
		return args;
	}

	@Override
	public final TreeModel run(final LearnerInput<? extends Sample, ? extends Double> input) {
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
		new GrowJob(root, context, subset, 1, splitCriterionFactory, parallelize).compute();
		return new TreeModel(root, train.featureNames());
	}

}
