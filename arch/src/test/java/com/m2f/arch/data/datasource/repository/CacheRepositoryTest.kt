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

package com.m2f.arch.data.datasource.repository

import arrow.core.Either
import com.m2f.arch.data.datasource.DeleteDataSource
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.datasource.PutDataSource
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.operation.CacheOperation
import com.m2f.arch.data.operation.CacheSyncOperation
import com.m2f.arch.data.operation.DefaultOperation
import com.m2f.arch.data.operation.MainOperation
import com.m2f.arch.data.operation.MainSyncOperation
import com.m2f.arch.data.query.KeyQuery
import com.m2f.arch.data.repository.CacheRepository
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals

class CacheRepositoryTest {

    private val keyQuery = KeyQuery("key")
    private val insertedValue = 1
    private val insertedList = listOf(1, 2, 3, 4, 5)

    private val remoteGetDataSource: GetDataSource<Int> = mockk()
    private val remotePutDataSource: PutDataSource<Int> = mockk()
    private val remoteDeleteDataSource: DeleteDataSource = mockk()

    private val localGetDataSource: GetDataSource<Int> = mockk()
    private val localPutDataSource: PutDataSource<Int> = mockk()
    private val localDeleteDataSource: DeleteDataSource = mockk()

    val cacheRepository: CacheRepository<Int> = CacheRepository(
        getMain = remoteGetDataSource,
        putMain = remotePutDataSource,
        deleteMain = remoteDeleteDataSource,
        getCache = localGetDataSource,
        putCache = localPutDataSource,
        deleteCache = localDeleteDataSource
    )

