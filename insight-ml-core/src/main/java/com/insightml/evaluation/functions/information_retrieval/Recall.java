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
package com.insightml.evaluation.functions.information_retrieval;

import java.util.Collection;
import java.util.List;

import com.insightml.data.samples.Sample;

public final class Recall extends AbstractIRFunction<Object, Object> {

	private static final long serialVersionUID = -653970887798242216L;

	public Recall(final boolean macro) {
		super(macro);
	}

	@Override
	protected String name() {
		return "Rec";
	}

	@Override
	public String getDescription() {
		return "-";
	}

	@Override
	public double instance(final Collection<? extends Object> predicted, final Object[] expected, final Sample sample,
			final int labelIndex) {
		if (expected.length == 0) {
			return 1;
		}
		int matches = 0;
		for (final Object exp : expected) {
			if (predicted.contains(exp)) {
				++matches;
			}
		}
		return matches * 1.0 / expected.length;
	}

	@Override
	protected double micro(final Collection<? extends Object>[] preds, final List<? extends Object[]> expected) {
		int matches = 0;
		int exps = 0;
		for (int i = 0; i < preds.length; ++i) {
			for (final Object exp : (Object[]) expected.get(i)) {
				if (preds[i].contains(exp)) {
					++matches;
				}
				++exps;
			}
		}
		return matches * 1.0 / exps;
	}

}
