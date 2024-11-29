package com.hackernewschallenge.data.repository

import android.util.Log
import com.hackernewschallenge.data.api.NewsApi
import com.hackernewschallenge.data.local.HitsDao
import com.hackernewschallenge.data.local.entities.toDomainModel
import com.hackernewschallenge.domain.models.Hits
import com.hackernewschallenge.domain.models.toDomainModel
import com.hackernewschallenge.domain.models.toEntity
import com.hackernewschallenge.domain.repository.NewsRepository
import kotlinx.coroutines.flow.first

class NewsRepositoryImpl(
    private val api: NewsApi,
    private val hitsDao: HitsDao
) : NewsRepository {

    override suspend fun fetchNews(): List<Hits> {
        return try {
            val response = api.getNews("android")
            if (response.isSuccessful) {
                val hits = response.body()?.hits?.map { it.toDomainModel() } ?: emptyList()

                hitsDao.insertArticles(hits.map { it.toEntity() })
                hits
            } else {
                Log.e("NewsRepositoryImpl", "API call failed: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {

            Log.e("NewsRepositoryImpl", "Error fetching news, using cached data", e)


            val cachedArticles = hitsDao.getArticles()
                .first()
                .filter { !it.isDeleted }
                .map { it.toDomainModel() }

            cachedArticles
        }
    }

    override suspend fun fetchNewsFromDb(): List<Hits> {
        val cachedArticles = hitsDao.getArticles()
            .first()
            .filter { !it.isDeleted }
            .map { it.toDomainModel() }

        return cachedArticles
    }

    override suspend fun removeNewsItem(post: Hits) {

        Log.d("NewsRepository", "Removing news item: ${post.storyId}")

        hitsDao.updateArticle(post.storyId)
    }

    override suspend fun insertNews(newsToInsert: List<Hits>) {
        hitsDao.insertArticles(newsToInsert.map { it.toEntity() })
    }
}

