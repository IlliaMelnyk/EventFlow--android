package com.example.eventflow

import com.example.eventflow.firestore.Event

data class AddEditEventUIState(
    val event: Event = Event(),
    val loading: Boolean = false,
    val nameError: Int? = null,
    val descriptionError: Int? = null,
    val categoryError: Int? = null,
    val dateError: Int? = null,
    val locationError: Int? = null,
    val imageUploadError: Int? = null,
    val eventSaved: Boolean = false
)