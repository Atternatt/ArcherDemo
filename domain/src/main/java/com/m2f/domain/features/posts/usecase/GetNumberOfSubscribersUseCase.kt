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
import com.m2f.domain.features.posts.query.SubscribersQuery

/**
 * Use Case: Retrieve the number of subscribers of an author, given by [SubscribersQuery]
 *
 * Note that this interface is sealed and can not be implemented outside its package
 */
sealed interface GetNumberOfSubscribersUseCase {

    suspend operator fun invoke(query: SubscribersQuery): Either<Failure, Long>
}
