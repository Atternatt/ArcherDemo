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
import com.m2f.arch.data.datasource.DeleteDataSource
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.datasource.PutDataSource
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.query.AllObjectsQuery
import com.m2f.arch.data.query.KeyQuery
import com.m2f.arch.data.query.Query

/**
 * This DataSource uses a [SharedPreferences] to store data.
 * It supports the same value types that a SharedPreferences does: Boolean, Int, Float, Long, String
 */
class DeviceStorageDataSource<T>(
    private val sharedPreferences: SharedPreferences,
    private val prefix: String = ""
) : GetDataSource<T>, PutDataSource<T>, DeleteDataSource {

    override suspend fun get(query: Query): Either<Failure, T> {
        return when (query) {
            is KeyQuery -> {
                val key = addPrefixTo(query.key)
                if (!sharedPreferences.contains(key)) {
                    Either.Left(Failure.DataNotFound)
                } else {
                    Either.Right(sharedPreferences.all[key] as T)
                }
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }
    }

    override suspend fun getAll(query: Query): Either<Failure, List<T>> =
        Either.Left(Failure.QueryNotSupported)

    override suspend fun put(query: Query, value: T?): Either<Failure, T> {
        return when (query) {
            is KeyQuery -> {
                value?.let {
                    val key = addPrefixTo(query.key)
                    val editor = sharedPreferences.edit()
                    val result = when (value) {
                        is String -> editor.putString(key, value).apply().let { true }
                        is Boolean -> editor.putBoolean(key, value).apply().let { true }
                        is Float -> editor.putFloat(key, value).apply().let { true }
                        is Int -> editor.putInt(key, value).apply().let { true }
                        is Long -> editor.putLong(key, value).apply().let { true }
                        else -> false
                    }
                    if (result) Either.Right<T>(it) else Either.Left(Failure.UnsupportedOperation)
                }
                    ?: Either.Left(Failure.DataEmpty)
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }
    }

    override suspend fun putAll(query: Query, value: List<T>?): Either<Failure, List<T>> =
        Either.Left(Failure.QueryNotSupported)

    override suspend fun delete(query: Query): Either<Failure, Unit> {
        return when (query) {
            is AllObjectsQuery -> {
                with(sharedPreferences.edit()) {
                    if (prefix.isNotEmpty()) {
                        sharedPreferences.all.keys.filter { it.contains(prefix) }
                            .forEach { remove(it) }
                    } else {
                        clear()
                    }
                    apply()
                }
                Either.Right(Unit)
            }
            is KeyQuery -> {
                sharedPreferences.edit()
                    .remove(addPrefixTo(query.key))
                    .apply()
                Either.Right(Unit)
            }
            else -> Either.Left(Failure.QueryNotSupported)
        }
    }

    override suspend fun deleteAll(query: Query): Either<Failure, Unit> = delete(AllObjectsQuery)

    private fun addPrefixTo(key: String) = if (prefix.isEmpty()) key else "$prefix.$key"
}