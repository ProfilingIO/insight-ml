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
package com.insightml.models;

import java.lang.reflect.Array;

import com.google.common.base.Preconditions;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.utils.Arrays;
import com.insightml.utils.jobs.ParallelFor;

public abstract class AbstractIndependentModel<I extends Sample, E> extends AbstractModel<I, E> {

	private static final long serialVersionUID = -4539226467144740757L;

	protected AbstractIndependentModel() {
	}

	public AbstractIndependentModel(final String[] features) {
		super(features);
	}

	@Override
	public final E[] apply(final ISamples<? extends I, ?> instances) {
		final int[] featuresFilter = constractFeaturesFilter(instances);
		if (instances.size() < 10) {
			final E first = predict(0, instances, featuresFilter);
			final E[] result = (E[]) Array.newInstance(first.getClass(), instances.size());
			result[0] = first;
			for (int i = 1; i < result.length; ++i) {
				result[i] = predict(i, instances, featuresFilter);
			}
			return result;
		}
		return Arrays.of(ParallelFor
				.run(i -> Preconditions.checkNotNull(predict(i, instances, featuresFilter)), 0, instances.size(), 1));
	}

	protected int[] constractFeaturesFilter(final ISamples<? extends I, ?> instances) {
		final CharSequence[] ref = features();
		final String[] names = instances.featureNames();
		final int[] featuresFilter = new int[ref == null ? names.length : ref.length];

		if (ref == null) {
			for (int i = 0; i < names.length; ++i) {
				featuresFilter[i] = i;
			}
		} else {
			for (int i = 0; i < ref.length; ++i) {
				int idx = -1;
				for (int j = 0; j < names.length; ++j) {
					if (ref[i].equals(names[j])) {
						idx = j;
						break;
					}
				}
				featuresFilter[i] = idx;
			}
		}
		return featuresFilter;
	}

	protected abstract E predict(final int instance, final ISamples<? extends I, ?> instances, int[] featuresFilter);

}
