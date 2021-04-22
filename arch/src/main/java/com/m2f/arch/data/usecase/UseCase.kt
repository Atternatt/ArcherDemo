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

package com.m2f.arch.data.usecase

import arrow.core.Either
import com.m2f.arch.data.error.Failure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<R>(private val coroutineDispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(): Either<Failure, R> = withContext(coroutineDispatcher) {
        execute()
    }

    abstract suspend fun execute(): Either<Failure, R>
}