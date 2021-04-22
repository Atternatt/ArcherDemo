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
import com.m2f.arch.data.mapper.map
import com.m2f.arch.data.query.Query

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toInMapper Mapper to map repository objects to data source objects
 */
class DataSourceMapper<In, Out>(
    getDataSource: GetDataSource<In>,
    putDataSource: PutDataSource<In>,
    private val deleteDataSource: DeleteDataSource,
    toOutMapper: Mapper<In, Out>,
    toInMapper: Mapper<Out, In>
) : GetDataSource<Out>, PutDataSource<Out>, DeleteDataSource {

    private val getDataSourceMapper = GetDataSourceMapper(getDataSource, toOutMapper)
    private val putDataSourceMapper = PutDataSourceMapper(putDataSource, toOutMapper, toInMapper)

    override suspend fun get(query: Query) = getDataSourceMapper.get(query)

    override suspend fun getAll(query: Query) = getDataSourceMapper.getAll(query)

    override suspend fun put(query: Query, value: Out?) = putDataSourceMapper.put(query, value)

    override suspend fun putAll(query: Query, value: List<Out>?) =
        putDataSourceMapper.putAll(query, value)

    override suspend fun delete(query: Query) = deleteDataSource.delete(query)

    override suspend fun deleteAll(query: Query) = deleteDataSource.deleteAll(query)
}

class GetDataSourceMapper<In, Out>(
    private val getDataSource: GetDataSource<In>,
    private val toOutMapper: Mapper<In, Out>
) : GetDataSource<Out> {

    override suspend fun get(query: Query) = getDataSource.get(query).map(toOutMapper)

    override suspend fun getAll(query: Query) =
        getDataSource.getAll(query).map { toOutMapper.map(it) }
}

class PutDataSourceMapper<In, Out>(
    private val putDataSource: PutDataSource<In>,
    private val toOutMapper: Mapper<In, Out>,
    private val toInMapper: Mapper<Out, In>
) : PutDataSource<Out> {

    override suspend fun put(query: Query, value: Out?): Either<Failure, Out> {
        val mapped = value?.let { toInMapper.map(it) }
        return putDataSource.put(query, mapped)
            .map { toOutMapper.map(it) }
    }

    override suspend fun putAll(query: Query, value: List<Out>?): Either<Failure, List<Out>> {
        val mapped = value?.let { toInMapper.map(it) }
        return putDataSource.putAll(query, mapped)
            .map { toOutMapper.map(it) }
    }
}
