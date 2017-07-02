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
import com.insightml.utils.Arrays;

public final class Precision extends AbstractIRFunction<Object, Object> {

	private static final long serialVersionUID = -653970887798242216L;

	public Precision(final boolean macro) {
		super(macro);
	}

	@Override
	protected String name() {
		return "Prec";
	}

	@Override
	public double instance(final Collection<? extends Object> predicted, final Object[] expected, final Sample sample,
			final int labelIndex) {
		if (predicted.size() == 0) {
			return 1;
		}
		int matches = 0;
		for (final Object pred : predicted) {
			if (Arrays.contains(expected, pred)) {
				++matches;
			}
		}
		return matches * 1.0 / predicted.size();
	}

	@Override
	protected double micro(final Collection<? extends Object>[] preds, final List<? extends Object[]> expected) {
		int matches = 0;
		int predicted = 0;
		for (int i = 0; i < preds.length; ++i) {
			for (final Object pred : preds[i]) {
				if (Arrays.contains(expected.get(i), pred)) {
					++matches;
				}
				++predicted;
			}
		}
		return predicted == 0 ? 1 : matches * 1.0 / predicted;
	}

}
