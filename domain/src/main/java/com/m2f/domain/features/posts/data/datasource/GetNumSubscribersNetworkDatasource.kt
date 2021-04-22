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
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.query.Query
import com.m2f.domain.features.posts.data.api.PostsService
import com.m2f.domain.features.posts.data.model.SubscriptionCountEntity
import com.m2f.domain.features.posts.query.SubscribersQuery
import com.m2f.domain.utils.tryNetwork

/**
 * [GetDataSource] that will be used to retrieve the number of subscribers of the post's author.
 */
internal class GetNumSubscribersNetworkDatasource(private val postService: PostsService) :
    GetDataSource<SubscriptionCountEntity> {

    override suspend fun get(query: Query): Either<Failure, SubscriptionCountEntity> = tryNetwork {
        when (query) {
            is SubscribersQuery -> Either.Right(postService.getSubscribers(query.url))
            else -> Either.Left(Failure.QueryNotSupported)
        }
    }

    override suspend fun getAll(query: Query): Either<Failure, List<SubscriptionCountEntity>> =
        Either.Left(Failure.QueryNotSupported)
}