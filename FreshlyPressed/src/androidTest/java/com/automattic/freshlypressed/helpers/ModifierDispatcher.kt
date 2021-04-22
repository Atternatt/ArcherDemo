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

package com.automattic.freshlypressed.helpers

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

/**
 * A [ModifierDispatcher] is Proxy object that allow response modification.
 */
class ModifierDispatcher(
    private val dispatcher: Dispatcher,
    private val modifier: ResponseModifier
) : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return modifier modify dispatcher.dispatch(request)
    }
}

/**
 * Extension function that allow [ResponseModifier] concatenation.
 */
operator fun Dispatcher.plus(modifier: ResponseModifier): Dispatcher {
    return ModifierDispatcher(this, modifier)
}