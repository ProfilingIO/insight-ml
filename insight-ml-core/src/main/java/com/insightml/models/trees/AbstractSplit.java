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

import com.insightml.math.statistics.Stats;
import com.insightml.utils.types.AbstractClass;

abstract class AbstractSplit extends AbstractClass implements ISplit {

	private static final long serialVersionUID = -6931400300729153655L;

	private Stats statsL;
	private Stats statsR;
	private Stats statsNaN;
	private transient int lastIndexLeft;
	double improve;

	int feature;

	AbstractSplit() {
	}

	AbstractSplit(final Stats statsL, final Stats statsR, final Stats statsNaN, final double improvement,
			final int lastIndexLeft, final int feature) {
		this.statsL = statsL;
		this.statsR = statsR;
		this.statsNaN = statsNaN;
		improve = improvement;
		this.lastIndexLeft = lastIndexLeft;
		this.feature = feature;
	}

	@Override
	public int getFeature() {
		return feature;
	}

	@Override
	public double getWeightSum() {
		return statsL.getSumOfWeights() + statsR.getSumOfWeights();
	}

	@Override
	public int getLastIndexLeft() {
		return lastIndexLeft;
	}

	@Override
	public final double getImprovement() {
		return improve;
	}

	@Override
	public final boolean isBetterThan(final ISplit split) {
		final AbstractSplit splitt = (AbstractSplit) split;
		return isFirstBetter(improve, splitt.improve, feature, splitt.feature);
	}

	public static boolean isFirstBetter(final double improvement1, final double improvement2, final int feature1,
			final int feature2) {
		if (improvement1 < improvement2) {
			return false;
		}
		if (improvement1 > improvement2) {
			return true;
		}
		return feature1 == feature2 ? true : feature1 < feature2;
	}

	@Override
	public final Stats getStatsL() {
		return statsL;
	}

	@Override
	public final Stats getStatsR() {
		return statsR;
	}

	public Stats getStatsNaN() {
		return statsNaN;
	}

}
