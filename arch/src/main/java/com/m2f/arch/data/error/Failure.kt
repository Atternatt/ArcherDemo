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

package com.m2f.arch.data.error

/**
 * class representation for a failure
 */
sealed class Failure {

    /** Data can't be found */
    object DataNotFound : Failure()

    /** Data is empty */
    object DataEmpty : Failure()

    /**No connection*/
    object NoConnection : Failure()

    /**Server Error*/
    object ServerError : Failure()

    /** The query is not supported */
    object QueryNotSupported : Failure()

    /** Data is not valid*/
    data class InvalidObject(val message: String) : Failure()

    object UnsupportedOperation : Failure()

    data class Unknown(val exception: Exception) : Failure()
}