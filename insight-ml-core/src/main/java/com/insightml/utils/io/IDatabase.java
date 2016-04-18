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

import javax.persistence.EntityManager;

public interface IDatabase extends IDataSource {

	int count(String table, String where);

	<T> T persist(T object);

	void insertDetached(Object object);

	void execute(String sql);

	void update(Object object);

	void commit();

	void remove(Object object);

	void removeAll(Class<?> objectClass);

	void flush();

	void close();

	EntityManager createManager();

	EntityManager makeTransaction();

	void commitTransaction(EntityManager manager);

}
