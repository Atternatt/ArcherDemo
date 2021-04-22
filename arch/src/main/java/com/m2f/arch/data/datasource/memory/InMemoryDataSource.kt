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

package com.m2f.arch.data.datasource.memory

import arrow.core.Either
import com.m2f.arch.data.datasource.DeleteDataSource
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.datasource.PutDataSource
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.query.KeyQuery
import com.m2f.arch.data.query.Query

class InMemoryDataSource<V> : GetDataSource<V>, PutDataSource<V>, DeleteDataSource {

    private val objects: MutableMap<String, V> = mutableMapOf()
    private val arrays: MutableMap<String, List<V>> = mutableMapOf()

    override suspend fun get(query: Query): Either<Failure, V> =
        when (query) {
            is KeyQuery -> {
                val item = objects[query.key]
                if (item != null) {
                    Either.Right<V>(item)
                } else {
                    Either.Left(Failure.DataNotFound)
                }
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }

    override suspend fun getAll(query: Query): Either<Failure, List<V>> =
        when (query) {
            is KeyQuery -> {
                val list = arrays[query.key]
                if (!list.isNullOrEmpty()) {
                    Either.Right(list)
                } else {
                    Either.Left(Failure.DataNotFound)
                }
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }

    override suspend fun put(query: Query, value: V?): Either<Failure, V> =
        when (query) {
            is KeyQuery -> {
                if (value != null) {
                    objects[query.key] = value
                    Either.Right<V>(value)
                } else {
                    Either.Left(Failure.DataEmpty)
                }
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }

    override suspend fun putAll(query: Query, value: List<V>?): Either<Failure, List<V>> =
        when (query) {
            is KeyQuery -> {
                if (value != null) {
                    arrays[query.key] = value
                    Either.Right(value)
                } else {
                    Either.Left(Failure.DataEmpty)
                }
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }

    override suspend fun delete(query: Query): Either<Failure, Unit> {
        return when (query) {
            is KeyQuery -> {
                objects.remove(query.key)
                Either.Right(Unit)
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }
    }

    override suspend fun deleteAll(query: Query): Either<Failure, Unit> {
        return when (query) {
            is KeyQuery -> {
                arrays.remove(query.key)
                Either.Right(Unit)
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }
    }
}