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

fun Hits.toDomainModel(): Hits {
    return Hits(
        storyId = this.storyId,
        title = this.storyTitle ?: this.title,
        author = this.author,
        createdAt = this.createdAt,
        storyTitle = this.storyTitle ?: "No story title",
        storyUrl = this.storyUrl
    )
}