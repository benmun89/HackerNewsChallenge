package com.hackernewschallenge.domain.repository

import com.hackernewschallenge.domain.models.Hits

interface NewsRepository {
        suspend fun fetchNews(): List<Hits>
}