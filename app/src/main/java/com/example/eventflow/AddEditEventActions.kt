package com.example.eventflow

sealed class AddEditEventActions {
    data class NameChanged(val value: String) : AddEditEventActions()
    data class DescriptionChanged(val value: String) : AddEditEventActions()
    data class CategoryChanged(val value: String) : AddEditEventActions()
    data class DateChanged(val value: Long?) : AddEditEventActions()
    data class LocationChanged(val latitude: Double?, val longitude: Double?): AddEditEventActions()
    object Submit : AddEditEventActions()
}
