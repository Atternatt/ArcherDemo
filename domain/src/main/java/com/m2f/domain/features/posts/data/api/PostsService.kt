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

package com.m2f.domain.features.posts.data.api

import com.m2f.domain.features.posts.data.model.PostListResponse
import com.m2f.domain.features.posts.data.model.SubscriptionCountEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Representation of the Post-related api calls
 */
internal interface PostsService {

    /**
     * Get a list of posts
     * @param number the number of posts to show. Max allowed = 100
     */
    @GET("sites/discover.wordpress.com/posts")
    suspend fun getPosts(@Query("numer") number: Int = 10): PostListResponse

    /**
     * Get the list of subscribers taken the host url of an author.
     * @param url the url without the scheme
     */
    @GET("sites/{url}")
    suspend fun getSubscribers(@Path("url") url: String): SubscriptionCountEntity
}