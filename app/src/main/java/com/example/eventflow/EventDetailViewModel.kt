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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val localRepository: IEventsLocalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String? = savedStateHandle.get<String>("id")?.toString()

    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState

    val favorites: StateFlow<Set<String>> = localRepository.getAll()
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        loadEvent()
    }

    private fun loadEvent() {
        viewModelScope.launch {
            if (eventId == null) {
                _uiState.value = EventDetailUiState.Error("Event ID is null")
                return@launch
            }

            try {
                val event = firebaseRepository.getEventById(eventId)
                if (event != null) {
                    _uiState.value = EventDetailUiState.Success(event)
                } else {
                    _uiState.value = EventDetailUiState.Error("Event not found")
                }
            } catch (e: Exception) {
                _uiState.value = EventDetailUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun formatDate(timestamp: Long?): String {
        val sdf = SimpleDateFormat("dd. MMMM yyyy", Locale.getDefault())
        return sdf.format(timestamp?.let { Date(it) })
    }

    fun toggleFavorite(event: Event) {
        viewModelScope.launch {
            val currentFavorites = favorites.value
            if (currentFavorites.contains(event.id)) {
                localRepository.delete(event.toRoomEntity())
            } else {
                localRepository.insert(event.toRoomEntity())
            }
        }
    }

    fun deleteEvent(eventId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                firebaseRepository.deleteEvent(eventId)
                onSuccess()
            } catch (e: Exception) {
                Log.e("EventDetail", "Error deleting event", e)
            }
        }
    }

    // Převod Event <-> Events (Room) podle tvého předchozího kódu
    private fun Event.toRoomEntity() = Events(
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