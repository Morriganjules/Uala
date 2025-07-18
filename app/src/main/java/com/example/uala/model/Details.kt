package com.example.uala.model

data class Details(
    val title: String,
    val extract: String,
    val thumbnail: Thumbnail? = null
)

data class Thumbnail(
    val source: String
)

