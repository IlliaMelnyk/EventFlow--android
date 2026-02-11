package com.example.eventflow

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventflow.database.Events
import com.example.eventflow.database.IEventsLocalRepository
import com.example.eventflow.firestore.FirebaseRepository
import com.example.eventflow.firestore.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class HomeFavouriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository,
    private val localRepository: IEventsLocalRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _filteredEvents = MutableStateFlow<List<Event>>(emptyList())
    val filteredEvents: StateFlow<List<Event>> = _filteredEvents

    val favorites: StateFlow<Set<String>> = localRepository.getAll()
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Aktuální filtry
    private var currentFilter: FilterUIState? = null

    init {
        readFiltersFromSavedState()
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            val loadedEvents = firebaseRepository.getEvents()
            _events.value = loadedEvents
            applyCurrentFilter()
        }
    }

    private fun readFiltersFromSavedState() {
        val startDate = savedStateHandle.get<Long?>("filter_startDate")
        val endDate = savedStateHandle.get<Long?>("filter_endDate")
        val category = savedStateHandle.get<String?>("filter_category")
        val latitude = savedStateHandle.get<Double?>("filter_latitude")
        val longitude = savedStateHandle.get<Double?>("filter_longitude")

        currentFilter = FilterUIState(
            startDate = startDate,
            endDate = endDate,
            selectedCategory = category,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun updateFilter(filter: FilterUIState) {
        currentFilter = filter
        Log.d("Filter", "updateFilter called with: $filter")
        applyCurrentFilter()
    }

    private fun applyCurrentFilter() {
        val filter = currentFilter
        val allEvents = _events.value

        val filtered = allEvents.filter { event ->
            Log.d("Filter", "Filtering event: ${event.name}")
            val matchesDate = filter?.let {
                val eventDate = event.date
                eventDate != null &&
                        (it.startDate == null || eventDate >= it.startDate) &&
                        (it.endDate == null || eventDate <= it.endDate)
            } ?: true

            val matchesCategory = filter?.selectedCategory?.let {
                it == event.category
            } ?: true

            val matchesLocation = filter?.let {
                if (it.latitude != null && it.longitude != null && event.latitude != null && event.longitude != null) {
                    val distance = haversineDistance(it.latitude, it.longitude, event.latitude, event.longitude)
                    distance <= 30.0
                } else true
            } ?: true

            matchesDate && matchesCategory && matchesLocation
        }
        Log.d("Filter", "Filter: $filter")
        Log.d("Filter", "All events: ${_events.value.size}, filtered: ${filtered.size}")

        _filteredEvents.value = filtered
    }

    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            val currentFavorites = favorites.value
            val event = _events.value.find { it.id == eventId }
            event?.let {
                if (currentFavorites.contains(eventId)) {
                    localRepository.delete(it.toRoomEntity())
                } else {
                    localRepository.insert(it.toRoomEntity())
                }
            }
        }
    }

    private fun haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private fun Event.toRoomEntity(): Events = Events(
        id = id,
        name = name,
        category = category,
        description = description,
        latitude = latitude,
        longitude = longitude,
        date = date,
        imageUrl = imageUrl
    )
}