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

package com.m2f.arch.data.repository

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.handleErrorWith
import com.m2f.arch.data.datasource.DeleteDataSource
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.datasource.PutDataSource
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.operation.CacheOperation
import com.m2f.arch.data.operation.CacheSyncOperation
import com.m2f.arch.data.operation.DefaultOperation
import com.m2f.arch.data.operation.MainOperation
import com.m2f.arch.data.operation.MainSyncOperation
import com.m2f.arch.data.operation.Operation
import com.m2f.arch.data.query.Query

class CacheRepository<V>(
    private val getCache: GetDataSource<V>,
    private val putCache: PutDataSource<V>,
    private val deleteCache: DeleteDataSource,
    private val getMain: GetDataSource<V>,
    private val putMain: PutDataSource<V>,
    private val deleteMain: DeleteDataSource
) : GetRepository<V>, PutRepository<V>, DeleteRepository {

    override suspend fun get(query: Query, operation: Operation): Either<Failure, V> {
        return when (operation) {
            is DefaultOperation -> get(query, CacheSyncOperation)
            is MainOperation -> getMain.get(query)
            is CacheOperation -> getCache.get(query)
            is MainSyncOperation -> getMain.get(query)
                .flatMap { putCache.put(query, it) }
                .handleErrorWith { failure ->
                    when (failure) {
                        is Failure.NoConnection, is Failure.ServerError -> {
                            get(query, CacheOperation)
                                .mapLeft { failure }
                        }
                        else -> Either.Left(failure)
                    }
                }
            is CacheSyncOperation -> {
                return getCache.get(query).handleErrorWith {
                    when (it) {
                        is Failure.DataNotFound -> get(query, MainSyncOperation)
                        else -> Either.Left(it)
                    }
                }
            }
        }
    }

    override suspend fun getAll(query: Query, operation: Operation): Either<Failure, List<V>> {
        return when (operation) {
            is DefaultOperation -> getAll(query, CacheSyncOperation)
            is MainOperation -> getMain.getAll(query)
            is CacheOperation -> getCache.getAll(query)
            is MainSyncOperation -> getMain.getAll(query)
                .flatMap { putCache.putAll(query, it) }
                .handleErrorWith { failure ->
                    when (failure) {
                        is Failure.NoConnection, is Failure.ServerError -> {
                            getAll(query, CacheOperation)
                                .mapLeft { failure }
                        }
                        else -> Either.Left(failure)
                    }
                }
            is CacheSyncOperation -> {
                getCache.getAll(query).handleErrorWith {
                    when (it) {
                        is Failure.DataNotFound -> getAll(query, MainSyncOperation)
                        else -> Either.Left(it)
                    }
                }
            }
        }
    }

    override suspend fun put(query: Query, value: V?, operation: Operation): Either<Failure, V> =
        when (operation) {
            is DefaultOperation -> put(query, value, MainSyncOperation)
            is MainOperation -> putMain.put(query, value)
            is CacheOperation -> putCache.put(query, value)
            is MainSyncOperation -> putMain.put(query, value).flatMap { putCache.put(query, it) }
            is CacheSyncOperation -> putCache.put(query, value).flatMap { putMain.put(query, it) }
        }

    override suspend fun putAll(
        query: Query,
        value: List<V>?,
        operation: Operation
    ): Either<Failure, List<V>> = when (operation) {
        is DefaultOperation -> putAll(query, value, MainSyncOperation)
        is MainOperation -> putMain.putAll(query, value)
        is CacheOperation -> putCache.putAll(query, value)
        is MainSyncOperation -> putMain.putAll(query, value).flatMap { putCache.putAll(query, it) }
        is CacheSyncOperation -> putCache.putAll(query, value).flatMap { putMain.putAll(query, it) }
    }

    override suspend fun delete(query: Query, operation: Operation): Either<Failure, Unit> =
        when (operation) {
            is DefaultOperation -> delete(query, MainSyncOperation)
            is MainOperation -> deleteMain.delete(query)
            is CacheOperation -> deleteCache.delete(query)
            is MainSyncOperation -> deleteMain.delete(query).flatMap { deleteCache.delete(query) }
            is CacheSyncOperation -> deleteCache.delete(query).flatMap { deleteMain.delete(query) }
        }

    override suspend fun deleteAll(query: Query, operation: Operation): Either<Failure, Unit> =
        when (operation) {
            is DefaultOperation -> deleteAll(query, MainSyncOperation)
            is MainOperation -> deleteMain.deleteAll(query)
            is CacheOperation -> deleteCache.deleteAll(query)
            is MainSyncOperation -> deleteMain.deleteAll(query)
                .flatMap { deleteCache.deleteAll(query) }
            is CacheSyncOperation -> deleteCache.deleteAll(query)
                .flatMap { deleteMain.deleteAll(query) }
        }
}