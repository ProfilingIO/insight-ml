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
package com.insightml.math.types;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.insightml.math.statistics.IStats;
import com.insightml.math.statistics.Stats;
import com.insightml.utils.Maps;
import com.insightml.utils.types.AbstractClass;
import com.insightml.utils.types.cache.Cache;

public final class StatsTable<R, C> extends AbstractClass implements Iterable<Entry<R, Cache<C, StatisticalSummary>>> {

	private RowCache<R, C> cache;

	StatsTable() {
	}

	public StatsTable(final int expectedRows) {
		cache = new RowCache<>(expectedRows);
	}

	public void add(final R row, final C column, final double value) {
		((Stats) cache.getOrLoad(row).getOrLoad(column)).add(value);
	}

	public void add(final R row, final C column, final Stats stats) {
		((Stats) cache.getOrLoad(row).getOrLoad(column)).add(stats);
	}

	public StatisticalSummary get(final R row, final C column) {
		final Cache<C, StatisticalSummary> rowMap = cache.get(row);
		return rowMap == null ? null : rowMap.get(column);
	}

	public Cache<C, StatisticalSummary> columns(final R row) {
		return cache.get(row);
	}

	public IStats columnSum(final R row) {
		final Stats base = new Stats();
		final Cache<C, StatisticalSummary> cols = columns(row);
		if (cols == null) {
			return base;
		}
		for (final Entry<C, StatisticalSummary> entry : cols) {
			base.add((Stats) entry.getValue());
		}
		return base;
	}

	public Entry<C, StatisticalSummary> maxSum(final R row) {
		Entry<C, StatisticalSummary> max = null;
		final Cache<C, StatisticalSummary> cols = columns(row);
		if (cols == null) {
			return null;
		}
		for (final Entry<C, StatisticalSummary> entry : cols) {
			if (max == null || entry.getValue().getSum() > max.getValue().getSum()) {
				max = entry;
			}
		}
		return max;
	}

	public <S> Map<R, Map<C, S>> filter(final int minN, final Function<StatisticalSummary, S> summaryMapper) {
		final Map<R, Map<C, S>> map = Maps.create(cache.size());
		for (final Entry<R, Cache<C, StatisticalSummary>> row : cache) {
			Map<C, S> rowMap = map.get(row.getKey());
			for (final Entry<C, StatisticalSummary> entry : row.getValue()) {
				if (entry.getValue().getN() >= minN) {
					if (rowMap == null) {
						rowMap = new LinkedHashMap<>();
						map.put(row.getKey(), rowMap);
					}
					rowMap.put(entry.getKey(), summaryMapper.apply(entry.getValue()));
				}
			}
		}
		return map;
	}

	public Cache<R, Cache<C, StatisticalSummary>> getMap() {
		return cache;
	}

	public int size() {
		return cache.size();
	}

	@Override
	public Iterator<Entry<R, Cache<C, StatisticalSummary>>> iterator() {
		return cache.iterator();
	}

	static final class RowCache<R, C> extends Cache<R, Cache<C, StatisticalSummary>> {

		private static final long serialVersionUID = -4789334058408778182L;

		RowCache() {
			super();
		}

		public RowCache(final int expectedSize) {
			super(expectedSize);
		}

		@Override
		protected Cache<C, StatisticalSummary> load(final R key) {
			return new Cache<C, StatisticalSummary>(32) {

				private static final long serialVersionUID = -595072967976798692L;

				@Override
				protected IStats load(final C keyy) {
					return new Stats();
				}
			};
		}
	}

}
