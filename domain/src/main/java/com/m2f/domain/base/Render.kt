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

package com.m2f.domain.base

/**
 * A Render class that unwraps a [ViewModelState] in its possible 4 states.
 *
 * It forces to implement [Render.onSucces] & [Render.onError]
 * It provides default empty implementation for [Render.onLoading] & [Render.onEmpty]
 */
interface Render<T> {

    fun render(state: ViewModelState<T>) {
        when (state) {
            is ViewModelState.Success -> {
                onLoading(false); onSucces(state.data)
            }
            ViewModelState.Empty -> {
                onLoading(false); onEmpty()
            }
            is ViewModelState.Error -> {
                onLoading(false); onError(state.failureType)
            }
            is ViewModelState.Loading -> onLoading(state.isLoading)
        }
    }

    fun onSucces(data: T)

    fun onError(failure: FailureType)

    fun onLoading(isLoading: Boolean) {}

    fun onEmpty() {}
}
