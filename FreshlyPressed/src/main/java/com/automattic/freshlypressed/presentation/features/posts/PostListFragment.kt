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
package com.automattic.freshlypressed.presentation.features.posts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.automattic.freshlypressed.databinding.FragmentPostListBinding
import com.automattic.freshlypressed.presentation.components.LoadingDialog
import com.m2f.domain.base.FailureType
import com.m2f.domain.base.Render
import com.m2f.domain.features.posts.model.Post
import com.m2f.domain.features.posts.viewmodel.PostListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Fragment to display a list of [Post] objects
 */
@AndroidEntryPoint
class PostListFragment : Fragment() {

    private lateinit var binding: FragmentPostListBinding

    private val postsViewModel: PostListViewModel by viewModels()

    private val adapter = PostListAdapter { post ->
        Intent().also {
            it.action = Intent.ACTION_VIEW
            it.data = Uri.parse(post.url)
            activity?.startActivity(it)
        }
    }

    private val loadingDialog: LoadingDialog by lazy { LoadingDialog() }

    private val stateRender = object : Render<List<Post>> {

        override fun onSucces(data: List<Post>) {
            Log.d("LIST_SIZE", data.size.toString())
            adapter.submitList(data)
        }

        override fun onError(failure: FailureType) {
            Log.e("LIST_ERROR", failure.toString())
        }

        override fun onEmpty() {
            super.onEmpty()
            Log.d("LIST_SIZE", "Empty")
        }

        override fun onLoading(isLoading: Boolean) {
            if (isLoading) {
                if (!loadingDialog.isVisible) {
                    loadingDialog.show(childFragmentManager, "loading")
                }
            } else {
                if (loadingDialog.isVisible) {
                    loadingDialog.dismiss()
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostListBinding.inflate(inflater, container, false).apply {
            list.adapter = adapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postsViewModel.state.observe(viewLifecycleOwner, stateRender::render)
        postsViewModel.loadPosts(forceRefresh = false)
    }
}