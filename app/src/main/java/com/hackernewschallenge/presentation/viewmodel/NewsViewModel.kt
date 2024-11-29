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
    val hasError: StateFlow<Boolean> = _hasError

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
}