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
package com.insightml.models.general;

import com.google.common.base.MoreObjects;
import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.models.AbstractModel;
import com.insightml.utils.Arrays;

public final class ConstantModel<E> extends AbstractModel<ISample, E> {
	private static final long serialVersionUID = 3872171524371116676L;

	private E constant;

	ConstantModel() {
	}

	public ConstantModel(final E constant) {
		super(null);
		this.constant = constant;
	}

	@Override
	public E[] apply(final ISamples<ISample, ?> input) {
		return Arrays.fill(input.size(), constant);
	}

	@Override
	public boolean equals(final Object obj) {
		return constant.equals(((ConstantModel<?>) obj).constant);
	}

	@Override
	public String getName() {
		return MoreObjects.toStringHelper(this).addValue(constant).toString();
	}
}