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
package com.insightml.evaluation.functions;

import java.io.Serializable;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.math.Maths;
import com.insightml.math.distributions.IDiscreteDistribution;
import com.insightml.utils.Check;
import com.insightml.utils.ui.SimpleFormatter;

public final class Accuracy extends AbstractObjectiveFunctionFrame<Object, Object> {

	private static final long serialVersionUID = -426032174522789426L;

	private final double thresholdTrue;

	public Accuracy(final double thresholdTrue) {
		this.thresholdTrue = Check.num(thresholdTrue, 0.025, 0.9);
	}

	@Override
	public double instance(final Object prediction, final Object label, final Sample sample, final int labelIndex) {
		if (label instanceof String) {
			final String pred = ((IDiscreteDistribution<String>) prediction).getMax().getFirst();
			return label.equals(pred) ? 1.0 : 0.0;
		}
		final double predicted = ((Number) prediction).doubleValue();
		final double actual = label instanceof Boolean ? (Boolean) label ? 1.0 : 0.0 : ((Number) label).doubleValue();
		if (actual >= thresholdTrue) {
			return predicted >= thresholdTrue ? 1 : 0;
		}
		return predicted < thresholdTrue ? 1 : 0;
	}

	@Override
	public String getDescription() {
		return "Percentage of correctly classified samples.";
	}

	abstract static class AbstractConfusion extends AbstractIndependentLabelsObjectiveFunction<Object, Serializable> {

		private static final long serialVersionUID = -653970887798242216L;

		final double thresholdTrue;

		AbstractConfusion(final double thresholdTrue) {
			this.thresholdTrue = Check.num(thresholdTrue, 0.025, 0.9);
		}

		@Override
		public String getName() {
			return getClass().getSimpleName() + "@" + thresholdTrue;
		}

		@Override
		public final DescriptiveStatistics label(final Serializable[] preds, final Object[] expected,
				final double[] weights, final ISamples<?, ?> samples, final int labelIndex) {
			int count = 0;
			for (int i = 0; i < preds.length; ++i) {
				final double[] predAndAct = toDouble(preds[i], expected[i]);
				if (doCount(predAndAct[0], predAndAct[1])) {
					++count;
				}
			}
			return new DescriptiveStatistics(new double[] { count });
		}

		abstract boolean doCount(double pred, double act);
	}

	public static final class TruePositives extends AbstractConfusion {

		private static final long serialVersionUID = -578807203768849452L;

		public TruePositives(final double thresholdTrue) {
			super(thresholdTrue);
		}

		@Override
		boolean doCount(final double pred, final double act) {
			return pred >= thresholdTrue && act >= thresholdTrue;
		}
	}

	public static final class FalsePositives extends AbstractConfusion {

		private static final long serialVersionUID = -653970887798242216L;

		public FalsePositives(final double thresholdTrue) {
			super(thresholdTrue);
		}

		@Override
		boolean doCount(final double pred, final double act) {
			return pred >= thresholdTrue && act < thresholdTrue;
		}
	}

	public static final class TrueNegatives extends AbstractConfusion {

		private static final long serialVersionUID = -653970887798242216L;

		public TrueNegatives(final double thresholdTrue) {
			super(thresholdTrue);
		}

		@Override
		boolean doCount(final double pred, final double act) {
			return pred < thresholdTrue && act < thresholdTrue;
		}
	}

	public static final class FalseNegatives extends AbstractConfusion {

		private static final long serialVersionUID = -653970887798242216L;

		public FalseNegatives(final double thresholdTrue) {
			super(thresholdTrue);
		}

		@Override
		boolean doCount(final double pred, final double act) {
			return pred < thresholdTrue && act >= thresholdTrue;
		}
	}

	public static final class Precision extends AbstractIndependentLabelsObjectiveFunction<Object, Serializable> {

		private static final long serialVersionUID = -653970887798242216L;

		final double thresholdTrue;
		private final boolean evaluatePositiveClass;

		public Precision(final double thresholdTrue) {
			this(thresholdTrue, true);
		}

