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

import com.insightml.data.samples.Sample;
import com.insightml.models.LearnerArguments;
import com.insightml.models.meta.VoteModel.VoteStrategy;
import com.insightml.utils.IArguments;

public final class RandomForest extends Bagging<Sample> {

	public RandomForest(final IArguments arguments) {
		super(arguments, GBRT.getLearner(arguments));
	}

	public RandomForest(final int trees, final int depth, final int minObs, final double isample, final double fsample,
			final VoteStrategy strategy) {
		this(trees, depth, depth, minObs, isample, fsample, strategy);
	}

	public RandomForest(final int trees, final int minDepth, final int maxDepth, final int minObs, final double isample,
			final double fsample, final VoteStrategy strategy) {
		super(trees, isample, fsample, strategy, GBRT.getLearner(minDepth, maxDepth, minObs));
	}

	@Override
	public LearnerArguments arguments() {
		final LearnerArguments args = super.arguments();
		args.add("depth", 4.0, 2, 24);
		args.add("minObs", 10.0, 10, 10000);
		return args;
	}

}
