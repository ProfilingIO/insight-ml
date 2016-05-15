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
package com.insightml.utils.pipeline;

import java.util.LinkedList;
import java.util.List;

import com.insightml.utils.Check;
import com.insightml.utils.types.AbstractModule;
import com.insightml.utils.types.collections.PairList;
import com.insightml.utils.ui.UiUtils;
import com.insightml.utils.ui.reports.IReporter;

public class Pipeline<I> extends AbstractModule implements IPipelineElement<I, I>, IReporter {
	private static final long serialVersionUID = -4772589841476090283L;

	private final List<IPipelineElement<? super I, ? extends I>> elements = new LinkedList<>();

	public Pipeline(final IPipelineElement<I, I>[] elements) {
		for (final IPipelineElement<I, I> element : elements) {
			add(element);
		}
	}

	public final <E extends IPipelineElement<? super I, ? extends I>> Pipeline<I> add(final E element) {
		elements.add(Check.notNull(element));
		return this;
	}

	@Override
	public final I run(final I input) {
		Object nextInput = Check.notNull(input);
		for (final IPipelineElement<? super I, ? extends I> element : elements) {
			nextInput = element.run((I) nextInput);
			if (nextInput == null) {
				break;
			}
		}
		return (I) nextInput;
	}

	@Override
	public final String getReport() {
		final PairList<String, String> fields = new PairList<>();
		for (final IPipelineElement<?, ?> element : elements) {
			fields.add(element.getName(), element.getDescription());
		}
		return UiUtils.format(fields);
	}

}
