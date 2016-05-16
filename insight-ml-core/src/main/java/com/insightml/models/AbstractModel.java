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

import com.insightml.data.samples.Sample;
import com.insightml.math.types.SumMap;
import com.insightml.utils.types.AbstractConfigurable;
import com.insightml.utils.ui.UiUtils;

public abstract class AbstractModel<I extends Sample, E> extends AbstractConfigurable implements
IModel<I, E> {

    private static final long serialVersionUID = -3450572475700673815L;

    private String[] features;

    public AbstractModel() {
    }

    public AbstractModel(final String[] features) {
        this.features = features;
    }

    @Override
    public final String[] features() {
        return features;
    }

    @Override
    public SumMap<String> featureImportance() {
        return null;
    }

    @Override
    public double logLikelihood(final I sample, final E result) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String info() {
        final SumMap<String> importance = featureImportance();
        if (importance != null) {
            return "\n" + UiUtils.format(importance.distribution(), 0).toString();
        }
        return "No model info for " + getClass().toString();
    }

    @Override
    public Object[] getComponents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }

}
