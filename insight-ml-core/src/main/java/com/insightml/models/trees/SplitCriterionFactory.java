package com.insightml.models.trees;

public interface SplitCriterionFactory {

	SplitCriterion create(final SplitFinderContext context, final boolean[] subset);
}
