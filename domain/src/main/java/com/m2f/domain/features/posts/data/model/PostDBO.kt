package com.m2f.domain.features.posts.data.model

data class PostDBO(
    val id: Long,
    val title: String,
    val date: String,
    val excerpt: String,
    val url: String,
    val authorAvatarUrl: String,
    val authorName: String,
    val authorUrl: String,
    val featuredImage: String?,
    val numberOfSubscribers: Long
)