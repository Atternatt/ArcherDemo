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

package com.m2f.domain.features.posts.mapper

import com.m2f.arch.data.mapper.Mapper
import com.m2f.domain.features.posts.data.model.SubscriptionCountEntity
import com.m2f.domain.features.posts.model.SubscriptionCount

internal object SubscriptionCountEntityToSubscriptionCountMapper :
    Mapper<SubscriptionCountEntity, SubscriptionCount> {
    override fun map(from: SubscriptionCountEntity): SubscriptionCount = with(from) {
        SubscriptionCount(count)
    }
}