/**
 * Copyright (c) 2011-2013 Stefan Henss.
 *
 * @author stefan.henss@gmail.com
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

	public Pipeline(final IPipelineElement<I, I>... elements) {
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
