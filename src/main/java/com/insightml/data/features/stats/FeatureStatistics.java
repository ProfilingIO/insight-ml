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
package com.insightml.data.features.stats;

import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.types.IntSumMap;
import com.insightml.math.types.IntSumMap.IntSumMapBuilder;
import com.insightml.utils.Sets;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.cache.Cache;
import com.insightml.utils.types.cache.SimpleCache;
import com.insightml.utils.ui.SimpleFormatter;
import com.insightml.utils.ui.UiUtils;

public final class FeatureStatistics extends AbstractClass {

	private final ISamples<Sample, Double> instances;
	private final int labelIndex;

	private final Cache<String, DescriptiveStatistics> stats = new SimpleCache<>(DescriptiveStatistics.class, 500,
			false);
	private final IntSumMap<CharSequence> nans;

	public FeatureStatistics(final ISamples<?, Double> instances, final int labelIndex) {
		this.instances = (ISamples<Sample, Double>) instances;
		this.labelIndex = labelIndex;
		final String[] featureNames = this.instances.featureNames();
		final double[][] feats = this.instances.features();
		final IntSumMapBuilder<CharSequence> nan = IntSumMap.builder(false, 16);
		for (int i = 0; i < this.instances.size(); ++i) {
			for (int j = 0; j < featureNames.length; ++j) {
				final double value = feats[i][j];
				if (Double.isNaN(value) || value == Double.NEGATIVE_INFINITY) {
					nan.increment(featureNames[j], 1);
				} else {
					stats.getOrLoad(featureNames[j]).addValue(value);
				}
			}
		}
		nans = nan.build(0);
	}

	public ISamples<Sample, Double> getInstances() {
		return instances;
	}

	public int getLabelIndex() {
		return labelIndex;
	}

	public int getN(final String feature) {
		final DescriptiveStatistics stat = stats.get(feature);
		return (int) ((stat == null ? 0 : stat.getN()) + getNull(feature));
	}

	public int getNull(final String feature) {
		return nans.get(feature, 0);
	}

	public Double getMin(final String feature) {
		final DescriptiveStatistics stat = stats.get(feature);
		return stat == null ? null : stat.getMin();
	}

	public Double getMax(final String feature) {
		final DescriptiveStatistics stat = stats.get(feature);
		return stat == null ? null : stat.getMax();
	}

	private Double getMedian(final String feature) {
		final DescriptiveStatistics stat = stats.get(feature);
		return stat == null ? null : stat.getPercentile(50);
	}

	public Double getMean(final String feature) {
		final DescriptiveStatistics stat = stats.get(feature);
		return stat == null ? null : stat.getMean();
	}

	public Double getStandardDeviation(final String feature) {
		final DescriptiveStatistics stat = stats.get(feature);
		return stat == null ? null : stat.getStandardDeviation();
	}

	public Double getVariance(final String feature) {
		final DescriptiveStatistics stat = stats.get(feature);
		return stat == null ? null : stat.getVariance();
	}

	private int getDistinct(final String feature) {
		final DescriptiveStatistics stat = stats.get(feature);
		if (stat == null) {
			return 0;
		}
		final Set<Double> values = Sets.create((int) stat.getN());
		for (final double val : stat.getValues()) {
			values.add(val);
		}
		return values.size();
	}

	public DescriptiveStatistics getStatistics(final String feature) {
		return stats.get(feature);
	}

	String toString(final String feature) {
		final StringBuilder builder = new StringBuilder(UiUtils.fill(feature, 50));
		final SimpleFormatter formatter = new SimpleFormatter(5, true);
		final Double min = getMin(feature);
		final Double max = getMax(feature);
		final Double median = getMedian(feature);
		final Double mean = getMean(feature);
		final Double sd = getStandardDeviation(feature);
		builder.append(UiUtils.fill("[" + (min == null ? null : formatter.format(min)) + "; "
				+ (max == null ? null : formatter.format(max)) + "]", 25));
		builder.append("null: " + UiUtils.fill(formatter.format(getNull(feature) * 1.0 / getN(feature)), 10));
		builder.append("median: " + (median == null ? null : UiUtils.fill(formatter.format(median), 10)));
		builder.append("mean: " + UiUtils.fill(
				(mean == null ? null : formatter.format(mean)) + " \u00B1" + (sd == null ? null : formatter.format(sd)),
				30));
		builder.append("distinct: " + getDistinct(feature));
		return builder.toString();
	}

}
