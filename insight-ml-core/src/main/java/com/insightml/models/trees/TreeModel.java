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
package com.insightml.models.trees;

import java.util.Arrays;

import com.insightml.math.types.SumMap;
import com.insightml.models.AbstractIndependentFeaturesModel;

public final class TreeModel extends AbstractIndependentFeaturesModel {

	private static final long serialVersionUID = -1127329976938652612L;

	private TreeNode root;

	TreeModel() {
	}

	public TreeModel(final TreeNode root, final String[] features) {
		super(features);
		this.root = root;
	}

	@Override
	public double predict(final double[] features) {
		return root.predict(features);
	}

	@Override
	public SumMap<String> featureImportance() {
		return root.featureImportance(true);
	}

	@Override
	public String info() {
		return root.info();
	}

	@Override
	public boolean equals(final Object obj) {
		return Arrays.deepEquals(features(), ((TreeModel) obj).features()) && root.equals(((TreeModel) obj).root);
	}

}
