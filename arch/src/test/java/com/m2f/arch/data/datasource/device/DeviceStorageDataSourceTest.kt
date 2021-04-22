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

package com.m2f.arch.data.datasource.device

import android.content.SharedPreferences
import arrow.core.Either
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.query.AllObjectsQuery
import com.m2f.arch.data.query.KeyQuery
import com.m2f.arch.data.query.Query
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeviceStorageDataSourceTest {

    private val sharedPreferences: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk()

    private val prefix = "prefix"

    private val expectedString = "string"
    private val expectedInt = 0
    private val expectedLong = 0L
    private val expectedFloat = 0F
    private val expectedBoolean = true
    private val expectedInvalid = 0.0

    private val keyString = "string"
    private val keyInt = "int"
    private val keyLong = "long"
    private val keyFloat = "float"
    private val keyBoolean = "boolean"
    private val keyInvalid = "invalid"

    private val sharedPreferencesContent = mapOf(
        keyString to expectedString,
        keyInt to expectedInt,
        keyLong to expectedLong,
        keyFloat to expectedFloat,
        keyBoolean to expectedBoolean,
        "$prefix.$keyString" to expectedString,
        "$prefix.$keyInt" to expectedInt,
        "$prefix.$keyLong" to expectedLong,
        "$prefix.$keyFloat" to expectedFloat,
        "$prefix.$keyBoolean" to expectedBoolean
    )

    private lateinit var deviceStorageStringDatasource: DeviceStorageDataSource<String>
    private lateinit var deviceStorageIntDatasource: DeviceStorageDataSource<Int>
    private lateinit var deviceStorageLongDatasource: DeviceStorageDataSource<Long>
    private lateinit var deviceStorageFloatDatasource: DeviceStorageDataSource<Float>
    private lateinit var deviceStorageBooleanDatasource: DeviceStorageDataSource<Boolean>
    private lateinit var deviceStorageInvalidDatasource: DeviceStorageDataSource<Double>

    @Before
    fun setUp() {
        deviceStorageStringDatasource = DeviceStorageDataSource<String>(sharedPreferences)
        deviceStorageIntDatasource = DeviceStorageDataSource<Int>(sharedPreferences)
        deviceStorageLongDatasource = DeviceStorageDataSource<Long>(sharedPreferences)
        deviceStorageFloatDatasource = DeviceStorageDataSource<Float>(sharedPreferences)
        deviceStorageBooleanDatasource = DeviceStorageDataSource<Boolean>(sharedPreferences)
        deviceStorageInvalidDatasource = DeviceStorageDataSource<Double>(sharedPreferences)

        coEvery { sharedPreferences.all } returns sharedPreferencesContent
        coEvery { sharedPreferences.edit() } returns editor
        coEvery { editor.putString(any(), any()) } returns editor
        coEvery { editor.putInt(any(), any()) } returns editor
        coEvery { editor.putLong(any(), any()) } returns editor
        coEvery { editor.putFloat(any(), any()) } returns editor
        coEvery { editor.putBoolean(any(), any()) } returns editor
        coEvery { editor.clear() } returns editor
        coEvery { editor.remove(any()) } returns editor
        coEvery { editor.apply() } just Runs
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `Getting an element fails with DataNotFound if shared preferences doesn't contains the value`() =
        runBlockingTest {

            coEvery { sharedPreferences.contains(any()) } returns false

            val result = deviceStorageStringDatasource.get(KeyQuery(keyString))

            assertEquals(Either.Left(Failure.DataNotFound), result)
        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Getting an element fails with QueryNotSupported used other Queries different than KeyQuery`() =
        runBlockingTest {

            coEvery { sharedPreferences.contains(any()) } returns false

            val result = deviceStorageStringDatasource.get(Query())

            assertEquals(Either.Left(Failure.QueryNotSupported), result)
        }

    @Test
    @ExperimentalCoroutinesApi
    fun getString() = runBlockingTest {

        coEvery { sharedPreferences.contains(any()) } returns true

        val result = deviceStorageStringDatasource.get(KeyQuery(keyString))

        assertEquals(Either.Right(expectedString), result)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun getInt() = runBlockingTest {
        coEvery { sharedPreferences.contains(any()) } returns true

        val result = deviceStorageIntDatasource.get(KeyQuery(keyInt))

        assertEquals(Either.Right(expectedInt), result)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun getLong() = runBlockingTest {
        coEvery { sharedPreferences.contains(any()) } returns true

        val result = deviceStorageLongDatasource.get(KeyQuery(keyLong))

        assertEquals(Either.Right(expectedLong), result)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun getFloat() = runBlockingTest {
        coEvery { sharedPreferences.contains(any()) } returns true

        val result = deviceStorageFloatDatasource.get(KeyQuery(keyFloat))

        assertEquals(Either.Right(expectedFloat), result)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun getBoolean() = runBlockingTest {
        coEvery { sharedPreferences.contains(any()) } returns true

        val result = deviceStorageBooleanDatasource.get(KeyQuery(keyBoolean))

        assertEquals(Either.Right(expectedBoolean), result)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `All calls to getAll will throw a QueryNotSupported Failure`() = runBlockingTest {
        val resString = async { deviceStorageStringDatasource.getAll(AllObjectsQuery) }
        val resInt = async { deviceStorageIntDatasource.getAll(AllObjectsQuery) }
        val resLong = async { deviceStorageLongDatasource.getAll(AllObjectsQuery) }
        val resFloat = async { deviceStorageFloatDatasource.getAll(AllObjectsQuery) }
        val resBoolean = async { deviceStorageBooleanDatasource.getAll(AllObjectsQuery) }

        val result = awaitAll(resString, resInt, resLong, resFloat, resBoolean)

        assert(result.all<Either<Failure, List<Any>>> { Either.Left(Failure.QueryNotSupported) == it })
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `putting a null object will fail with DataEmpty`() = runBlockingTest {
        val res = deviceStorageStringDatasource.put(KeyQuery(keyString))
        assertEquals(Either.Left(Failure.DataEmpty), res)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `putting data will fail with QueryNotSupported if using a query different than KeyQuery`() =
        runBlockingTest {
            val res = deviceStorageStringDatasource.put(Query())
            assertEquals(Either.Left(Failure.QueryNotSupported), res)
        }

    @Test
    @ExperimentalCoroutinesApi
    fun putString() = runBlockingTest {
        val res = deviceStorageStringDatasource.put(KeyQuery(keyString), expectedString)
        assertEquals(Either.Right(expectedString), res)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun putInt() = runBlockingTest {
        val res = deviceStorageIntDatasource.put(KeyQuery(keyInt), expectedInt)
        assertEquals(Either.Right(expectedInt), res)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun putLong() = runBlockingTest {
        val res = deviceStorageLongDatasource.put(KeyQuery(keyLong), expectedLong)
        assertEquals(Either.Right(expectedLong), res)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun putFloat() = runBlockingTest {
        val res = deviceStorageFloatDatasource.put(KeyQuery(keyFloat), expectedFloat)
        assertEquals(Either.Right(expectedFloat), res)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun putBoolean() = runBlockingTest {
        val res = deviceStorageBooleanDatasource.put(KeyQuery(keyBoolean), expectedBoolean)
        assertEquals(Either.Right(expectedBoolean), res)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `putting an invalid item with fail with UnsupportedOperation`() = runBlockingTest {
        val res = deviceStorageInvalidDatasource.put(KeyQuery(keyInvalid), expectedInvalid)
        assertEquals(Either.Left(Failure.UnsupportedOperation), res)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `All calls to putAll will throw a QueryNotSupported Failure`() = runBlockingTest {
        val resString = async { deviceStorageStringDatasource.putAll(AllObjectsQuery) }
        val resInt = async { deviceStorageIntDatasource.putAll(AllObjectsQuery) }
        val resLong = async { deviceStorageLongDatasource.putAll(AllObjectsQuery) }
        val resFloat = async { deviceStorageFloatDatasource.putAll(AllObjectsQuery) }
        val resBoolean = async { deviceStorageBooleanDatasource.putAll(AllObjectsQuery) }

        val result = awaitAll(resString, resInt, resLong, resFloat, resBoolean)

        assert(result.all<Either<Failure, List<Any>>> { Either.Left(Failure.QueryNotSupported) == it })
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `Delete with Unsupported Query fails with QueryNotSupported`() = runBlockingTest {

        val res = deviceStorageStringDatasource.delete(Query())

        assertEquals(Either.Left(Failure.QueryNotSupported), res)

    }

    @Test
    @ExperimentalCoroutinesApi
    fun `Delete with AllObjectsQuery clears SharedPreferences if there isn't a configured prefix`() =
        runBlockingTest {

            deviceStorageStringDatasource.delete(AllObjectsQuery)

            coVerify(exactly = 1) {
                editor.clear()
            }

        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Delete with AllObjectsQuery only removes items with prefix in the key`() =
        runBlockingTest {

            deviceStorageStringDatasource = DeviceStorageDataSource(sharedPreferences, prefix)

            deviceStorageStringDatasource.delete(AllObjectsQuery)

            coVerify(exactly = sharedPreferencesContent.count { it.key.contains(prefix) }) {
                editor.remove(any())
            }
        }

    @Test
    @ExperimentalCoroutinesApi
    fun `Delete with KeyQuery will remove the specified item`() = runBlockingTest {

        deviceStorageStringDatasource.delete(KeyQuery(keyString))

        coVerify(exactly = 1) {
            editor.remove(keyString)
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `deleteAll just calls delete with AllObjectsQuery`() = runBlockingTest {
        deviceStorageStringDatasource.deleteAll(Query())

        coVerify(exactly = 1) {
            editor.clear()
        }
    }
}