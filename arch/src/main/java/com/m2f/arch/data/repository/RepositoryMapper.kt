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
import com.m2f.arch.data.mapper.map
import com.m2f.arch.data.operation.Operation
import com.m2f.arch.data.query.Query

/**
 * This repository uses mappers to map objects and redirects them to the contained repository, acting as a simple "translator".
 *
 * @param getRepository Repository with get operations
 * @param putRepository Repository with put operations
 * @param deleteRepository Repository with delete operations
 * @param toOutMapper Mapper to map data objects to domain objects
 * @param toInMapper Mapper to map domain objects to data objects
 */
class RepositoryMapper<In, Out>(
    private val getRepository: GetRepository<In>,
    private val putRepository: PutRepository<In>,
    private val deleteRepository: DeleteRepository,
    private val toOutMapper: Mapper<In, Out>,
    private val toInMapper: Mapper<Out, In>
) : GetRepository<Out>, PutRepository<Out>, DeleteRepository {

    override suspend fun get(query: Query, operation: Operation) =
        getRepository.get(query, operation).map { toOutMapper.map(it) }

    override suspend fun getAll(query: Query, operation: Operation) =
        getRepository.getAll(query, operation).map { toOutMapper.map(it) }

    override suspend fun put(
        query: Query,
        value: Out?,
        operation: Operation
    ): Either<Failure, Out> {
        val mapped = value?.let { toInMapper.map(it) }
        return putRepository.put(query, mapped, operation).map(toOutMapper)
    }

    override suspend fun putAll(
        query: Query,
        value: List<Out>?,
        operation: Operation
    ): Either<Failure, List<Out>> {
        val mapped = value?.let { toInMapper.map(it) }
        return putRepository.putAll(query, mapped, operation).map { toOutMapper.map(it) }
    }

    override suspend fun delete(query: Query, operation: Operation) =
        deleteRepository.delete(query, operation)

    override suspend fun deleteAll(query: Query, operation: Operation) =
        deleteRepository.deleteAll(query, operation)
}

class GetRepositoryMapper<In, Out>(
    private val getRepository: GetRepository<In>,
    toOutMapper: Mapper<In, Out>
) : GetRepository<Out>, Mapper<In, Out> by toOutMapper {

    override suspend fun get(query: Query, operation: Operation): Either<Failure, Out> =
        getRepository.get(query, operation).map(this)

    override suspend fun getAll(query: Query, operation: Operation): Either<Failure, List<Out>> =
        getRepository.getAll(query, operation).map { map(it) }
}

class PutRepositoryMapper<In, Out>(
    private val putRepository: PutRepository<In>,
    private val toOutMapper: Mapper<In, Out>,
    private val toInMapper: Mapper<Out, In>
) : PutRepository<Out> {

    override suspend fun put(
        query: Query,
        value: Out?,
        operation: Operation
    ): Either<Failure, Out> {
        val mapped = value?.let { toInMapper.map(it) }
        return putRepository.put(query, mapped, operation).map(toOutMapper)
    }

    override suspend fun putAll(
        query: Query,
        value: List<Out>?,
        operation: Operation
    ): Either<Failure, List<Out>> {
        val mapped = value?.let { toInMapper.map(it) }
        return putRepository.putAll(query, mapped, operation).map { toOutMapper.map(it) }
    }
}