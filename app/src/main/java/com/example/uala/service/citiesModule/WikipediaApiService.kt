package com.example.uala.service.citiesModule

import com.example.uala.model.Details
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaApiService {
    @GET("api/rest_v1/page/summary/{title}")
    suspend fun getDetails(@Path("title") title: String): Details
}