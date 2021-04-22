/*
 * Copyright (c) 2021.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.m2f.domain.helpers

import okio.buffer
import okio.source
import java.nio.charset.StandardCharsets

fun <T : Any> T.readContentFromFilePath(fileName: String): String {
    val inputStream = javaClass.classLoader!!.getResourceAsStream("service-response/$fileName")
    val source = inputStream.source()
    val buffer = source.buffer()
    return buffer.readString(StandardCharsets.UTF_8)
}