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

package com.m2f.domain.features.posts.model

data class Post(
    val id: Long,
    val title: String,
    val date: String,
    val excerpt: String,
    val url: String,
    val authorAvatarUrl: String,
    val authorName: String,
    val authorUrl: String,
    val featuredImage: String?,
    val numberOfSubscribers: Long = 0
)