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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.Sample;
import com.insightml.data.samples.Samples;
import com.insightml.models.LearnerArguments.Argument;
import com.insightml.utils.Check;
import com.insightml.utils.IArguments;
import com.insightml.utils.types.AbstractModule;
import com.insightml.utils.ui.UiUtils;

public abstract class AbstractLearner<S extends Sample, E, O> extends AbstractModule implements ILearner<S, E, O> {

	public static final Logger logger = LoggerFactory.getLogger(AbstractLearner.class);

	private final IArguments arguments;
	private final LearnerArguments args;

	public AbstractLearner(final IArguments arguments) {
		this.arguments = Check.notNull(arguments);
		this.args = arguments();
	}

	@Override
	public LearnerArguments arguments() {
		return null;
	}

	@Override
	public IArguments getOriginalArguments() {
		return arguments;
	}

	protected final double argument(final String arg) {
		final Argument ar = Check.notNull(args, "No arguments specified.").get(arg);
		final Double def = ar.getDefault();
		return ar.validate(def == null ? arguments.toDouble(arg) : arguments.toDouble(arg, def));
	}

	@Override
	public String getName() {
		if (args == null) {
			return getClass().getSimpleName();
		}
		final StringBuilder builder = new StringBuilder();
		for (final Argument arg : args) {
			final double value = argument(arg.getName());
			final Double def = arg.getDefault();
			if (def != null && value == def.doubleValue()) {
				continue;
			}
			builder.append(", ");
			builder.append(arg.getName() + "=" + UiUtils.format(value));
		}
		return getClass().getSimpleName() + (builder.length() > 2 ? "{" + builder.substring(2) + "}" : "");
	}

	public final IModel<S, O> run(final Collection<? extends S> trainingData) {
		return run(new Samples<S, E>((Iterable<S>) trainingData), 0);
	}

}
