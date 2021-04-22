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

package com.m2f.domain.features.posts.viewmodel

import androidx.lifecycle.viewModelScope
import com.m2f.domain.base.BaseViewModel
import com.m2f.domain.base.ViewModelState
import com.m2f.domain.features.posts.model.Post
import com.m2f.domain.features.posts.query.PostsQuery
import com.m2f.domain.features.posts.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that manages all the logic to retrieve post lists
 *
 * It provides a [loadPosts] method to requests Posts List.
 *
 */
@HiltViewModel
class PostListViewModel @Inject constructor(private val getPostsUseCase: GetPostsUseCase) :
    BaseViewModel<List<Post>>() {

    /**
     * [Job] executed to retrieve post list it will be canceled and
     * relaunched for each call to [loadPosts].
     */
    private var job: Job? = null

    /**
     * Load the posts. It uses a job that it's cancelled each time we want to call this method.
     */
    fun loadPosts(forceRefresh: Boolean = false) {
        job?.cancel()
        postViewState(ViewModelState.Loading(true))
        job = viewModelScope.launch {
            getPostsUseCase(PostsQuery(forceRefresh = forceRefresh, number = 25))
                .handleResult()
        }
    }

    override fun handleSuccess(data: List<Post>): ViewModelState<List<Post>> {
        return if (data.isEmpty()) {
            ViewModelState.Empty
        } else {
            ViewModelState.Success(data)
        }
    }
}