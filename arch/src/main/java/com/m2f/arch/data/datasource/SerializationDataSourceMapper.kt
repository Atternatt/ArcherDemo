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
import com.m2f.arch.data.query.Query

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 * This class map a List to a single Object when using putAll, in this case it will call the put method on the contained data source
 * This class map a Object to a List when using getAll, int his case it will call the get method on the contained data source
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toOutListMapper Mapper to map data source objects to repository object lists
 * @param toInMapper Mapper to map repository objects to data source objects
 * @param toInMapperFromList Mapper to map repository object lists to data source objects
 */
class SerializationDataSourceMapper<SerializedIn, Out>(
    private val getDataSource: GetDataSource<SerializedIn>,
    private val putDataSource: PutDataSource<SerializedIn>,
    private val deleteDataSource: DeleteDataSource,
    private val toOutMapper: Mapper<SerializedIn, Out>,
    private val toOutListMapper: Mapper<SerializedIn, List<Out>>,
    private val toInMapper: Mapper<Out, SerializedIn>,
    private val toInMapperFromList: Mapper<List<Out>, SerializedIn>
) : GetDataSource<Out>, PutDataSource<Out>, DeleteDataSource {

    override suspend fun get(query: Query) = getDataSource.get(query).map(toOutMapper)

    override suspend fun getAll(query: Query) =
        getDataSource.get(query).map { toOutListMapper.map(it) }

    override suspend fun put(query: Query, value: Out?): Either<Failure, Out> {
        val mapped = value?.let { toInMapper.map(value) }
        return putDataSource.put(query, mapped)
            .map { toOutMapper.map(it) }
    }

    override suspend fun putAll(query: Query, value: List<Out>?): Either<Failure, List<Out>> {
        val mapped = value?.let { toInMapperFromList.map(value) }
        return putDataSource.put(query, mapped)
            .map { toOutListMapper.map(it) }
    }

    override suspend fun delete(query: Query) = deleteDataSource.delete(query)

    override suspend fun deleteAll(query: Query) = deleteDataSource.deleteAll(query)
}
