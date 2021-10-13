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
package com.insightml.data.features;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.insightml.data.samples.Sample;

public class DelegatingSimpleFeaturesProvider<S extends Sample> implements SimpleFeaturesProvider<S> {

	private final SimpleFeaturesProvider<? super S>[] providers;

	@SafeVarargs
	public DelegatingSimpleFeaturesProvider(final SimpleFeaturesProvider<? super S>... providers) {
		this.providers = providers;
	}

	@Override
	public String getName() {
		return Arrays.stream(providers).map(p -> p.getName()).collect(Collectors.joining("+"));
	}

	@Override
	public void apply(final S sample, final FeaturesConsumer consumer) {
		for (final SimpleFeaturesProvider<? super S> provider : providers) {
			provider.apply(sample, consumer);
		}
	}
}
