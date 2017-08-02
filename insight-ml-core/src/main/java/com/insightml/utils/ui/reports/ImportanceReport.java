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
package com.insightml.utils.ui.reports;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.LoggerFactory;

import com.insightml.math.types.SumMap;
import com.insightml.models.ModelPipeline;
import com.insightml.utils.Collections;
import com.insightml.utils.Collections.SortOrder;
import com.insightml.utils.io.IoUtils;
import com.insightml.utils.ui.LaTeX;

public final class ImportanceReport {

	private ImportanceReport() {
	}

	public static void writeLatex(final ModelPipeline<?, ?> model, final String dataset, final String folder) {
		final SumMap<String> imp = model.featureImportance();
		if (model.featureImportance() == null) {
			LoggerFactory.getLogger(ImportanceReport.class).info("No feature importance for " + model.getName());
			return;
		}
		new File(folder).mkdirs();
		final List<Object[]> cells = new LinkedList<>();
		for (final Entry<String, Double> feat : Collections.sort(imp.distribution().getMap(), SortOrder.DESCENDING)
				.entrySet()) {
			cells.add(new Object[] { feat.getKey(), feat.getValue() });
		}
		IoUtils.write(
				LaTeX.table("Feature Importance "
						+ dataset, new String[] { "Feature", "Importance", }, cells, false, 5.0, 10),
				new File(folder + (dataset + "-" + model.getName()).replaceAll("[}{, =]+", "") + ".tex"));
	}

}
