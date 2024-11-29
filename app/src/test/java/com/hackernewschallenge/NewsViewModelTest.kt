package com.hackernewschallenge.presentation.viewmodel

import com.hackernewschallenge.domain.models.Hits
import com.hackernewschallenge.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

import kotlinx.coroutines.flow.first

@ExperimentalCoroutinesApi
class NewsViewModelTest {

    @Mock
    lateinit var mockRepository: NewsRepository

    private lateinit var viewModel: NewsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = NewsViewModel(mockRepository)

        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `should update news state with fetched data`() = runTest {
        val hitsList = listOf(
            Hits(1, "Test Title", "", "t", "ben", "https://image.url", "", false),
            Hits(2, "Another Title", "true", "t", "alice", "https://image2.url", "", true)
        )

        `when`(mockRepository.fetchNews()).thenReturn(hitsList)

        viewModel.loadNews()


        val news = viewModel.news.first()
        assertEquals(hitsList, news)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}

