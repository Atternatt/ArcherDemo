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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.mapper.Mapper

/**
 * Abstract base implementation for a ViewModel.
 *
 * A ViewModel is suposed to handle logic for a specific block of UI and that's the reason why
 * [BaseViewModel] is scoped with one generic item. It'll emit [ViewModelState] wrapping [T] data.
 *
 * If you want or need to have several data objects into your UI then you should create several
 * ViewModels, one for each type.
 *
 * @param mapper a mapper to translate Architecture [Failure] into a presentation [FailureType].
 * A dafault implementation is provided, take a look into [FailureMapper]
 */
abstract class BaseViewModel<T>(mapper: Mapper<Failure, FailureType> = FailureMapper) : ViewModel(),
    Mapper<Failure, FailureType> by mapper {

    private val _state = MutableLiveData<ViewModelState<T>>()

    /**
     * State to be subscribed in the UI.
     */
    val state: LiveData<ViewModelState<T>> = _state

    internal open fun postViewState(viewstate: ViewModelState<T>) {
        _state.value = viewstate
    }

    internal open fun handleFailure(failure: Failure): ViewModelState.Error =
        ViewModelState.Error(map(failure))

    internal abstract fun handleSuccess(data: T): ViewModelState<T>

    /** Helping method to handle the result of a Either and send it through the view state*/
    internal open suspend fun Either<Failure, T>.handleResult() {
        fold(
            ifLeft = { postViewState(handleFailure(it)) },
            ifRight = { postViewState(handleSuccess(it)) })
    }

    /**If we just want to handle the failure*/
    fun Either<Failure, T>.handleFailure() {
        fold(
            ifLeft = { postViewState(handleFailure(it)) },
            ifRight = { })
    }
}

enum class FailureType {
    ServerError,
    ConnectionError,
    DataNotFound,
    UnknownError
}

sealed class ViewModelState<out T> {
    data class Loading(val isLoading: Boolean) : ViewModelState<Nothing>()
    object Empty : ViewModelState<Nothing>()
    data class Success<T>(val data: T) : ViewModelState<T>()
    data class Error(val failureType: FailureType) : ViewModelState<Nothing>()
}