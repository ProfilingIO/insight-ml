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
package com.insightml.models.meta;

import java.util.function.Supplier;

import com.insightml.evaluation.functions.MSE;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.math.statistics.StatsBuilder;
import com.insightml.models.trees.MseSplitCriterion;
import com.insightml.models.trees.RegTree;
import com.insightml.models.trees.RegTree.SimpleStatisticsSupplier;
import com.insightml.utils.IArguments;

public class GBRT extends GBM {
	private static final long serialVersionUID = -1383014150507996177L;

	public static final String DEPTH = "depth";
	public static final String MIN_OBS = "minObs";
	public static final String NODE_PRED = "nodePred";
	public static final String PARALLELIZE = "parallelize";

	public GBRT(final IArguments arguments) {
		super(arguments, new MSE(), getLearner(arguments, new SimpleStatisticsSupplier(), true), null);
	}

	public GBRT(final IArguments arguments, final ObjectiveFunction<? extends Object, ? super Double> objective,
			final Baseline predefinedBaseline) {
		super(arguments, objective, getLearner(arguments, new SimpleStatisticsSupplier(), true), predefinedBaseline);
	}

	public GBRT(final IArguments arguments, final int it, final double shrink, final double bag, final int minDepth,
			final int maxDepth, final int minObs) {
		this(arguments, it, shrink, bag, minDepth, maxDepth, minObs, true);
	}

	public GBRT(final IArguments arguments, final int it, final double shrink, final double bag, final int minDepth,
			final int maxDepth, final int minObs, final boolean parallelize) {
		super(arguments, it, shrink, bag, new MSE(),
				getLearner(minDepth, maxDepth, minObs, 1, new SimpleStatisticsSupplier(), parallelize));
	}

	public GBRT(final IArguments arguments, final int it, final double shrink, final double bag, final int minDepth,
			final int maxDepth, final int minObs, final int nodePred) {
		super(arguments, it, shrink, bag, new MSE(),
				getLearner(minDepth, maxDepth, minObs, nodePred, new SimpleStatisticsSupplier(), true));
	}

	public static RegTree[] getLearner(final IArguments arguments, final Supplier<StatsBuilder<?>> statisticsFactory,
			final boolean parallelize) {
		// TODO: Change back to min/max params
		return getLearner(arguments.toInt(DEPTH, 4),
				arguments.toInt(DEPTH, 4),
				arguments.toInt(MIN_OBS, 10),
				arguments.toInt(NODE_PRED, 1),
				statisticsFactory,
				arguments.bool(PARALLELIZE, parallelize));
	}

	private static RegTree[] getLearner(final int minDepth, final int maxDepth, final int minObs, final int nodePred,
			final Supplier<StatsBuilder<?>> statisticsFactory, final boolean parallelize) {
		final RegTree[] learner = new RegTree[maxDepth - minDepth + 1];
		for (int i = 0; i < learner.length; ++i) {
			learner[i] = new RegTree(i + minDepth, minObs, nodePred, MseSplitCriterion.factory(), statisticsFactory,
					parallelize);
		}
		return learner;
	}

}