		public Precision(final double thresholdTrue, final boolean evaluatePositiveClass) {
			this.thresholdTrue = Check.num(thresholdTrue, 0.025, 0.9);
			this.evaluatePositiveClass = evaluatePositiveClass;
		}

		@Override
		public String getName() {
			return "Precision_" + evaluatePositiveClass + "@" + thresholdTrue;
		}

		@Override
		public DescriptiveStatistics label(final Serializable[] preds, final Object[] expected, final double[] weights,
				final ISamples<?, ?> samples, final int labelIndex) {
			int count = 0;
			int correct = 0;
			for (int i = 0; i < preds.length; ++i) {
				final double[] predAndAct = toDouble(preds[i], expected[i]);
				if (evaluatePositiveClass) {
					if (predAndAct[0] >= thresholdTrue) {
						++count;
						if (predAndAct[1] >= thresholdTrue) {
							++correct;
						}
					}
				} else {
					if (predAndAct[0] < thresholdTrue) {
						++count;
						if (predAndAct[1] < thresholdTrue) {
							++correct;
						}
					}
				}
			}
			return new DescriptiveStatistics(new double[] { correct == 0 ? 0 : correct * 1.0 / count });
		}
	}

	public static final class Recall extends AbstractIndependentLabelsObjectiveFunction<Object, Serializable> {

		private static final long serialVersionUID = -653970887798242216L;

		final double thresholdTrue;
		private final boolean evaluatePositiveClass;

		public Recall(final double thresholdTrue) {
			this(thresholdTrue, true);
		}

		public Recall(final double thresholdTrue, final boolean evaluatePositiveClass) {
			this.thresholdTrue = Check.num(thresholdTrue, 0.025, 0.9);
			this.evaluatePositiveClass = evaluatePositiveClass;
		}

		@Override
		public String getName() {
			return "Recall_" + evaluatePositiveClass + "@" + thresholdTrue;
		}

		@Override
		public DescriptiveStatistics label(final Serializable[] preds, final Object[] expected, final double[] weights,
				final ISamples<?, ?> samples, final int labelIndex) {
			int count = 0;
			int correct = 0;
			for (int i = 0; i < preds.length; ++i) {
				final double[] predAndAct = toDouble(preds[i], expected[i]);
				if (evaluatePositiveClass) {
					if (predAndAct[1] >= thresholdTrue) {
						++count;
						if (predAndAct[0] >= thresholdTrue) {
							++correct;
						}
					}
				} else {
					if (predAndAct[1] < thresholdTrue) {
						++count;
						if (predAndAct[0] < thresholdTrue) {
							++correct;
						}
					}
				}
			}
			return new DescriptiveStatistics(new double[] { correct * 1.0 / count });
		}
	}

	public static final class FScore extends AbstractIndependentLabelsObjectiveFunction<Object, Serializable> {

		private static final long serialVersionUID = -653970887798242216L;

		final double thresholdTrue;
		private final double beta;
		private final boolean evaluatePositiveClass;

		public FScore(final double thresholdTrue, final double beta) {
			this(thresholdTrue, beta, true);
		}

		public FScore(final double thresholdTrue, final double beta, final boolean evaluatePositiveClass) {
			this.thresholdTrue = Check.num(thresholdTrue, 0.025, 0.9);
			this.beta = beta;
			this.evaluatePositiveClass = evaluatePositiveClass;
		}

		@Override
		public String getName() {
			return "F" + new SimpleFormatter(2, false).format(beta) + "_" + evaluatePositiveClass + "@" + thresholdTrue;
		}

		@Override
		public DescriptiveStatistics label(final Serializable[] preds, final Object[] expected, final double[] weights,
				final ISamples<?, ?> samples, final int labelIndex) {
			return new DescriptiveStatistics(new double[] { Maths.fScore(
					new Precision(thresholdTrue, evaluatePositiveClass)
							.label(preds, expected, weights, samples, labelIndex).getMean(),
					new Recall(thresholdTrue, evaluatePositiveClass)
							.label(preds, expected, weights, samples, labelIndex).getMean(),
					beta), });
		}
	}
}
