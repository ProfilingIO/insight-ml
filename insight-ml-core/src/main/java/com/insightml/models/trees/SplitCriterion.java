package com.insightml.models.trees;

import com.insightml.math.statistics.Stats;

public interface SplitCriterion {

	double improvement(Stats sumL, int featureIndex, int lastIndexLeft);

}
