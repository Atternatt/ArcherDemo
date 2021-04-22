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

package com.m2f.domain.features.posts.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Kotlin representation of a Post json object
 */
@Keep
internal data class PostEntity(
    @SerializedName("ID")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("author")
    val author: AuthorEntity,
    @SerializedName("excerpt")
    val excerpt: String,
    @SerializedName("URL")
    val url: String,
    @SerializedName("featured_image")
    val featuredImage: String?
)