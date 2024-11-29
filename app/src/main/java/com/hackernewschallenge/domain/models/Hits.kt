package com.hackernewschallenge.domain.models

import com.google.gson.annotations.SerializedName
import com.hackernewschallenge.data.local.entities.ArticlesEntity

data class Hits(
    @SerializedName("story_id") val storyId: Long,
    val title: String?,
    val author: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("story_title") val storyTitle: String?,
    @SerializedName ("story_url") val storyURL: String?,
    val url: String?,
    val isDeleted: Boolean = false
)

fun Hits.toDomainModel(): Hits {
    return Hits(
        storyId = this.storyId,
        title = this.storyTitle ?: this.title,
        author = this.author,
        createdAt = this.createdAt,
        storyTitle = this.storyTitle ?: "No story title",
        storyURL = this.storyURL ?: this.url,
        url = this.storyURL ?: this.url,
    )
}

fun Hits.toEntity(): ArticlesEntity {
    return ArticlesEntity(
        storyId = this.storyId,
        title = this.title,
        author = this.author,
        createdAt = this.createdAt,
        isDeleted = false,
        storyURL = this.storyURL ?: this.url,
        url = this.storyURL ?: this.url,
    )
}
