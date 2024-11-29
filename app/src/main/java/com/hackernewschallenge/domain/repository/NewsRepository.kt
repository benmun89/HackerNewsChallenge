package com.hackernewschallenge.domain.repository

import com.hackernewschallenge.domain.models.Hits

interface NewsRepository {
        suspend fun fetchNews(): List<Hits>
        suspend fun fetchNewsFromDb(): List<Hits>
        suspend fun removeNewsItem(post: Hits)
        suspend fun insertNews(newsToInsert: List<Hits>)

}