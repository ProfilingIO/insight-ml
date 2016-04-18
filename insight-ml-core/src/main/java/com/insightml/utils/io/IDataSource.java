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

public interface IDataSource {

    <T> T find(String query);

    <T> T find(Class<T> objectClass, Object identifier);

    <T> T find(Class<T> objectClass, Map<String, ?> query);

    <T> List<T> findAll(Class<T> objectClass);

    <T> List<T> findAll(Class<T> objectClass, String where);

    <T> List<T> findAll(Class<T> objectClass, String where, int limit);

    <T> List<T> findAll(String query);

}
