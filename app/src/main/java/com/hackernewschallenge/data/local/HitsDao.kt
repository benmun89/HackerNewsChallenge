package com.hackernewschallenge.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hackernewschallenge.data.local.entities.ArticlesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticlesEntity>)

    @Query("SELECT * FROM news WHERE isDeleted = 0")
    fun getArticles(): Flow<List<ArticlesEntity>>

    @Delete
    suspend fun deleteArticle(article: ArticlesEntity)

    @Query("UPDATE news SET isDeleted = 1 WHERE storyId = :storyId")
    suspend fun updateArticle(storyId: Long)
}