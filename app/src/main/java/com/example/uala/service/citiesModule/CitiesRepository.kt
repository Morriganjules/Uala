package com.example.uala.service.citiesModule

import com.example.uala.model.City
import javax.inject.Inject

class CitiesRepository @Inject constructor(
    private val CitiesApiService: CitiesApiService
) {
    suspend fun getCities(): List<City> {
        return CitiesApiService.getCities()
    }
}