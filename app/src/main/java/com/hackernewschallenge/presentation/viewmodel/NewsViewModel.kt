package com.hackernewschallenge.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackernewschallenge.domain.models.Hits
import com.hackernewschallenge.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _news = MutableStateFlow<List<Hits>>(emptyList())
    val news: StateFlow<List<Hits>> = _news

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _hasError = MutableStateFlow(false)

    fun loadNews() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val fetchedNews = repository.fetchNews()
                _news.value = fetchedNews

                _hasError.value = false
            } catch (e: Exception) {
                _hasError.value = true
                Log.e("NewsViewModel", "Error loading news", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshNews() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            _hasError.value = false

            try {
                if (_news.value.isEmpty()) {
                    val freshNewsFromApi = repository.fetchNews()
                    repository.insertNews(freshNewsFromApi)

                    val validNews = freshNewsFromApi.filterNot { it.isDeleted }

                    _news.value = validNews
                } else {
                    val freshNewsFromDb = repository.fetchNewsFromDb().filterNot { it.isDeleted }
                    _news.value = freshNewsFromDb
                }

            } catch (e: Exception) {
                _hasError.value = true
                Log.e("NewsViewModel", "Error refreshing news", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun removeNewsItem(post: Hits) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.removeNewsItem(post)
                val updatedNews = repository.fetchNewsFromDb()
                _news.value = updatedNews.filterNot { it.isDeleted }
                Log.d("NewsViewModel", "Updated news list: ${_news.value}")
            } catch (e: Exception) {
                _hasError.value = true
                Log.e("NewsViewModel", "Error removing item", e)
            }
        }
    }
}