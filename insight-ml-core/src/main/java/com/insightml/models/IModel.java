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

import java.io.Serializable;

import com.google.common.base.Function;
import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.math.types.SumMap;

public interface IModel<I extends ISample, E> extends Function<ISamples<I, ?>, E[]>, Serializable {

    String getName();

    String[] features();

    SumMap<String> featureImportance();

    double logLikelihood(I sample, E result);

    String info();

    void close();

}
