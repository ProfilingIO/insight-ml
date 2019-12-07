package com.insightml.data.features;

import com.insightml.data.samples.Sample;

public class DelegatingSimpleFeaturesProvider<S extends Sample> implements SimpleFeaturesProvider<S> {

	private final SimpleFeaturesProvider<? super S>[] providers;

	@SafeVarargs
	public DelegatingSimpleFeaturesProvider(final SimpleFeaturesProvider<? super S>... providers) {
		this.providers = providers;
	}

	@Override
	public void apply(final S sample, final FeaturesConsumer consumer) {
		for (final SimpleFeaturesProvider<? super S> provider : providers) {
			provider.apply(sample, consumer);
		}
	}
}
