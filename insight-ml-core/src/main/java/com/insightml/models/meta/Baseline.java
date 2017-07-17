package com.insightml.models.meta;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.types.SumMap;
import com.insightml.models.DoubleModel;

public final class Baseline implements DoubleModel {
	private final double value;

	public Baseline(final double value) {
		this.value = value;
	}

	@Override
	public double[] predictDouble(final ISamples<? extends Sample, ?> instances) {
		final double[] preds = new double[instances.size()];
		for (int i = 0; i < preds.length; ++i) {
			preds[i] = value;
		}
		return preds;
	}

	@Override
	public SumMap<String> featureImportance() {
		return null;
	}
}