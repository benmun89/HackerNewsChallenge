package com.hackernewschallenge.domain.models

import com.google.gson.annotations.SerializedName

data class Hits(
    @SerializedName("story_id") val storyId: Long,
    val title: String?,
    val author: String?,
    val createdAt: String?,
    @SerializedName("story_title") val storyTitle: String?,
    val storyUrl: String?,
    val isDeleted: Boolean = false
)