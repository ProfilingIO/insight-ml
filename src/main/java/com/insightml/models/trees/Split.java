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

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.insightml.math.statistics.IStats;
import com.insightml.utils.ui.UiUtils;

public final class Split extends AbstractSplit implements Cloneable {
	private static final long serialVersionUID = -8060176890051949338L;

	private double thresh;
	private String fname;
	private int lastIndexNaN;

	Split() {
	}

	Split(final double threshold, final IStats statsL, final IStats statsR, final IStats statsNaN,
			final double improvement, final int lastIndexNaN, final int lastIndexLeft, final int feature,
			final String[] featureNames) {
		super(statsL, statsR, statsNaN, improvement, lastIndexLeft, feature);
		Preconditions.checkArgument(lastIndexNaN < lastIndexLeft);
		thresh = threshold;
		fname = featureNames[feature];
		this.lastIndexNaN = lastIndexNaN;
	}

	@Override
	public String getFeatureName() {
		return fname;
	}

	public double getFeatureValueThreshold() {
		return thresh;
	}

	public int getLastIndexNaN() {
		return lastIndexNaN;
	}

	@Override
	public int selectChild(final float[] features) {
		if (features[feature] > thresh) {
			return 1;
		}
		return lastIndexNaN >= 0 && features[feature] == ThresholdSplitFinder.VALUE_MISSING ? 2 : 0;
	}

	public TreeNode selectChild(final float[] features, final TreeNode[] children) {
		if (features[feature] > thresh) {
			return children[1];
		}
		final boolean isNan = lastIndexNaN >= 0 && features[feature] == ThresholdSplitFinder.VALUE_MISSING;
		if (isNan) {
			return children.length > 2 ? children[2] : null;
		}
		return children[0];
	}

	@Override
	public String explain(final float[] features) {
		return explain(fname, features);
	}

	public String explain(final String featureName, final float[] features) {
		final int child = selectChild(features);
		final double featureValue = features[feature];
		if (child == 1) {
			return featureName + " (" + UiUtils.format(featureValue) + ") > " + UiUtils.format(thresh);
		} else if (child == 2) {
			return featureName + " (" + UiUtils.format(featureValue) + ") missing";
		}
		return featureName + " (" + UiUtils.format(featureValue) + ") \u2264 " + UiUtils.format(thresh);
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
	public int hashCode() {
		return Objects.hash(feature, thresh);
	}

	public String getRulePresentation() {
		return " \u2264 " + UiUtils.format(thresh);
	}

	@Override
	public String toString() {
		final double weightSum = getWeightSum();
		return fname + getRulePresentation() + " (" + UiUtils.format(improve) + "/" + UiUtils.format(weightSum) + '='
				+ UiUtils.format(improve / weightSum) + ")";
	}
}