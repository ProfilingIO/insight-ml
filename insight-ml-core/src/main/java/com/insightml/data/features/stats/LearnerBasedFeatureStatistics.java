package com.insightml.data.features.stats;

import com.insightml.data.samples.ISamples;
import com.insightml.data.samples.Sample;
import com.insightml.data.samples.decorators.FeaturesFilterDecorator;
import com.insightml.math.types.SumMap;
import com.insightml.models.ILearner;
import com.insightml.models.LearnerInput;

public class LearnerBasedFeatureStatistics extends AbstractIndependentFeatureStatistic {
	private final ILearner<Sample, Double, Double> learner;

	public LearnerBasedFeatureStatistics(final ILearner<Sample, Double, Double> learner) {
		this.learner = learner;
	}

	@Override
	protected double compute(final FeatureStatistics stats, final int feature, final String featureName) {
		final ISamples<Sample, Double> samples = stats.getInstances();
		final boolean[] featuresMask = new boolean[samples.numFeatures()];
		for (int i = 0; i < featuresMask.length; ++i) {
			featuresMask[i] = i == feature;
		}
		final SumMap<String> featureImportance = learner
				.run(new LearnerInput<>(new FeaturesFilterDecorator<>(samples, featuresMask), 0)).featureImportance();
		final Number imp = featureImportance.get(featureName);
		return imp == null ? 0 : imp.doubleValue();
	}

}
