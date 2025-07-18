package com.example.uala.service.citiesModule

import com.example.uala.model.Details
import javax.inject.Inject

class WikipediaRepository @Inject constructor(
    private val wikipediaApiService: WikipediaApiService
) {
    suspend fun getCityDetails(cityName: String): Details {
        return wikipediaApiService.getDetails(cityName)
    }
}