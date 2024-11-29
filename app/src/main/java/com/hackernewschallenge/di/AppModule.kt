package com.hackernewschallenge.di

import com.hackernewschallenge.data.api.NewsApi
import com.hackernewschallenge.data.repository.NewsRepositoryImpl
import com.hackernewschallenge.domain.repository.NewsRepository
import com.hackernewschallenge.domain.usecase.FetchNewsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideNewsApi(): NewsApi {
        return Retrofit.Builder()
            .baseUrl("https://hn.algolia.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    fun provideNewsRepository(
        api: NewsApi,
    ): NewsRepository {
        return NewsRepositoryImpl(api)
    }

    @Provides
    fun provideFetchNewsUseCase(repository: NewsRepository): FetchNewsUseCase {
        return FetchNewsUseCase(repository)
    }
}