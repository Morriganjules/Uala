package com.example.uala.service.citiesModule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("cities")
    fun provideCitiesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/hernan-uala/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("wikipedia")
    fun provideWikipediaRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCitiesApiService(@Named("cities") retrofit: Retrofit): CitiesApiService {
        return retrofit.create(CitiesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWikipediaApiService(@Named("wikipedia") retrofit: Retrofit): WikipediaApiService {
        return retrofit.create(WikipediaApiService::class.java)
    }
}
