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

import java.io.File;

import com.google.common.collect.ImmutableMap;
import com.insightml.utils.io.CsvWriter;

import org.junit.Test;

public final class CsvWriterTest {

    @Test
    public void test() {
        final CsvWriter writer =
                new CsvWriter(new File("../data/test-ouput.csv"), ',', true, "testA", "testB");
        writer.addLine(ImmutableMap.of("testA", 4, "testB", 2));
        writer.addLine(ImmutableMap.of("testA", 9, "testB", "muh"));
    }

}
