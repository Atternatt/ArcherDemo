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
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.mapper.Mapper
import com.m2f.arch.data.operation.DefaultOperation
import com.m2f.arch.data.operation.Operation
import com.m2f.arch.data.query.IdQuery
import com.m2f.arch.data.query.IdsQuery
import com.m2f.arch.data.query.Query

interface Repository {

    fun notSupportedQuery(): Nothing = throw UnsupportedOperationException("Query not supported")

    fun notSupportedOperation(): Nothing =
        throw UnsupportedOperationException("Operation not defined")
}

// Repositories
interface GetRepository<V> : Repository {
    suspend fun get(query: Query, operation: Operation = DefaultOperation): Either<Failure, V>
    suspend fun getAll(
        query: Query,
        operation: Operation = DefaultOperation
    ): Either<Failure, List<V>>
}

interface PutRepository<V> : Repository {
    suspend fun put(
        query: Query,
        value: V? = null,
        operation: Operation = DefaultOperation
    ): Either<Failure, V>

    suspend fun putAll(
        query: Query,
        value: List<V>? = emptyList(),
        operation: Operation = DefaultOperation
    ): Either<Failure, List<V>>
}

interface DeleteRepository : Repository {
    suspend fun delete(query: Query, operation: Operation = DefaultOperation): Either<Failure, Unit>
    suspend fun deleteAll(
        query: Query,
        operation: Operation = DefaultOperation
    ): Either<Failure, Unit>
}

// Extensions

suspend fun <K, V> GetRepository<V>.get(id: K, operation: Operation = DefaultOperation) = get(
    IdQuery(id), operation
)

suspend fun <K, V> GetRepository<V>.getAll(ids: List<K>, operation: Operation = DefaultOperation) =
    getAll(
        IdsQuery(ids), operation
    )

suspend fun <K, V> PutRepository<V>.put(id: K, value: V?, operation: Operation = DefaultOperation) =
    put(
        IdQuery(id), value, operation
    )

suspend fun <K, V> PutRepository<V>.putAll(
    ids: List<K>,
    values: List<V>? = emptyList(),
    operation: Operation = DefaultOperation
) = putAll(
    IdsQuery(ids), values,
    operation
)

suspend fun <K> DeleteRepository.delete(id: K, operation: Operation = DefaultOperation) = delete(
    IdQuery(id), operation
)

suspend fun <K> DeleteRepository.deleteAll(ids: List<K>, operation: Operation = DefaultOperation) =
    deleteAll(
        IdsQuery(ids), operation
    )

fun <K, V> GetRepository<K>.withMapping(mapper: Mapper<K, V>): GetRepository<V> =
    GetRepositoryMapper(this, mapper)

operator fun <K, V> GetRepository<K>.plus(mapper: Mapper<K, V>): GetRepository<V> =
    withMapping(mapper)

fun <K, V> GetRepository<K>.toGetRepository(mapper: Mapper<K, V>): GetRepository<V> =
    withMapping(mapper)

fun <K, V> PutRepository<K>.withMapping(
    toMapper: Mapper<K, V>,
    fromMapper: Mapper<V, K>
): PutRepository<V> = PutRepositoryMapper(this, toMapper, fromMapper)

fun <K, V> PutRepository<K>.toPutRepository(
    toMapper: Mapper<K, V>,
    fromMapper: Mapper<V, K>
): PutRepository<V> = withMapping(toMapper, fromMapper)