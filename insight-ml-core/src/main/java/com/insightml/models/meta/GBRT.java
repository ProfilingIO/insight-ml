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

import com.insightml.evaluation.functions.MSE;
import com.insightml.evaluation.functions.ObjectiveFunction;
import com.insightml.models.ILearner;
import com.insightml.models.trees.RegTree;
import com.insightml.utils.IArguments;

public class GBRT extends GBM {

	public GBRT(final IArguments arguments) {
		super(arguments, new MSE(), getLearner(arguments, true), null);
	}

	public GBRT(final IArguments arguments, final ObjectiveFunction<? extends Object, ? super Double> objective,
			final Baseline predefinedBaseline) {
		super(arguments, objective, getLearner(arguments, true), predefinedBaseline);
	}

	public GBRT(final int it, final double shrink, final double bag, final int minDepth, final int maxDepth,
			final int minObs) {
		super(it, shrink, bag, new MSE(), getLearner(minDepth, maxDepth, minObs, true));
	}

	public static ILearner[] getLearner(final IArguments arguments, final boolean parallelize) {
		// TODO: Change back to min/max params
		return getLearner(arguments.toInt("depth", 4), arguments.toInt("depth", 4), arguments.toInt("minObs", 10),
				parallelize);
	}

	private static ILearner[] getLearner(final int minDepth, final int maxDepth, final int minObs,
			final boolean parallelize) {
		final RegTree[] learner = new RegTree[maxDepth - minDepth + 1];
		for (int i = 0; i < learner.length; ++i) {
			learner[i] = new RegTree(i + minDepth, minObs, parallelize);
		}
		return learner;
	}

}
