# insight-ml

Java machine learning library.

## Example

```java
final int trees = 30; // number of trees to learn
final int maxDepth = 6; // maximum tree depth
final int minObs = 10; // min. observation in tree nodes
final double isample = 0.9; // sample fraction of data points per tree
final double fsample = 0.8; // sample fraction of features per tree
final VoteStrategy strategy = VoteStrategy.AVERAGE;

final RandomForest learner = new RandomForest(trees, maxDepth, minObs, isample, fsample, strategy);

// create your list of training data points here
final Collection<SimpleSample> trainingData = ...;
final IModel<ISample, Double> model = learner.run(trainingData);

// create your list of testing data points here
final Collection<ISample> test = ...;
final Double[] predictions = model.apply(new Samples<>(test));
```

## License

	Copyright (C) 2016 Stefan Henß
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.