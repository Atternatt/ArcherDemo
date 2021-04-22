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

package com.m2f.domain.features.posts.usecase

import arrow.core.Either
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.operation.MainOperation
import com.m2f.arch.data.repository.GetRepository
import com.m2f.arch.data.usecase.ParametrizedUseCase
import com.m2f.domain.features.posts.model.SubscriptionCount
import com.m2f.domain.features.posts.query.SubscribersQuery
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Default implementation of [GetNumberOfSubscribersUseCase]
 */
internal class DefaultGetNumberOfSubscribersUseCase(
    private val getRepository: GetRepository<SubscriptionCount>,
    coroutineDispatcher: CoroutineDispatcher
) : ParametrizedUseCase<SubscribersQuery, Long>(coroutineDispatcher),
    GetNumberOfSubscribersUseCase {

    override suspend fun execute(query: SubscribersQuery): Either<Failure, Long> {
        return getRepository.get(query = query, operation = MainOperation)
            .map { it.count }
    }
}