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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.insightml.data.samples.Sample;
import com.insightml.math.statistics.StatsBuilder;
import com.insightml.models.ILearner;
import com.insightml.models.LearnerArguments;
import com.insightml.models.LearnerArguments.Argument;
import com.insightml.models.meta.VoteModel.VoteStrategy;
import com.insightml.models.trees.MseSplitCriterion;
import com.insightml.models.trees.RegTree;
import com.insightml.models.trees.RegTree.FullStatisticsSupplier;
import com.insightml.models.trees.RegTree.StatsSupplier;
import com.insightml.utils.IArguments;

public class RandomForest extends Bagging<Sample> implements Serializable {
	private static final long serialVersionUID = -3175593175677762628L;

	RandomForest() {
	}

	public RandomForest(final IArguments arguments) {
		super(arguments, GBRT.getLearner(arguments,
				(Supplier<StatsBuilder<?>>) (arguments.bool("fullStatistics", false) ? new FullStatisticsSupplier()
						: new StatsSupplier()),
				false));
	}

	public RandomForest(final int trees, final int depth, final int minObs, final double isample, final double fsample,
			final VoteStrategy strategy) {
		this(trees, depth, depth, minObs, isample, fsample, strategy);
	}

	public RandomForest(final int trees, final int minDepth, final int maxDepth, final int minObs, final double isample,
			final double fsample, final VoteStrategy strategy) {
		super(trees, isample, fsample, strategy, getLearner(minDepth, maxDepth, minObs));
	}

	private static ILearner[] getLearner(final int minDepth, final int maxDepth, final int minObs) {
		final RegTree[] learner = new RegTree[maxDepth - minDepth + 1];
		for (int i = 0; i < learner.length; ++i) {
			learner[i] = new RegTree(i + minDepth, minObs, 1, MseSplitCriterion.factory(), new StatsSupplier(), false);
		}
		return learner;
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = super.arguments();
		args.add(GBRT.DEPTH, 4.0, 2, 24);
		args.add(GBRT.MIN_OBS, 10.0, 5, 20000);
		args.add(GBRT.NODE_PRED, 1.0, 1, 4);
		return args;
	}

	private Map<String, Object> usedArguments() {
		final Map<String, Object> args = new HashMap<>();
		final IArguments originalArgs = getOriginalArguments();
		for (final Argument arg : arguments()) {
			args.put(arg.getName(), originalArgs.get(arg.getName()));
		}
		return args;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(usedArguments(), getVoteStrategy());
	}

	@Override
	public boolean equals(final Object oth) {
		if (!(oth instanceof RandomForest)) {
			return false;
		}
		final RandomForest o = (RandomForest) oth;
		return o.usedArguments().equals(usedArguments()) && o.getVoteStrategy().equals(getVoteStrategy());
	}
}
