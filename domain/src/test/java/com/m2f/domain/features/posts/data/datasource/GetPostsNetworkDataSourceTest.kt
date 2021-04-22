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

package com.m2f.domain.features.posts.data.datasource

import arrow.core.Either
import com.m2f.arch.data.error.Failure
import com.m2f.domain.features.posts.data.api.PostsService
import com.m2f.domain.features.posts.query.PostsQuery
import com.m2f.domain.helpers.MockNetworkDispatcher
import com.m2f.domain.helpers.MockNetworkKoDispatcher
import com.m2f.domain.helpers.TimeoutModifier
import com.m2f.domain.helpers.plus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ServerSocketFactory
import kotlin.test.assertEquals

class GetPostsNetworkDataSourceTest {

    private val mockWebServer: MockWebServer = MockWebServer().apply {
        this.serverSocketFactory = ServerSocketFactory.getDefault()
    }

    private lateinit var service: PostsService

    private lateinit var dataSource: GetPostsNetworkDataSource

    private val dispatcher = MockNetworkDispatcher
    private val koDispatcher = MockNetworkKoDispatcher

    @Before
    fun setUp() {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        service =
            Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(PostsService::class.java)

        dataSource = GetPostsNetworkDataSource(service)

        mockWebServer.dispatcher = dispatcher
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun dataSource_mappsValuePropperly() = runBlocking {

        //Given
        val query = PostsQuery(false, 1)

        //When
        val result = dataSource.getAll(query)

        //Then
        assert(result.isRight())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun dataSource_launchTimeoutIfRequestIsTooSlow() = runBlocking {

        //Given
        val query = PostsQuery(false, 1)
        mockWebServer.dispatcher = dispatcher + TimeoutModifier

        //When
        val result = dataSource.getAll(query)

        //Then
        assertEquals(Either.Left(Failure.NoConnection), result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun dataSource_requestingMoreThan100PostsPerCallFails() = runBlocking {

        //Given
        val query = PostsQuery(false, 100)
        mockWebServer.dispatcher = koDispatcher

        //When
        val result = dataSource.getAll(query)

        //Then
        assert(result.isLeft())
    }
}