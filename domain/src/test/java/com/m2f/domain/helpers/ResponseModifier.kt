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

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy

/**
 * Interface used to modify a response and return it with their modifiers.
 */
fun interface ResponseModifier {
    infix fun modify(response: MockResponse): MockResponse
}

/**
 * This modifier bocks the response enfocing a Timeout
 */
object TimeoutModifier : ResponseModifier {
    override fun modify(response: MockResponse): MockResponse {
        return response.setSocketPolicy(SocketPolicy.NO_RESPONSE)
    }
}