package com.example.eventflow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor() : ViewModel(), MapActions{

    private val _mapScreenUIState: MutableStateFlow<MapScreenUIState> =
        MutableStateFlow(value = MapScreenUIState())

    val mapScreenUIState = _mapScreenUIState.asStateFlow()

    override fun locationChanged(latitude: Double, longitude: Double) {
        _mapScreenUIState.value = _mapScreenUIState.value.copy(
            locationChanged = true,
            latitude = latitude,
            longitude = longitude)
    }
}