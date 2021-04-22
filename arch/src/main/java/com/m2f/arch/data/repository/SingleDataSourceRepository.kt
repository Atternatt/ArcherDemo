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

import com.m2f.arch.data.datasource.DeleteDataSource
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.datasource.PutDataSource
import com.m2f.arch.data.operation.Operation
import com.m2f.arch.data.query.Query

class SingleDataSourceRepository<T>(
    private val getDataSource: GetDataSource<T>,
    private val putDataSource: PutDataSource<T>,
    private val deleteDataSource: DeleteDataSource
) : GetRepository<T>, PutRepository<T>, DeleteRepository {

    override suspend fun get(query: Query, operation: Operation) = getDataSource.get(query)

    override suspend fun getAll(query: Query, operation: Operation) = getDataSource.getAll(query)

    override suspend fun put(query: Query, value: T?, operation: Operation) =
        putDataSource.put(query, value)

    override suspend fun putAll(query: Query, value: List<T>?, operation: Operation) =
        putDataSource.putAll(query, value)

    override suspend fun delete(query: Query, operation: Operation) = deleteDataSource.delete(query)

    override suspend fun deleteAll(query: Query, operation: Operation) =
        deleteDataSource.deleteAll(query)
}

class SingleGetDataSourceRepository<T>(private val getDataSource: GetDataSource<T>) :
    GetRepository<T> {

    override suspend fun get(query: Query, operation: Operation) = getDataSource.get(query)

    override suspend fun getAll(query: Query, operation: Operation) = getDataSource.getAll(query)
}

class SinglePutDataSourceRepository<T>(private val putDataSource: PutDataSource<T>) :
    PutRepository<T> {
    override suspend fun put(query: Query, value: T?, operation: Operation) =
        putDataSource.put(query, value)

    override suspend fun putAll(query: Query, value: List<T>?, operation: Operation) =
        putDataSource.putAll(query, value)
}

class SingleDeleteDataSourceRepository(private val deleteDataSource: DeleteDataSource) :
    DeleteRepository {

    override suspend fun delete(query: Query, operation: Operation) = deleteDataSource.delete(query)

    override suspend fun deleteAll(query: Query, operation: Operation) =
        deleteDataSource.deleteAll(query)
}