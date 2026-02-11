package com.example.eventflow

import com.example.eventflow.database.Events
import com.example.eventflow.firestore.Event

data class HomeFavouriteScreenUiState(
    val events: List<Event> = emptyList(),
    val favoriteEvents: List<Events> = emptyList()
)