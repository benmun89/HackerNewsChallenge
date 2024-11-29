package com.hackernewschallenge.domain.usecase

import com.hackernewschallenge.domain.models.Hits
import com.hackernewschallenge.domain.repository.NewsRepository

class FetchNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(): List<Hits> = repository.fetchNews()
}