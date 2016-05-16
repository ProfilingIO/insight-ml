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

import com.insightml.data.samples.Sample;
import com.insightml.models.Features;
import com.insightml.utils.ui.reports.IReporter;

public interface IFeatureProvider<I extends Sample> extends IReporter {

	String getName();

	String[] featureNames(Iterable<I> samples);

	double[] features(I sample, CharSequence[] features, boolean isTraining);

	Features features(I sample, boolean isTraining);

}