package com.example.eventflow

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventflow.constants.Constants
import com.example.eventflow.model.Location
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilterUIState())
    val uiState: StateFlow<FilterUIState> = _uiState.asStateFlow()

    init {
        loadFromSavedState()
    }

    private fun loadFromSavedState() {
        val startDate = savedStateHandle.get<Long?>("filter_startDate")
        val endDate = savedStateHandle.get<Long?>("filter_endDate")
        val category = savedStateHandle.get<String?>("filter_category")
        val latitude = savedStateHandle.get<Double?>("filter_latitude")
        val longitude = savedStateHandle.get<Double?>("filter_longitude")

        _uiState.value = FilterUIState(
            startDate = startDate,
            endDate = endDate,
            selectedCategory = category,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun updateFromAppliedFilter(filter: FilterUIState) {
        _uiState.value = filter

        savedStateHandle["filter_startDate"] = filter.startDate
        savedStateHandle["filter_endDate"] = filter.endDate
        savedStateHandle["filter_category"] = filter.selectedCategory
        savedStateHandle["filter_latitude"] = filter.latitude
        savedStateHandle["filter_longitude"] = filter.longitude

        Log.d("FilterViewModel", "Applying filter: $filter")
    }

    fun onAction(action: FilterActions) {
        when (action) {
            is FilterActions.StartDateChanged -> _uiState.update { it.copy(startDate = action.date) }
            is FilterActions.EndDateChanged -> _uiState.update { it.copy(endDate = action.date) }
            is FilterActions.CategoryChanged -> _uiState.update { it.copy(selectedCategory = action.category) }
            is FilterActions.LocationChanged -> _uiState.update { it.copy(latitude = action.latitude, longitude = action.longitude) }
            FilterActions.Submit -> submitFilters()
        }
    }

    private fun submitFilters() {
        val state = _uiState.value
        savedStateHandle["filter_startDate"] = state.startDate
        savedStateHandle["filter_endDate"] = state.endDate
        savedStateHandle["filter_category"] = state.selectedCategory
        savedStateHandle["filter_latitude"] = state.latitude
        savedStateHandle["filter_longitude"] = state.longitude
    }
}