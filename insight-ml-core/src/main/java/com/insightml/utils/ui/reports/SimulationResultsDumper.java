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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightml.data.samples.ISample;
import com.insightml.data.samples.ISamples;
import com.insightml.evaluation.functions.IObjectiveFunction;
import com.insightml.evaluation.simulation.SimulationResult;
import com.insightml.models.Predictions;
import com.insightml.utils.Filter;
import com.insightml.utils.Maps;
import com.insightml.utils.io.CsvWriter;
import com.insightml.utils.io.IDatabase;
import com.insightml.utils.io.IoUtils;
import com.insightml.utils.types.Triple;
import com.insightml.utils.types.collections.TripleList;
import com.insightml.utils.ui.LaTeX;
import com.insightml.utils.ui.SimpleFormatter;
import com.insightml.utils.ui.UiUtils;

public final class SimulationResultsDumper {

	private static Logger logger = LoggerFactory.getLogger(SimulationResultsDumper.class);

	private SimulationResultsDumper() {
	}

	public static void createReports(final String datasetName, final IDatabase db) {
		final List<SimulationResult> results = db.findAll(SimulationResult.class, "e._data LIKE '" + datasetName
				+ "%' ORDER BY e.mean DESC");
		final String[] columns = columns(results, new String[] { "MA-R-Prec", "MAP@5", "MAP@7", "MAP@8", "MAP@14",
				"MA-New", });
		final String file = "reports/simulations/" + datasetName.replace(' ', '_');
		new File("reports/simulations/").mkdirs();
		createCsvReport(results, columns, file);
		createLatexReport(datasetName, results, columns, file);
	}

	private static String[] columns(final List<SimulationResult> results, final String[] filter) {
		final Set<String> allMetrics = new LinkedHashSet<>();
		for (final SimulationResult result : results) {
			allMetrics.addAll(result.getMetrics().keySet());
		}
		final String[] metrics = Filter.filter(allMetrics, filter).toArray(new String[0]);
		Arrays.sort(metrics);
		final String[] columns = new String[metrics.length + 1];
		columns[0] = "Model";
		for (int i = 0; i < metrics.length; ++i) {
			columns[i + 1] = metrics[i];
		}
		return columns;
	}

	private static void createCsvReport(final List<SimulationResult> results, final String[] columns, final String file) {
		final CsvWriter writer = new CsvWriter(new File(file + ".csv"), ';', true, columns);
		for (final SimulationResult row : results) {
			final Map<CharSequence, Object> ro = Maps.create(16);
			ro.put("Model", row.getModel());
			for (final Entry<String, Float> metric : row.getMetrics().entrySet()) {
				ro.put(metric.getKey(), metric.getValue());
			}
			writer.addLine(ro);
		}
		writer.close();
	}

	private static void createLatexReport(final String datasetName, final List<SimulationResult> results,
			final String[] columns, final String file) {
		final Map<String, Float> max = getMax(results, columns);
		final List<Object[]> cells = new LinkedList<>();
		for (final SimulationResult result : results) {
			final Map<String, Float> metrics = result.getMetrics();
			if (metrics == null) {
				continue;
			}
			final Object[] cols = new Object[columns.length];
			cols[0] = result.getModel().replace("TopMA-F1", "F1").replace("[]", "");
			for (int j = 1; j < columns.length; ++j) {
				final Float value = metrics.get(columns[j]);
				cols[j] = (value != null && Math.abs(value - max.get(columns[j])) < 0.01 ? "\\bf " : "")
						+ (value == null ? "-" : new SimpleFormatter(j == columns.length - 1 ? 1 : 4, true)
								.format(value));
			}
			cells.add(cols);
		}
		IoUtils.write(LaTeX.table("Simulation Results for " + datasetName, columns, cells, true, 5.5, 10), new File(
				file + ".tex"));
	}

	private static Map<String, Float> getMax(final List<SimulationResult> results, final String[] columns) {
		final Map<String, Float> max = new HashMap<>();
		for (final String column : columns) {
			max.put(column, Float.NEGATIVE_INFINITY);
		}
		for (final SimulationResult result : results) {
			if (result.getModel().startsWith("All") || result.getModel().startsWith("InText")) {
				continue;
			}
			final Map<String, Float> metrics = result.getMetrics();
			if (metrics == null) {
				continue;
			}
			for (int j = 1; j < columns.length; ++j) {
				final Float value = metrics.get(columns[j]);
				if (value != null && max.get(columns[j]) < value) {
					max.put(columns[j], (float) value);
				}
			}
		}
		return max;
	}

	public static <E, P> void dump(final String filename, final Predictions<E, P>[][] preds,
			final IObjectiveFunction<? super E, ? super P> objective) {
		final TripleList<ISample, P, Double> predictions = new TripleList<>();
		for (final Predictions<E, P>[] run : preds) {
			for (int label = 0; label < run.length; ++label) {
				final ISamples<? extends ISample, E> samples = run[label].getSamples();
				if (samples == null) {
					logger.info("Not dumping results for label " + label);
					continue;
				}
				for (int i = 0; i < run[label].size(); ++i) {
					final ISample instance = samples.get(i);
					final E exp = (E) instance.getExpected(label);
					if (exp == null) {
						continue;
					}
					final P pred = run[label].getPredictions()[i];
					try {
						predictions.add(instance, pred, objective.instance(pred, exp, run[label].getSample(i)));
					} catch (final IllegalAccessError e) {
						predictions.add(instance, pred, null);
					}
				}
			}
		}
		write(filename, predictions);
	}

	private static <P> void write(final String filename, final TripleList<ISample, P, Double> predictions) {
		final String[] header = new String[5];
		header[0] = "id";
		header[1] = "error";
		header[2] = "comment";
		header[3] = "predicted";
		header[4] = "actual";
		final CsvWriter writer = new CsvWriter(new File(filename), ';', true, header);
		final List<Triple<ISample, P, Double>> sorted = predictions.toList();
		Collections.sort(sorted, (o1, o2) -> o2.getThird().compareTo(o1.getThird()));
		final SimpleFormatter formatter = new SimpleFormatter(5, true);
		for (final Triple<ISample, P, Double> prediction : sorted) {
			final Map<CharSequence, Object> map = Maps.create(5);
			map.put("id", prediction.getFirst().getId());
			map.put("error", prediction.getThird() == null ? null : formatter.format(prediction.getThird()));
			map.put("comment", prediction.getFirst().getComment());
			map.put("predicted", UiUtils.format(prediction.getSecond()));
			map.put("actual", prediction.getFirst().getExpected(0));
			writer.addLine(map);
		}
		writer.close();
	}
}
