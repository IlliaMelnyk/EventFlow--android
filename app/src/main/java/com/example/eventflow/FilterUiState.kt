package com.example.eventflow

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FilterUIState(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val selectedCategory: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)