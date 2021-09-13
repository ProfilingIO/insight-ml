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
package com.insightml.math.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.utils.types.collections.DoubleLinkedList;
import com.insightml.utils.types.collections.PairList;
import com.insightml.utils.ui.SimpleFormatter;
import com.insightml.utils.ui.UiUtils;

public class Correlation implements Comparable<Correlation> {

	private final float[][] arrays;

	private final Double covariance;
	private final double pearson;
	private final double spearman;
	private final double mean;

	public Correlation(final float[] x, final float[] y) {
		arrays = new float[2][x.length];
		for (int i = 0; i < x.length; ++i) {
			arrays[0][i] = x[i];
			arrays[1][i] = y[i];
		}
		final DoubleLinkedList x2 = new DoubleLinkedList();
		final DoubleLinkedList y2 = new DoubleLinkedList();
		for (int i = 0; i < x.length; ++i) {
			if (!Double.isNaN(x[i]) && x[i] != Double.NEGATIVE_INFINITY) {
				x2.add(x[i]);
				y2.add(y[i]);
			}
		}
		final double[] x2arr = x2.toArray();
		final double[] y2arr = y2.toArray();
		covariance = new Covariance().covariance(x2arr, y2arr);
		pearson = new PearsonsCorrelation().correlation(x2arr, y2arr);
		spearman = new SpearmansCorrelation().correlation(x2arr, y2arr);
		mean = (Math.abs(pearson) + Math.abs(spearman)) / 2;
	}

	public final double getMean() {
		return mean;
	}

	public final double getPearson() {
		return pearson;
	}

	public final double getSpearman() {
		return spearman;
	}

	public final double getCovariance() {
		return covariance;
	}

	@Override
	public final int compareTo(final Correlation o) {
		return Double.valueOf(Math.max(Math.abs(o.pearson), Math.abs(o.spearman)))
				.compareTo(Math.max(Math.abs(pearson), Math.abs(spearman)));
	}

	public final String getText() {
		final StringBuilder builder = new StringBuilder(512);
		final SimpleFormatter formatter = new SimpleFormatter(5, true);
		builder.append("Covariance: " + UiUtils.fill(formatter.format(getCovariance()), 12));
		builder.append("Pearson: " + UiUtils.fill(formatter.format(getPearson()), 12));
		builder.append("Spearman: " + formatter.format(getSpearman()));
		return builder.toString();
	}

	public final PairList<String, Map<Number, Number>> getChart(final CharSequence label) {
		final Map<Number, DescriptiveStatistics> points = new HashMap<>();
		for (int i = 0; i < arrays[0].length; ++i) {
			final double key = arrays[1][i];
			if (!points.containsKey(key)) {
				points.put(key, new DescriptiveStatistics());
			}
			points.get(key).addValue(arrays[0][i]);
		}
		final Map<Number, Number> average = new HashMap<>();
		final Map<Number, Number> median = new HashMap<>();
		for (final Entry<Number, DescriptiveStatistics> entry : points.entrySet()) {
			average.put(entry.getKey(), entry.getValue().getMean());
			median.put(entry.getKey(), entry.getValue().getPercentile(50));
		}
		final PairList<String, Map<Number, Number>> list = new PairList<>();
		list.add("Average " + label, average);
		list.add("Median " + label, median);
		return list;
	}

}
