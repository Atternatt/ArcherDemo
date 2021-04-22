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

package com.m2f.domain.helpers

import okhttp3.mockwebserver.MockResponse
import java.net.HttpURLConnection

/**
 * Interface to define a strategy pattern for api calls
 */
sealed fun interface MockApiCall {
    fun generateResponse(): MockResponse
}

object PostsOkApiCall : MockApiCall {
    override fun generateResponse(): MockResponse =
        MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(readContentFromFilePath("posts_ok.json"))
}

object PostsKoApiCall : MockApiCall {
    override fun generateResponse(): MockResponse =
        MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            .setBody(readContentFromFilePath("posts_ko.json"))
}

object NumSubscribersOkApiCall : MockApiCall {
    override fun generateResponse(): MockResponse =
        MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(readContentFromFilePath("num_subscribers_ok.json"))
}

object NumSubscribersKoApiCall : MockApiCall {
    override fun generateResponse(): MockResponse =
        MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            .setBody(readContentFromFilePath("num_subscribers_ko.json"))
}

object EmptyKoApiCall : MockApiCall {
    override fun generateResponse(): MockResponse =
        MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
}