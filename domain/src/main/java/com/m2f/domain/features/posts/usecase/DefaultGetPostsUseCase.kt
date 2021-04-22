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
import arrow.core.computations.either
import arrow.core.handleError
import arrow.fx.coroutines.parTraverse
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.operation.CacheSyncOperation
import com.m2f.arch.data.operation.MainSyncOperation
import com.m2f.arch.data.repository.GetRepository
import com.m2f.arch.data.usecase.ParametrizedUseCase
import com.m2f.domain.features.posts.model.Post
import com.m2f.domain.features.posts.query.PostsQuery
import com.m2f.domain.features.posts.query.SubscribersQuery
import kotlinx.coroutines.CoroutineDispatcher
import java.net.URI

/**
 * Default implementation for [GetPostsUseCase]. It uses [GetNumberOfSubscribersUseCase]
 * to retrieve the subscription counts.
 */
internal class DefaultGetPostsUseCase(
    private val repository: GetRepository<Post>,
    private val getsubsCountUseCase: GetNumberOfSubscribersUseCase,
    coroutineDispatcher: CoroutineDispatcher
) :
    ParametrizedUseCase<PostsQuery, List<Post>>(coroutineDispatcher), GetPostsUseCase {

    /**
     * Retrieves the list of posts and for each of them gets the subscription count for their author.
     *
     * Notice that the notation could be a bit confusing. Here we are running an async/await type of
     * operation imperatively
     *
     * We are combining comprehensions with parallel execution using parTraverse operator over each
     * post to retrieve the number of subscribers.
     *
     * For more information about Comprehensions over coroutines take a look into the documentation:
     * @see [https://arrow-kt.io/docs/patterns/monad_comprehensions/#comprehensions-over-coroutines]
     *
     * For more information about parTravers
     * @see [https://arrow-kt.io/docs/fx/async/#partraverse]
     */
    override suspend fun execute(query: PostsQuery): Either<Failure, List<Post>> {
        val operation = if (query.forceRefresh) MainSyncOperation else CacheSyncOperation
        return either {
            val posts = repository.getAll(query = query, operation = operation).bind()
            posts
                .filter { it.featuredImage != null } //we just want posts with featured images
                .parTraverse { post ->
                    val host: String? = try {
                        URI.create(post.authorUrl).host
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                    if (host.isNullOrBlank()) {
                        post
                    } else {
                        post.copy(
                            numberOfSubscribers = getsubsCountUseCase(
                                SubscribersQuery(host)
                            ).handleError { 0L }.bind()
                        )
                    }
                }
        }
    }
}