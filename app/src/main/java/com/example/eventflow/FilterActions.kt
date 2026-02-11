package com.example.eventflow

sealed class FilterActions {
    data class StartDateChanged(val date: Long?) : FilterActions()
    data class EndDateChanged(val date: Long?) : FilterActions()
    data class CategoryChanged(val category: String?) : FilterActions()
    data class LocationChanged(val latitude: Double?, val longitude: Double?) : FilterActions()
    object Submit : FilterActions()
}