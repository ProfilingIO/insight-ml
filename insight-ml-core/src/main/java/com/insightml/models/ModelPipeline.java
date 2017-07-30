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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.insightml.data.PreprocessingPipeline;
import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.Samples;
import com.insightml.math.types.SumMap;
import com.insightml.utils.Arrays;
import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractConfigurable;
import com.insightml.utils.types.Parameter;

public class ModelPipeline<I extends Sample, P> extends AbstractConfigurable implements IModelPipeline<I, P> {
	private static final long serialVersionUID = 5471706800666990790L;

	private IModel<I, P> model;
	private PreprocessingPipeline<I> pipe;
	private Function<P, P> postProcessor;
	private int labelIndex;

	ModelPipeline() {
	}

	public ModelPipeline(final IModel<? super I, P> model, final PreprocessingPipeline<I> pipe,
			final Function<P, P> postProcessor, final int labelIndex) {
		this.model = (IModel<I, P>) Preconditions.checkNotNull(model);
		this.pipe = pipe;
		this.postProcessor = postProcessor;
		this.labelIndex = labelIndex;
	}

	public <E> ISamples<I, E> preprocess(final Iterable<? extends I> test) {
		return pipe == null ? new Samples<>((Iterable<I>) test) : (ISamples<I, E>) pipe.run((Iterable<I>) test, false);
	}

	@Override
	@SuppressWarnings("null")
	@Nonnull
	public P[] run(final Iterable<? extends I> test) {
		final P[] output = model.apply(preprocess(test));
		if (postProcessor == null) {
			return output;
		}
		final List<P> result = new LinkedList<>();
		for (final P pred : output) {
			result.add(Preconditions.checkNotNull(postProcessor.apply(pred)));
		}
		return Arrays.of(result);
	}

	public SumMap<String> featureImportance() {
		return model.featureImportance();
	}

	@Override
	public int getLabelIndex() {
		return labelIndex;
	}

	public String info() {
		return model.info();
	}

	@Override
	public void close() {
		model.close();
	}

	public IModel<I, P> getModel() {
		return model;
	}

	@Override
	public boolean equals(final Object obj) {
		final ModelPipeline<?, ?> oth = (ModelPipeline<?, ?>) obj;
		Check.state(labelIndex == oth.labelIndex);
		Check.state(model.equals(oth.model));
		Check.state(pipe.equals(oth.pipe));
		return postProcessor == null && oth.postProcessor == null || postProcessor.equals(oth.postProcessor);
	}

	@Override
	public Object[] getComponents() {
		return new Object[] { model, pipe, postProcessor, new Parameter("label_index", labelIndex) };
	}

}
