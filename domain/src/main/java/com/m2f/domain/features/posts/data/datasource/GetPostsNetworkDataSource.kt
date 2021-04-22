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
import com.m2f.domain.features.posts.data.model.PostEntity
import com.m2f.domain.features.posts.query.PostsQuery
import com.m2f.domain.utils.tryNetwork

/**
 * [GetDataSource] implementation for type [PostEntity]
 */
internal class GetPostsNetworkDataSource(private val service: PostsService) :
    GetDataSource<PostEntity> {

    /**
     * A single get() is not implemented so we return a [Failure.QueryNotSupported] for each possible
     * call to this function
     */
    override suspend fun get(query: Query): Either<Failure, PostEntity> =
        Either.Left(Failure.QueryNotSupported)

    /**
     * A Collection get() call to retrieve a list of [PostEntity]
     */
    override suspend fun getAll(query: Query): Either<Failure, List<PostEntity>> = tryNetwork {
        when (query) {
            is PostsQuery -> {
                Either.Right(service.getPosts(query.number).posts)
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }

    }
}