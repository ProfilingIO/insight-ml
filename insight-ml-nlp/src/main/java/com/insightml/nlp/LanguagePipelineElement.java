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
package com.insightml.nlp;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.ITextSample;
import com.insightml.utils.pipeline.IPipelineElement;
import com.insightml.utils.types.AbstractModule;
import com.insightml.utils.ui.reports.IUiProvider;

public abstract class LanguagePipelineElement extends AbstractModule
		implements IPipelineElement<ISentence, ISentence>, IUiProvider<ISamples<ITextSample, ?>> {

	private static final long serialVersionUID = 4998121755945568944L;

	@Override
	public String getText(final ISamples<ITextSample, ?> instances, final int labelIndex) {
		throw new IllegalAccessError();
	}

}
