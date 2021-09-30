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
import com.m2f.arch.data.datasource.PutDataSource
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.query.Query
import com.m2f.domain.features.posts.data.model.PostEntity
import com.m2f.domain.features.posts.query.PostsQuery
import com.m2f.domain.features.posts.data.model.PostDBO
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

/* fixme -> theres a problem with sqldelight pluguin that makes the build crash so we are not using this datasource for the moment
//we replaced this Datasource for a InMemoryDataSource as a workaround
internal class PostsDatabaseDataSource(private val postDBOQueries: PostDBOQueries) :
    GetDataSource<PostDBO>, PutDataSource<PostDBO> {

    /** a mutual exclusion lock to prevent concurrency problems over reading and writing
     * to the database*/
    private val mutex = Mutex()

    /**
     * A single get() is not implemented so we return a [Failure.QueryNotSupported] for each possible
     * call to this function
     */
    override suspend fun get(query: Query): Either<Failure, PostDBO> =
        Either.Left(Failure.QueryNotSupported)

    /**
     * A Collection get() call to retrieve a list of [PostEntity]
     */
    override suspend fun getAll(query: Query): Either<Failure, List<PostDBO>> = mutex.withLock {
        try {
            when (query) {
                is PostsQuery -> {
                    val result = postDBOQueries.getAllPosts().executeAsList()
                    if (result.isEmpty()) {
                        Either.Left(Failure.DataNotFound)
                    } else {
                        Either.Right(result)
                    }
                }
                else -> Either.Left(Failure.QueryNotSupported)
            }
        } catch (e: Exception) {
            Either.Left(Failure.Unknown(e))
        }
    }

    /**
     * A single put() is not implemented so we return a [Failure.QueryNotSupported] for each possible
     * call to this function
     */
    override suspend fun put(query: Query, value: PostDBO?): Either<Failure, PostDBO> =
        Either.Left(Failure.QueryNotSupported)

    /**
     * Put all the Posts in the database.
     *
     * It will roll back each input if one of the fails.
     */
    override suspend fun putAll(
        query: Query,
        value: List<PostDBO>?
    ): Either<Failure, List<PostDBO>> = mutex.withLock {
        if (value == null) {
            Either.Left(Failure.DataEmpty)
        } else {
            suspendCancellableCoroutine { continuation: CancellableContinuation<Either<Failure, List<PostDBO>>> ->
                postDBOQueries.transaction {
                    afterRollback { continuation.resume(Either.Left(Failure.DataEmpty)) }
                    afterCommit { continuation.resume(Either.Right(value)) }
                    value.forEach { postDBOQueries.insertOrReplacePost(it) }
                }
            }
        }
    }
}*/