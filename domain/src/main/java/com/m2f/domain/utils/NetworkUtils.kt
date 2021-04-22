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

package com.m2f.domain.utils

import arrow.core.Either
import com.m2f.arch.data.datasource.DataSource
import com.m2f.arch.data.error.Failure
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Wrapping function that allows to run a block of code and catch the possible error network states.
 *
 * @receiver [DataSource] in order to expose the function only on it's subtype classes.
 * @param block executionBlock that will run the network call.
 *
 * @return Either<Failure, T> where [T] is the return type of the implementation fo the [DataSource]
 */
suspend fun <T> DataSource.tryNetwork(block: suspend () -> Either<Failure, T>): Either<Failure, T> =
    try {
        block()
    } catch (e: Exception) {
        when (e) {
            is ConnectException, is InterruptedIOException, is SocketTimeoutException -> Either.Left(
                Failure.NoConnection
            )
            is HttpException -> {
                when (e.code() / 100) {
                    5 -> Either.Left(Failure.ServerError)
                    else -> Either.Left(Failure.DataNotFound)
                }
            }
            is SecurityException -> Either.Left(Failure.DataNotFound)
            else -> Either.Left(Failure.Unknown(e))
        }
    }
