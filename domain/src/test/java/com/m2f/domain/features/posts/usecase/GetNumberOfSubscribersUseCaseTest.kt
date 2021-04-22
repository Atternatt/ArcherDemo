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
import com.m2f.arch.data.operation.MainOperation
import com.m2f.arch.data.repository.GetRepository
import com.m2f.domain.features.posts.model.SubscriptionCount
import com.m2f.domain.features.posts.query.SubscribersQuery
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

/**
 * Tests cases for [GetNumberOfSubscribersUseCase]
 */
class GetNumberOfSubscribersUseCaseTest {

    private val repository: GetRepository<SubscriptionCount> = mockk()

    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined

    private val useCaseTest: DefaultGetNumberOfSubscribersUseCase =
        DefaultGetNumberOfSubscribersUseCase(repository, dispatcher)

    @Before
    fun setUp() {
        coEvery { repository.get(any(), any()) } returns Either.Right(SubscriptionCount(1))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun useCase_usesMainOperation() = runBlockingTest {
        //When
        useCaseTest(SubscribersQuery(""))

        //Then
        coVerify(exactly = 1) {
            repository.get(
                ofType(SubscribersQuery::class),
                MainOperation
            )
        }
    }
}