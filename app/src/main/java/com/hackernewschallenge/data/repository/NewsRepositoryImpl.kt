package com.hackernewschallenge.data.repository

import android.util.Log
import com.hackernewschallenge.data.api.NewsApi
import com.hackernewschallenge.domain.models.Hits
import com.hackernewschallenge.domain.models.toDomainModel
import com.hackernewschallenge.domain.repository.NewsRepository

class NewsRepositoryImpl(private val api: NewsApi) : NewsRepository {
    override suspend fun fetchNews(): List<Hits> {
        return try {
            val response = api.getNews("android")
            if (response.isSuccessful) {
                val hits = response.body()?.hits?.map { it.toDomainModel() } ?: emptyList()
                hits
            } else {
                Log.e("NewsRepositoryImpl", "API call failed: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("NewsRepositoryImpl", "Error fetching news, using cached data", e)
            emptyList()
        }
    }
}
