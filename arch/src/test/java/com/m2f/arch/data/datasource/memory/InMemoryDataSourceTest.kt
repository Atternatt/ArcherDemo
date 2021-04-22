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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class InMemoryDataSourceTest {

    companion object {
        val keyQuery = KeyQuery("key")
        val wrongKeyQuery = KeyQuery("wrong-key")
    }

    private lateinit var getDataSource: GetDataSource<Int>
    private lateinit var putDataSource: PutDataSource<Int>
    private lateinit var deleteDataSource: DeleteDataSource

    @Before
    fun setUp() {
        val datasource = InMemoryDataSource<Int>()
        getDataSource = datasource
        putDataSource = datasource
        deleteDataSource = datasource
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given a element inserting it with a KeyQuery will return the same value`() =
        runBlockingTest {
            val valueToInsert = 1
            val valueInserted = putDataSource.put(keyQuery, valueToInsert)
            assertEquals(Either.Right(valueToInsert), valueInserted)
        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given a datasource calling get will retrieve the same item that was inserted`() =
        runBlockingTest {
            val insertedValue = putDataSource.put(keyQuery, 1)
            val retrievedValue = getDataSource.get(keyQuery)
            assertEquals(retrievedValue, insertedValue)
        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given a datasource trying to get a item with a bad key will fail`() = runBlockingTest {
        putDataSource.put(keyQuery, 1)
        assert(getDataSource.get(wrongKeyQuery).isLeft())
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given a datasource trying to retrieve a element previosuly removed will fail`() =
        runBlockingTest {
            putDataSource.put(keyQuery, 1)
            deleteDataSource.delete(keyQuery)
            assert(getDataSource.get(keyQuery).isLeft())
        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given a list of elements inserting them with a KeyQuery will return the same list`() =
        runBlockingTest {
            val valueToInsert = listOf(1, 2, 3, 4, 5)
            val valueInserted = putDataSource.putAll(keyQuery, valueToInsert)
            assertEquals(Either.Right(valueToInsert), valueInserted)
        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given a datasource calling getAll will retrieve the same list that was inserted`() =
        runBlockingTest {
            val insertedValue = putDataSource.putAll(keyQuery, listOf(1, 2, 3, 4, 5))
            val retrievedValue = getDataSource.getAll(keyQuery)
            assertEquals(retrievedValue, insertedValue)
        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given a datasource trying to get a list of items with a bad key will Fail with DataNotFound`() =
        runBlockingTest {
            putDataSource.putAll(keyQuery, listOf(1, 2, 3, 4, 5))
            assertEquals(Either.Left(Failure.DataNotFound), getDataSource.getAll(wrongKeyQuery))
        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given a datasource trying to retrieve a list of elements previosuly removed will Fail`() =
        runBlockingTest {
            putDataSource.putAll(keyQuery, listOf(1, 2, 3, 4, 5))
            deleteDataSource.deleteAll(keyQuery)
            assert(getDataSource.getAll(keyQuery).isLeft())
        }
}