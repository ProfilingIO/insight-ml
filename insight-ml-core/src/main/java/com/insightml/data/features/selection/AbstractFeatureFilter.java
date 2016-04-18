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
package com.insightml.data.features.selection;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.utils.Arrays;
import com.insightml.utils.types.AbstractClass;

public abstract class AbstractFeatureFilter extends AbstractClass implements IFeatureFilter, Cloneable {

	private static final long serialVersionUID = 4088451563427123274L;

	private final Set<String> keepFeatures;
	private final Set<String> ignoreFeatures;
	private final Logger logger = LoggerFactory.getLogger(AbstractFeatureFilter.class);

	public AbstractFeatureFilter(final Set<String> keep, final Set<String> ignore) {
		keepFeatures = keep;
		ignoreFeatures = ignore;
	}

	@Override
	public void ignoreFeature(final String feature) {
		ignoreFeatures.add(feature);
	}

	@Override
	public String[] allowedFeatures(final String[] features) {
		final List<String> filtered = new LinkedList<>();
		final List<String> ignored = new LinkedList<>();
		for (final String feature : features) {
			if (keepFeatures.contains(feature) || !ignoreFeatures.contains(feature) && !removeFeature(feature)) {
				filtered.add(feature);
			} else {
				ignored.add(feature);
			}
		}
		if (!ignored.isEmpty()) {
			logger.debug("Ignored {}", ignored);
		}
		return Arrays.of(filtered, String.class);
	}

	protected abstract boolean removeFeature(CharSequence feature);

	protected final int numIgnored() {
		return ignoreFeatures.size();
	}

	@Override
	public String toString() {
		return "Filter" + keepFeatures + ignoreFeatures;
	}

}
