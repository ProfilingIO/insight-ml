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

import java.io.Serializable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.FullStatistics;
import com.insightml.math.statistics.MutableStatistics;
import com.insightml.math.statistics.SimpleStatistics;
import com.insightml.math.statistics.Stats;
import com.insightml.models.AbstractDoubleLearner;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerInput;
import com.insightml.utils.Arguments;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;

public class RegTree extends AbstractDoubleLearner<Double> implements Serializable {
	private static final long serialVersionUID = 1517682772459402265L;

	private final SplitCriterionFactory splitCriterionFactory;
	private final boolean parallelize;
	private final Supplier<MutableStatistics> statisticsFactory;

	public RegTree(final IArguments arguments) {
		this(arguments, MseSplitCriterion.factory());
	}

	public RegTree(final IArguments arguments, final SplitCriterionFactory splitCriterionFactory) {
		super(arguments);
		this.splitCriterionFactory = splitCriterionFactory;
		parallelize = true;
		statisticsFactory = new StatsSupplier();
	}

	public RegTree(final int depth, final int minobs, final boolean parallelize) {
		this(depth, minobs, 1, MseSplitCriterion.factory(), new SimpleStatisticsSupplier(), parallelize);
	}

	public RegTree(final int depth, final int minobs, final double minImprovement, final boolean parallelize) {
		this(depth, minobs, minImprovement, 1, MseSplitCriterion.factory(), new SimpleStatisticsSupplier(),
				parallelize);
	}

	public RegTree(final int depth, final int minobs, final int nodePred, final boolean parallelize) {
		this(depth, minobs, nodePred, MseSplitCriterion.factory(), new SimpleStatisticsSupplier(), parallelize);
	}

	public RegTree(final int depth, final int minobs, final int nodePred,
			final SplitCriterionFactory splitCriterionFactory, final Supplier<MutableStatistics> statisticsFactory,
			final boolean parallelize) {
		this(depth, minobs, 0, nodePred, splitCriterionFactory, statisticsFactory, parallelize);
	}

	public RegTree(final int depth, final int minobs, final double minImprovement, final int nodePred,
			final SplitCriterionFactory splitCriterionFactory, final Supplier<MutableStatistics> statisticsFactory,
			final boolean parallelize) {
		super(new Arguments("depth", String.valueOf(depth), "minObs", String.valueOf(minobs), "minImprovement",
				String.valueOf(minImprovement), "nodePred", String.valueOf(nodePred)));
		this.parallelize = parallelize;
		this.splitCriterionFactory = splitCriterionFactory;
		this.statisticsFactory = statisticsFactory;
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = new LearnerArguments();
		args.add("depth", 4.0, 1, 24, 1);
		args.add("minObs", 10.0, 1, 10000, 10);
		args.add("minImprovement", 0.0, 0, 1000, 0);
		args.add("nodePred", 1.0, 1, 4, 1);
		return args;
	}

	@Override
	public final TreeModel run(final LearnerInput<? extends Sample, ? extends Double> input) {
		Check.state(input.valid == null);
		final ISamples<Sample, Double> train = (ISamples<Sample, Double>) input.getTrain();
		final int labelIndex = input.labelIndex;
		return run(train, labelIndex);
	}

	@Override
	public TreeModel run(final ISamples<Sample, Double> train, final int labelIndex) {
		return run(train, null, labelIndex);
	}

	public TreeModel run(final ISamples<Sample, Double> train, @Nullable final boolean[] featuresMask,
			final int labelIndex) {
		final TreeNode root = createTreeRoot(train, labelIndex);
		final int minObs = (int) argument("minObs");
		final SplitFinderContext context = new SplitFinderContext(train, featuresMask, (int) argument("depth"),
				argument("minImprovement"), labelIndex);
		final boolean[] subset = makeTrainingSubset(train);
		final String nodePrediction = getNodePredictionMode();
		new GrowJob(root, context, subset, 1, nodePrediction, splitCriterionFactory, minObs, statisticsFactory,
				parallelize).compute();
		return new TreeModel(root, train.featureNames());
	}

	public static TreeNode createTreeRoot(final ISamples<?, Double> train, final int labelIndex) {
		final Stats sRoot = new Stats();
		final Double[] labels = train.expected(labelIndex);
		final double[] weights = train.weights(labelIndex);
		for (int i = 0; i < labels.length; ++i) {
			if (labels[i] != null) {
				sRoot.add(labels[i], weights[i]);
			}
		}
		final TreeNode root = new TreeNode(sRoot.getMean(), sRoot);
		return root;
	}

	public static boolean[] makeTrainingSubset(final ISamples<Sample, Double> train) {
		final boolean[] subset = new boolean[train.size()];
		for (int i = 0; i < subset.length; ++i) {
			subset[i] = true;
		}
		return subset;
	}

	private String getNodePredictionMode() {
		final double nodePred = argument("nodePred");
		if (nodePred == 1) {
			return "mean";
		} else if (nodePred == 2) {
			return "median";
		} else if (nodePred == 3) {
			return "meandian";
		} else if (nodePred == 4) {
			return "mean-smoothed";
		}
		throw new IllegalArgumentException("Unknown mode: " + nodePred);
	}

	public static final class StatsSupplier implements Supplier<MutableStatistics>, Serializable {
		private static final long serialVersionUID = 7222508288626338172L;

		@Override
		public MutableStatistics get() {
			return new Stats();
		}
	}

	public static final class SimpleStatisticsSupplier implements Supplier<MutableStatistics>, Serializable {
		private static final long serialVersionUID = -39356810665839873L;

		@Override
		public MutableStatistics get() {
			return new SimpleStatistics();
		}
	}

	public static final class FullStatisticsSupplier implements Supplier<MutableStatistics>, Serializable {
		private static final long serialVersionUID = -39356810665839873L;

		@Override
		public MutableStatistics get() {
			return new FullStatistics();
		}
	}

}
