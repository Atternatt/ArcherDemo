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

package com.m2f.domain.features.posts.di

import com.m2f.arch.data.datasource.DataSourceMapper
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.datasource.VoidDeleteDataSource
import com.m2f.arch.data.datasource.VoidPutDataSource
import com.m2f.arch.data.datasource.memory.InMemoryDataSource
import com.m2f.arch.data.datasource.plus
import com.m2f.arch.data.datasource.toGetRepository
import com.m2f.arch.data.repository.CacheRepository
import com.m2f.arch.data.repository.GetRepository
import com.m2f.domain.features.posts.data.api.PostsService
import com.m2f.domain.features.posts.data.datasource.GetNumSubscribersNetworkDatasource
import com.m2f.domain.features.posts.data.datasource.GetPostsNetworkDataSource
import com.m2f.domain.features.posts.data.model.PostEntity
import com.m2f.domain.features.posts.mapper.PostDboToPostMapper
import com.m2f.domain.features.posts.mapper.PostEntityToPostMapper
import com.m2f.domain.features.posts.mapper.PostToPostDboMapper
import com.m2f.domain.features.posts.mapper.SubscriptionCountEntityToSubscriptionCountMapper
import com.m2f.domain.features.posts.model.Post
import com.m2f.domain.features.posts.model.SubscriptionCount
import com.m2f.domain.features.posts.usecase.DefaultGetNumberOfSubscribersUseCase
import com.m2f.domain.features.posts.usecase.DefaultGetPostsUseCase
import com.m2f.domain.features.posts.usecase.GetNumberOfSubscribersUseCase
import com.m2f.domain.features.posts.usecase.GetPostsUseCase
import com.m2f.domain.features.posts.data.model.PostDBO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Module that provides the dependencies related with posts.
 *
 * It'll provide all the Use Case Objects related with Posts.
 */
@Module
@InstallIn(SingletonComponent::class)
object PostsModule {

    @Provides
    @Singleton
    fun providesPostRepository(
        retrofit: Retrofit
    ): CacheRepository<Post> {
        val postsService = retrofit.create(PostsService::class.java)

        val networkDatasource: GetDataSource<PostEntity> = GetPostsNetworkDataSource(postsService)
        val databaseDatasource = InMemoryDataSource<PostDBO>()

        val networkDataSourceMappaer: GetDataSource<Post> =
            networkDatasource + PostEntityToPostMapper

        val databaseDataSourceMapper = DataSourceMapper<PostDBO, Post>(
            getDataSource = databaseDatasource,
            putDataSource = databaseDatasource,
            deleteDataSource = VoidDeleteDataSource(),
            toOutMapper = PostDboToPostMapper,
            toInMapper = PostToPostDboMapper
        )

        return CacheRepository(
            getMain = networkDataSourceMappaer,
            putMain = VoidPutDataSource(),
            deleteMain = VoidDeleteDataSource(),
            getCache = databaseDataSourceMapper,
            putCache = databaseDataSourceMapper,
            deleteCache = VoidDeleteDataSource()
        )
    }

    @Provides
    @Singleton
    fun providesUseCase(
        retrofit: Retrofit,
        coroutineDispatcher: CoroutineDispatcher
    ): GetNumberOfSubscribersUseCase {
        val postsService = retrofit.create(PostsService::class.java)
        val repository: GetRepository<SubscriptionCount> =
            GetNumSubscribersNetworkDatasource(postsService)
                .toGetRepository(SubscriptionCountEntityToSubscriptionCountMapper)

        return DefaultGetNumberOfSubscribersUseCase(repository, coroutineDispatcher)
    }

    @Provides
    @Singleton
    fun providesGetPostsUseCase(
        repository: CacheRepository<Post>,
        subsUseCase: GetNumberOfSubscribersUseCase,
        coroutineDispatcher: CoroutineDispatcher
    ): GetPostsUseCase =
        DefaultGetPostsUseCase(
            repository = repository,
            getsubsCountUseCase = subsUseCase,
            coroutineDispatcher = coroutineDispatcher
        )
}