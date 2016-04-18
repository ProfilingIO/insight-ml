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
package com.insightml.utils;

public interface IArguments {

    boolean bool(String key);

    boolean bool(String key, boolean def);

    int toInt(String key);

    Integer toInt(String key, Integer def);

    double toDouble(String key);

    Double toDouble(String key, Double def);

    String toString(String key);

    String toString(String key, String def);

    boolean containsKey(String key);

    <T> T get(String key);

}
