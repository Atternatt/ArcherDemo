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

package com.m2f.arch.data.datasource

import arrow.core.Either
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.mapper.Mapper
import com.m2f.arch.data.query.IdQuery
import com.m2f.arch.data.query.IdsQuery
import com.m2f.arch.data.query.Query
import com.m2f.arch.data.repository.GetRepository
import com.m2f.arch.data.repository.PutRepository
import com.m2f.arch.data.repository.SingleDeleteDataSourceRepository
import com.m2f.arch.data.repository.SingleGetDataSourceRepository
import com.m2f.arch.data.repository.SinglePutDataSourceRepository
import com.m2f.arch.data.repository.withMapping

interface DataSource

// DataSources
interface GetDataSource<V> : DataSource {
    suspend fun get(query: Query): Either<Failure, V>

    suspend fun getAll(query: Query): Either<Failure, List<V>>
}

interface PutDataSource<V> : DataSource {
    suspend fun put(query: Query, value: V? = null): Either<Failure, V>

    suspend fun putAll(query: Query, value: List<V>? = emptyList()): Either<Failure, List<V>>
}

interface DeleteDataSource : DataSource {
    suspend fun delete(query: Query): Either<Failure, Unit>

    suspend fun deleteAll(query: Query): Either<Failure, Unit>
}

// Extensions
suspend fun <K, V> GetDataSource<V>.get(id: K): Either<Failure, V> = get(IdQuery(id))

suspend fun <K, V> GetDataSource<V>.getAll(ids: List<K>): Either<Failure, List<V>> =
    getAll(IdsQuery(ids))

suspend fun <K, V> PutDataSource<V>.put(id: K, value: V?): Either<Failure, V> =
    put(IdQuery(id), value)

suspend fun <K, V> PutDataSource<V>.putAll(ids: List<K>, values: List<V>?) =
    putAll(IdsQuery(ids), values)

suspend fun <K> DeleteDataSource.delete(id: K) = delete(IdQuery(id))

suspend fun <K> DeleteDataSource.deleteAll(ids: List<K>) = deleteAll(IdsQuery(ids))

// Extensions to create
fun <V> GetDataSource<V>.toGetRepository() = SingleGetDataSourceRepository(this)

fun <K, V> GetDataSource<K>.withMapping(mapper: Mapper<K, V>): GetDataSource<V> =
    GetDataSourceMapper(this, mapper)

operator fun <K, V> GetDataSource<K>.plus(mapper: Mapper<K, V>): GetDataSource<V> =
    withMapping(mapper)

fun <K, V> GetDataSource<K>.toGetRepository(mapper: Mapper<K, V>): GetRepository<V> =
    toGetRepository().withMapping(mapper)

fun <V> PutDataSource<V>.toPutRepository() = SinglePutDataSourceRepository(this)

fun <K, V> PutDataSource<K>.toPutRepository(
    toMapper: Mapper<K, V>,
    fromMapper: Mapper<V, K>
): PutRepository<V> = toPutRepository().withMapping(toMapper, fromMapper)

fun <K, V> PutDataSource<K>.withMapping(
    toMapper: Mapper<K, V>,
    fromMapper: Mapper<V, K>
): PutDataSource<V> = PutDataSourceMapper(this, toMapper, fromMapper)

fun DeleteDataSource.toDeleteRepository() = SingleDeleteDataSourceRepository(this)