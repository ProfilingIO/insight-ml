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
package com.insightml.utils.io;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

public class JPAUtils {

	public static <T> List<T> findAll(final Class<T> objectClass, final EntityManager manager) {
		return findAll("select e from " + objectClass.getSimpleName() + " e", manager);
	}

	public static <T> List<T> findAll(final String query, final EntityManager manager) {
		return manager.createQuery(query).getResultList();
	}

	public static <T> List<T> findAll(final Class<T> objectClass, final String where, final EntityManager manager) {
		return findAll("select e from " + objectClass.getSimpleName() + " e where " + where, manager);
	}

	public static <T> List<T> findAll(final Class<T> objectClass, final String where, final int limit,
			final EntityManager manager) {
		return findAll("from " + objectClass.getSimpleName() + " e where " + where, limit, manager);
	}

	public static <T> List<T> findAll(final String query, final int limit, final EntityManager manager) {
		return manager.createQuery(query).setMaxResults(limit).getResultList();
	}

	public static <T> T find(final Class<T> objectClass, final Map<String, ?> query, final EntityManager manager) {
		final StringBuilder q = new StringBuilder(128);
		q.append("FROM ");
		q.append(objectClass.getSimpleName());
		q.append(" e WHERE ");
		for (final Entry<String, ?> entry : query.entrySet()) {
			q.append("e.");
			q.append(entry.getKey());
			if (entry.getValue() == null) {
				q.append(" IS NULL");
			} else {
				q.append("=");
				q.append(entry.getValue() instanceof Boolean ? (Boolean) entry.getValue() ? 1 : 0 : "'"
						+ entry.getValue() + "'");
			}
			q.append(" AND ");
		}
		return (T) manager.createQuery(q.substring(0, q.length() - 5)).setMaxResults(1).getSingleResult();
	}

}
