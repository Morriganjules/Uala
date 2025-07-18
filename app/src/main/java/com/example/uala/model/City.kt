package com.example.uala.model

import com.google.gson.annotations.SerializedName

data class CityUiModel(
    val city: City,
    val isFavorite: Boolean
)

data class City(
    @SerializedName("_id")
    val id: Long,
    val name: String,
    val country: String,
    val coord: Coord
)

data class Coord(
    val lon: Double,
    val lat: Double
)


