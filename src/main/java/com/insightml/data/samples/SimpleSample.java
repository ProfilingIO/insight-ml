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
package com.insightml.data.samples;

import com.insightml.data.features.FeaturesConsumer;
import com.insightml.utils.Check;

public class SimpleSample implements Sample, Cloneable {
	private static final long serialVersionUID = 4596410329818803518L;

	private final int id;
	private final Object[] labels;
	private final float[] features;
	private final String[] featureNames;

	public SimpleSample(final int id, final Object[] label, final float[] features, final String[] featureNames) {
		Check.equals(features.length, featureNames.length, "features");
		this.id = id;
		labels = label;
		this.features = features;
		this.featureNames = featureNames;
	}

	@Override
	public final int getId() {
		return id;
	}

	@Override
	public final Object[] getExpected() {
		return labels;
	}

	@Override
	public final Object getExpected(final int labelIndex) {
		return labels == null ? null : labels[labelIndex];
	}

	public void loadFeatures(final FeaturesConsumer feat) {
		for (int i = 0; i < features.length; ++i) {
			feat.add(featureNames[i], features[i]);
		}
	}

	@Override
	public final void writeInfo(final ISampleInfoBuilder builder, final Iterable<? extends Sample> instances) {
		throw new IllegalAccessError();
	}

	@Override
	public final int compareTo(final Sample o) {
		throw new IllegalAccessError();
	}

	@Override
	public final float getWeight(final int labelIndex) {
		return 1;
	}

	@Override
	public String getComment() {
		return null;
	}

}