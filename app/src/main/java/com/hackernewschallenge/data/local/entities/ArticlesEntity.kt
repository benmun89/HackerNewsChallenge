package com.hackernewschallenge.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hackernewschallenge.domain.models.Hits

@Entity(tableName = "news")
data class ArticlesEntity(
    @PrimaryKey val storyId: Long = 0,
    val title: String?,
    val author: String?,
    val createdAt: String?,
    val storyURL: String?,
    val isDeleted: Boolean = false,
    val url: String?
)

fun ArticlesEntity.toDomainModel(): Hits {
    return Hits(
        storyId = this.storyId,
        title = this.title ?: "No title",
        author = this.author ?: "Unknown author",
        createdAt = this.createdAt ?: "Unknown time",
        storyTitle = this.title ?: "No story title",
        storyURL = this.storyURL,
        url = this.url
    )
}