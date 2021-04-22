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
import com.m2f.arch.data.operation.CacheSyncOperation
import com.m2f.arch.data.operation.MainSyncOperation
import com.m2f.arch.data.repository.GetRepository
import com.m2f.domain.features.posts.model.Post
import com.m2f.domain.features.posts.query.PostsQuery
import com.m2f.domain.features.posts.query.SubscribersQuery
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Tests cases for [DefaultGetPostsUseCase]
 */
class DefaultGetPostsUseCaseTest {

    private val mockPost = Post(
        id = 0,
        title = "title",
        date = "date",
        excerpt = "exerp",
        url = "url",
        authorAvatarUrl = "https://url",
        authorName = "name",
        authorUrl = "https://authUrl",
        featuredImage = "https://image.png",
        numberOfSubscribers = 0L
    )

    private val mockPostNoFeaturedImage = Post(
        id = 0,
        title = "title",
        date = "date",
        excerpt = "exerp",
        url = "url",
        authorAvatarUrl = "https://url",
        authorName = "name",
        authorUrl = "https://authUrl",
        featuredImage = "https://image.png",
        numberOfSubscribers = 0L
    )

    private val mockPostBadAuthUrl = Post(
        id = 0,
        title = "title",
        date = "date",
        excerpt = "exerp",
        url = "url",
        authorAvatarUrl = "https://url",
        authorName = "name",
        authorUrl = "badAuthUrl",
        featuredImage = "https://image.png",
        numberOfSubscribers = 0L
    )

    private val repository: GetRepository<Post> = mockk()

    private val getSubscribersCountUseCase: GetNumberOfSubscribersUseCase = mockk()

    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined

    private val useCaseTest: DefaultGetPostsUseCase =
        DefaultGetPostsUseCase(repository, getSubscribersCountUseCase, dispatcher)

    @Before
    fun setUp() {
        coEvery {
            repository.getAll(
                ofType(PostsQuery::class),
                any()
            )
        } answers {
            Either.Right(List((arg(0) as PostsQuery).number) {
                (if (it % 2 == 0) mockPost else mockPostNoFeaturedImage).copy(
                    id = it.toLong()
                )
            })
        }

        coEvery { getSubscribersCountUseCase(ofType(SubscribersQuery::class)) } returns Either.Right(
            9L
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun useCase_usesCacheSyncOperationWhenForceRefreshIsFalse() = runBlocking {
        //Given
        val query = PostsQuery(false, 1)

        //When
        useCaseTest(query)

        //Then
        coVerify(exactly = 1) {
            repository.getAll(
                ofType(PostsQuery::class),
                CacheSyncOperation
            )
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun useCase_usesMainSyncOperationWhenForceRefreshIsTrue() = runBlocking {
        //Given
        val query = PostsQuery(true, 1)

        //When
        useCaseTest(query)

        //Then
        coVerify(exactly = 1) {
            repository.getAll(
                ofType(PostsQuery::class),
                MainSyncOperation
            )
        }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun useCase_onlyShowsPostsWithFeaturedImages() = runBlocking {
        //Given
        val query = PostsQuery(true, 10)

        //When
        val result = useCaseTest(query)

        //Then
        assert(result.fold({ false }, { it.all { it.featuredImage != null } }))

    }

    @ExperimentalCoroutinesApi
    @Test
    fun useCase_willSkipCountingSubsIfNoAuthUrlOrBadUrl() = runBlocking {
        //Given
        val query = PostsQuery(false, 1)
        coEvery {
            repository.getAll(
                ofType(PostsQuery::class),
                any()
            )
        } answers { Either.Right(List((arg(0) as PostsQuery).number) { mockPostBadAuthUrl.copy(id = it.toLong()) }) }

        //When
        useCaseTest(query)

        //Then
        coVerify {
            getSubscribersCountUseCase wasNot Called
        }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun useCase_callNTimesSubscriptionCount() = runBlocking {
        //Given
        val query = PostsQuery(false, 1)

        //When
        useCaseTest(query)

        //Then
        coVerify(exactly = query.number) {
            getSubscribersCountUseCase(any())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun useCase_useCaseFailureRecovery() = runBlocking {

        //Given
        val query = PostsQuery(false, 1)
        coEvery { getSubscribersCountUseCase(any()) } returns Either.Left(Failure.DataNotFound)

        //When
        val result = useCaseTest(query)

        //Then
        assertEquals(Either.Right(listOf(mockPost)), result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun useCase_postWithUpdatedSubscriberCount() = runBlocking {

        //Given
        val query = PostsQuery(false, 1)

        //When
        val result = useCaseTest(query)

        //Then
        assert(result.fold({ false }, { it.all { it.numberOfSubscribers == 9L } }))
    }
}