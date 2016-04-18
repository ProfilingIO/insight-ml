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
package com.insightml.data;

import com.google.common.base.Function;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.features.selection.IgnoreFeatureFilter;
import com.insightml.data.samples.ISample;
import com.insightml.utils.Check;

public final class SimpleFeatureConfig<I extends ISample, P> extends FeaturesConfig<I, P> {

    private static final long serialVersionUID = -9027051549538426023L;

    private final IFeatureProvider<I> provider;
    private IFeatureFilter filter;

    public SimpleFeatureConfig(final IFeatureProvider<I> provider,
            final Function<P, P> postProcessor) {
        this(provider, new IgnoreFeatureFilter(), postProcessor);
    }

    public SimpleFeatureConfig(final IFeatureProvider<I> provider, final IFeatureFilter filter,
            final Function<P, P> postProcessor) {
        super(null, postProcessor);
        this.provider = provider;
        this.filter = Check.notNull(filter);
    }

    @Override
    public IFeatureProvider<I> newFeatureProvider(final Iterable<I> training,
            final Iterable<I>... rest) {
        return provider;
    }

    @Override
    public IFeatureFilter newFeatureFilter(final Iterable<I> training,
            final IFeatureProvider<I> prov, final Integer labelIndex) {
        return filter;
    }
}
