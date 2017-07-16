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
import com.insightml.utils.ui.UiUtils;

public final class Split extends AbstractSplit implements Cloneable {
	private static final long serialVersionUID = -8060176890051949338L;

	private double thresh;
	private String fname;

	Split() {
	}

	Split(final double threshold, final Stats statsL, final Stats statsR, final double improvement,
			final int lastIndexLeft, final int feature, final String[] featureNames) {
		super(statsL, statsR, improvement, lastIndexLeft, feature);
		thresh = threshold;
		fname = featureNames[feature];
	}

	@Override
	public String getFeatureName() {
		return fname;
	}

	@Override
	public boolean moveRight(final double[] features) {
		return features[feature] > thresh;
	}

	@Override
	public String explain(final double[] features) {
		if (moveRight(features)) {
			return fname + " (" + UiUtils.format(features[feature]) + ") > " + UiUtils.format(thresh);
		}
		return fname + " (" + UiUtils.format(features[feature]) + ") \u2264 " + UiUtils.format(thresh);
	}

	@Override
	protected Object clone() {
		try {
			return super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return fname + " \u2264 " + UiUtils.format(thresh) + " (" + UiUtils.format(improve) + "/"
				+ UiUtils.format(getWeightSum()) + '=' + UiUtils.format(improve / getWeightSum()) + ")";
	}
}