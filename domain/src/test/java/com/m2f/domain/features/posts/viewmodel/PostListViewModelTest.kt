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

package com.m2f.domain.features.posts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import arrow.core.Either
import com.m2f.arch.data.repository.GetRepository
import com.m2f.domain.base.ViewModelState
import com.m2f.domain.features.posts.model.Post
import com.m2f.domain.features.posts.query.PostsQuery
import com.m2f.domain.features.posts.query.SubscribersQuery
import com.m2f.domain.features.posts.usecase.DefaultGetPostsUseCase
import com.m2f.domain.features.posts.usecase.GetNumberOfSubscribersUseCase
import io.mockk.Called
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostListViewModelTest {

    private val mockPost = Post(
        id = 0,
        title = "title",
        date = "date",
        excerpt = "exerp",
        url = "https://url",
        authorAvatarUrl = "https://url",
        authorName = "name",
        authorUrl = "http://authUrl",
        featuredImage = "https://image.png",
        numberOfSubscribers = 0L
    )

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var viewModelTest: PostListViewModel

    private val observer = mockk<Observer<ViewModelState<List<Post>>>>()

    private val repository: GetRepository<Post> = mockk()

    private val getSubscribersCountUseCase: GetNumberOfSubscribersUseCase = mockk()

    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined

    private val getPostsUseCase: DefaultGetPostsUseCase =
        DefaultGetPostsUseCase(repository, getSubscribersCountUseCase, dispatcher)

    @Before
    fun setUp() {
        coEvery {
            repository.getAll(
                ofType(PostsQuery::class),
                any()
            )
        } answers { Either.Right(List((arg(0) as PostsQuery).number) { mockPost.copy(id = it.toLong()) }) }

        coEvery { getSubscribersCountUseCase(ofType(SubscribersQuery::class)) } returns Either.Right(
            9L
        )
        every { observer.onChanged(any()) } just Runs
        viewModelTest = PostListViewModel(getPostsUseCase)
        viewModelTest.state.observeForever(observer)
    }

    @After
    fun tearDown() {
        viewModelTest.state.removeObserver(observer)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun viewModel_wontLoadPostsOnInit() = runBlockingTest {
        coVerify(exactly = 1) {
            observer wasNot Called
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun viewModel_callingLoadItemsWillEmitCorrectValue() = runBlockingTest {
        //When
        viewModelTest.loadPosts()

        //Then
        coVerifySequence {
            observer.onChanged(nrefEq(ViewModelState.Loading(true)))
            observer.onChanged(nrefEq(ViewModelState.Success(listOf(mockPost))))
        }
    }
}