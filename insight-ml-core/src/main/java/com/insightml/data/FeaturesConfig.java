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

import java.io.Serializable;

import com.google.common.base.Function;
import com.insightml.data.features.IFeatureProvider;
import com.insightml.data.features.selection.IFeatureFilter;
import com.insightml.data.samples.ISample;
import com.insightml.math.Normalization;
import com.insightml.utils.types.AbstractModule;

public abstract class FeaturesConfig<I extends ISample, P> extends AbstractModule implements
Serializable {

    private static final long serialVersionUID = -5411101736646501986L;

    private final Normalization normalization;
    private final Function<P, P> postProcessor;

    public FeaturesConfig(final Normalization normalization, final Function<P, P> postProcessor) {
        this.normalization = normalization;
        this.postProcessor = postProcessor;
    }

    public abstract IFeatureProvider<I> newFeatureProvider(final Iterable<I> training,
            final Iterable<I>... rest);

    public abstract IFeatureFilter newFeatureFilter(final Iterable<I> training,
            final IFeatureProvider<I> provider, final Integer labelIndex);

    public final Normalization getNormalization() {
        return normalization;
    }

    public final Function<P, P> getPostProcessor() {
        return postProcessor;
    }

}
