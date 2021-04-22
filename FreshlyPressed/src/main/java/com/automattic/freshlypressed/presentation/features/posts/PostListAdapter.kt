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

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.automattic.freshlypressed.R
import com.automattic.freshlypressed.databinding.PostListFragmentItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.m2f.domain.features.posts.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean = oldItem.id == newItem.id
}

/**
 * A [RecyclerView.Adapter] of [Post] objects
 */
class PostListAdapter(private val onItemClick: (Post) -> Unit) :
    ListAdapter<Post, PostListAdapter.ViewHolder>(diffCallback), CoroutineScope {

    override val coroutineContext: CoroutineContext = Job()

    inner class ViewHolder(private val binding: PostListFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @ExperimentalCoroutinesApi
        fun bind(item: Post) = with(binding) {
            title.text = Html.fromHtml(item.title)
            summary.text = Html.fromHtml(item.excerpt)
            authorName.text = item.authorName
            subscribersCount.text = item.numberOfSubscribers.toString()

            launch(Dispatchers.Main) {
                val color: Int? = suspendCancellableCoroutine { continuation ->
                    Glide.with(image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(item.featuredImage)
                        .listener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                model: Any,
                                target: Target<Bitmap>,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                launch(Dispatchers.Default) {
                                    val palette = Palette.from(resource).generate()
                                    val color = palette.getDarkVibrantColor(
                                        ContextCompat.getColor(
                                            itemView.context,
                                            R.color.brandPrimaryBase
                                        )
                                    )
                                    continuation.resume(color)
                                }
                                return false
                            }
                        })
                        .into(image)
                }

                color?.also {
                    binding.titleBg.backgroundTintList = ColorStateList.valueOf(it)
                }
            }

            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            PostListFragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}