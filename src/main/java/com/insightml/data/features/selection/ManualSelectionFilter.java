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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.insightml.utils.io.IoUtils;

public final class ManualSelectionFilter extends AbstractFeatureFilter {
	private static final long serialVersionUID = 7510425912816873996L;

	private final boolean isKeep;

	public ManualSelectionFilter(final String file, final boolean isKeep) {
		this(new HashSet<>(Arrays.asList(IoUtils.readFile("/" + file + ".txt").split("[,\\n ]+"))), isKeep);
	}

	public ManualSelectionFilter(final Set<String> features, final boolean isKeep) {
		super(isKeep ? features : new HashSet<String>(), isKeep ? new HashSet<String>() : features);
		this.isKeep = isKeep;
	}

	@Override
	protected boolean removeFeature(final CharSequence feature) {
		return isKeep;
	}

}