    //region get section
    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository  cache data will retrieve data from localDataSource using DefaultOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.get(eq(keyQuery))
            } answers { Either.Right(insertedValue) }

            //When
            val result = cacheRepository.get(keyQuery, DefaultOperation)

            //Then
            assertEquals(Either.Right(insertedValue), result)

            coVerify {
                localGetDataSource.get(eq(keyQuery))
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository out list data will retrieve data from remoteDataSource and will store it to cache using DefaultOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.get(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            coEvery {
                remoteGetDataSource.get(eq(keyQuery))
            } answers { Either.Right(insertedValue) }

            coEvery {
                localPutDataSource.put(keyQuery, insertedValue)
            } answers { Either.Right(insertedValue) }

            //When
            val result = cacheRepository.get(keyQuery, DefaultOperation)

            //Then
            assertEquals(Either.Right(insertedValue), result)

            coVerify {
                localGetDataSource.get(eq(keyQuery))
                remoteGetDataSource.get(eq(keyQuery))
                localPutDataSource.put(eq(keyQuery), insertedValue)
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will retrieve data from remoteDataSource and will store it to cache using CacheSyncOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.get(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            coEvery {
                remoteGetDataSource.get(eq(keyQuery))
            } answers { Either.Right(insertedValue) }

            coEvery {
                localPutDataSource.put(keyQuery, insertedValue)
            } answers { Either.Right(insertedValue) }

            //When
            val result = cacheRepository.get(keyQuery, CacheSyncOperation)

            //Then
            assertEquals(Either.Right(insertedValue), result)

            coVerifySequence {
                localGetDataSource.get(eq(keyQuery))
                remoteGetDataSource.get(eq(keyQuery))
                localPutDataSource.put(eq(keyQuery), insertedValue)
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will fail using CacheOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.get(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            //When
            val result = cacheRepository.get(keyQuery, CacheOperation)

            //Then
            assertEquals(Either.Left(Failure.DataNotFound), result)

            coVerify {
                remoteGetDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository  data will retrieve it only from cache using CacheOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.get(eq(keyQuery))
            } answers { Either.Right(insertedValue) }

            //When
            val result = cacheRepository.get(keyQuery, CacheOperation)

            //Then
            assertEquals(Either.Right(insertedValue), result)

            coVerify {
                remoteGetDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will retrieve data from remote using MainOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteGetDataSource.get(eq(keyQuery))
            } answers { Either.Right(insertedValue) }

            //When
            val result = cacheRepository.get(keyQuery, MainOperation)

            //Then
            assertEquals(Either.Right(insertedValue), result)

            coVerify {
                remoteGetDataSource.get(eq(keyQuery))
            }
            coVerify {
                localGetDataSource wasNot Called
            }

            coVerify {
                localPutDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will retrieve data from remote and will store it to cache using MainSyncOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.get(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            coEvery {
                remoteGetDataSource.get(eq(keyQuery))
            } answers { Either.Right(insertedValue) }

            coEvery {
                localPutDataSource.put(keyQuery, insertedValue)
            } answers { Either.Right(insertedValue) }

            //When
            val result = cacheRepository.get(keyQuery, MainSyncOperation)

            //Then
            assertEquals(Either.Right(insertedValue), result)

            coVerifySequence {
                remoteGetDataSource.get(eq(keyQuery))
                localPutDataSource.put(eq(keyQuery), insertedValue)
            }

            coVerify {
                localGetDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository if remote data source fails with NoConnection will try to get data from cache using MainSyncOperation`() =
        runBlockingTest {
            //With
            coEvery {
                localGetDataSource.get(eq(keyQuery))
            } answers { Either.Right(insertedValue) }

            coEvery {
                remoteGetDataSource.get(eq(keyQuery))
            } answers { Either.Left(Failure.NoConnection) }

            coEvery {
                localPutDataSource.put(keyQuery, insertedValue)
            } answers { Either.Right(insertedValue) }

            //When
            val result = cacheRepository.get(keyQuery, DefaultOperation)

            assertEquals(Either.Right(insertedValue), result)

        }

    @ExperimentalCoroutinesApi
    @Test
    fun `CacheRepository will fail with NoConnection if there isn't connection and there isn't data in cache`() =
        runBlockingTest {
            //With
            coEvery {
                localGetDataSource.get(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            coEvery {
                remoteGetDataSource.get(eq(keyQuery))
            } answers { Either.Left(Failure.NoConnection) }

            //When
            val result = cacheRepository.get(keyQuery, DefaultOperation)

            assertEquals(Either.Left(Failure.NoConnection), result)

        }
    //endregion

    //region getAll
    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository  a cache list of data will retrieve the list from localDataSource using DefaultOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Right(insertedList) }

            //When
            val result = cacheRepository.getAll(keyQuery, DefaultOperation)

            //Then
            assertEquals(Either.Right(insertedList), result)

            coVerify {
                localGetDataSource.getAll(eq(keyQuery))
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will retrieve data from remoteDataSource and will store it to cache using DefaultOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            coEvery {
                remoteGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Right(insertedList) }

            coEvery {
                localPutDataSource.putAll(keyQuery, insertedList)
            } answers { Either.Right(insertedList) }

            //When
            val result = cacheRepository.getAll(keyQuery, DefaultOperation)

            //Then
            assertEquals(Either.Right(insertedList), result)

            coVerify {
                localGetDataSource.getAll(eq(keyQuery))
                remoteGetDataSource.getAll(eq(keyQuery))
                localPutDataSource.putAll(eq(keyQuery), insertedList)
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will retrieve list data from remoteDataSource and will store it to cache using CacheSyncOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            coEvery {
                remoteGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Right(insertedList) }

            coEvery {
                localPutDataSource.putAll(keyQuery, insertedList)
            } answers { Either.Right(insertedList) }

            //When
            val result = cacheRepository.getAll(keyQuery, CacheSyncOperation)

            //Then
            assertEquals(Either.Right(insertedList), result)

            coVerifySequence {
                localGetDataSource.getAll(eq(keyQuery))
                remoteGetDataSource.getAll(eq(keyQuery))
                localPutDataSource.putAll(eq(keyQuery), insertedList)
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository out list data will fail using CacheOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            //When
            val result = cacheRepository.getAll(keyQuery, CacheOperation)

            //Then
            assertEquals(Either.Left(Failure.DataNotFound), result)

            coVerify {
                remoteGetDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository  list data will retrieve it only from cache using CacheOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Right(insertedList) }

            //When
            val result = cacheRepository.getAll(keyQuery, CacheOperation)

            //Then
            assertEquals(Either.Right(insertedList), result)

            coVerify {
                remoteGetDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will retrieve list data from remote using MainOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Right(insertedList) }

            //When
            val result = cacheRepository.getAll(keyQuery, MainOperation)

            //Then
            assertEquals(Either.Right(insertedList), result)

            coVerifyAll {
                remoteGetDataSource.getAll(eq(keyQuery))
                localGetDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository out list data will retrieve it from remote using MainOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Right(insertedList) }

            //When
            val result = cacheRepository.getAll(keyQuery, MainOperation)

            //Then
            assertEquals(Either.Right(insertedList), result)

            coVerifyAll {
                localGetDataSource wasNot Called
                localPutDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository out list data will retrieve it from remote and will store it to cache using MainSyncOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Left(Failure.DataNotFound) }

            coEvery {
                remoteGetDataSource.getAll(eq(keyQuery))
            } answers { Either.Right(insertedList) }

            coEvery {
                localPutDataSource.putAll(keyQuery, insertedList)
            } answers { Either.Right(insertedList) }

            //When
            val result = cacheRepository.getAll(keyQuery, MainSyncOperation)

            //Then
            assertEquals(Either.Right(insertedList), result)

            coVerifySequence {
                remoteGetDataSource.getAll(eq(keyQuery))
                localPutDataSource.putAll(eq(keyQuery), insertedList)
            }

            coVerify {
                localGetDataSource wasNot Called
            }
        }
    //endregion

    //region delete
    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will remove date from local using CacheOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localDeleteDataSource.delete(eq(keyQuery))
            } answers { Either.Right(Unit) }

            val result = cacheRepository.delete(keyQuery, CacheOperation)

            assertEquals(Either.Right(Unit), result)

            coVerifyAll {
                localDeleteDataSource.delete(eq(keyQuery))
                remoteDeleteDataSource wasNot Called
            }

        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will remove date from remote using MainOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteDeleteDataSource.delete(eq(keyQuery))
            } answers { Either.Right(Unit) }

            val result = cacheRepository.delete(keyQuery, MainOperation)

            assertEquals(Either.Right(Unit), result)

            coVerifyAll {
                remoteDeleteDataSource.delete(eq(keyQuery))
                localDeleteDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will remove data from remote and then from local using MainSyncOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteDeleteDataSource.delete(eq(keyQuery))
            } answers { Either.Right(Unit) }

            coEvery {
                localDeleteDataSource.delete(eq(keyQuery))
            } answers { Either.Right(Unit) }

            val result = cacheRepository.delete(keyQuery, MainSyncOperation)

            assertEquals(Either.Right(Unit), result)

            coVerifySequence {
                remoteDeleteDataSource.delete(eq(keyQuery))
                localDeleteDataSource.delete(eq(keyQuery))
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will remove data from local and then from remote using CacheSyncOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteDeleteDataSource.delete(eq(keyQuery))
            } answers { Either.Right(Unit) }

            coEvery {
                localDeleteDataSource.delete(eq(keyQuery))
            } answers { Either.Right(Unit) }

            val result = cacheRepository.delete(keyQuery, CacheSyncOperation)

            assertEquals(Either.Right(Unit), result)

            coVerifySequence {
                localDeleteDataSource.delete(eq(keyQuery))
                remoteDeleteDataSource.delete(eq(keyQuery))
            }
        }
    //endregion

    //region deleteAll
    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will remove list data from local using CacheOperation`() =
        runBlockingTest {

            //With
            coEvery {
                localDeleteDataSource.deleteAll(eq(keyQuery))
            } answers { Either.Right(Unit) }

            val result = cacheRepository.deleteAll(keyQuery, CacheOperation)

            assertEquals(Either.Right(Unit), result)

            coVerifyAll {
                localDeleteDataSource.deleteAll(eq(keyQuery))
                remoteDeleteDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will remove list data from remote using MainOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteDeleteDataSource.deleteAll(eq(keyQuery))
            } answers { Either.Right(Unit) }

            val result = cacheRepository.deleteAll(keyQuery, MainOperation)

            assertEquals(Either.Right(Unit), result)

            coVerifyAll {
                remoteDeleteDataSource.deleteAll(eq(keyQuery))
                localDeleteDataSource wasNot Called
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will remove list data from remote and then from local using MainSyncOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteDeleteDataSource.deleteAll(eq(keyQuery))
            } answers { Either.Right(Unit) }

            coEvery {
                localDeleteDataSource.deleteAll(eq(keyQuery))
            } answers { Either.Right(Unit) }

            val result = cacheRepository.deleteAll(keyQuery, MainSyncOperation)

            assertEquals(Either.Right(Unit), result)

            coVerifySequence {
                remoteDeleteDataSource.deleteAll(eq(keyQuery))
                localDeleteDataSource.deleteAll(eq(keyQuery))
            }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `Given a CacheRepository will remove list data from local and then from remote using CacheSyncOperation`() =
        runBlockingTest {

            //With
            coEvery {
                remoteDeleteDataSource.deleteAll(eq(keyQuery))
            } answers { Either.Right(Unit) }

            coEvery {
                localDeleteDataSource.deleteAll(eq(keyQuery))
            } answers { Either.Right(Unit) }

            val result = cacheRepository.deleteAll(keyQuery, CacheSyncOperation)

            assertEquals(Either.Right(Unit), result)

            coVerifySequence {
                localDeleteDataSource.deleteAll(eq(keyQuery))
                remoteDeleteDataSource.deleteAll(eq(keyQuery))
            }
        }
    //endregion
}