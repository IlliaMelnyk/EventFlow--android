package com.example.eventflow.navigation

import kotlinx.serialization.Serializable

@Serializable
data class MapScreenDestination(
    var latitude: Double? = null,
    var longitude: Double? = null
)