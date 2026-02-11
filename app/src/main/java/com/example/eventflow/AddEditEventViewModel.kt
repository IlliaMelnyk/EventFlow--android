package com.example.eventflow

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventflow.firestore.Event
import com.example.eventflow.firestore.FirebaseRepository
import com.example.eventflow.image.EventImageHandler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AddEditEventViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState: MutableStateFlow<AddEditEventUIState> = MutableStateFlow(value = AddEditEventUIState())
    val uiState = _uiState.asStateFlow()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun onAction(action: AddEditEventActions) {
        when (action) {
            is AddEditEventActions.NameChanged -> {
                _uiState.value = _uiState.value.copy(
                    event = _uiState.value.event.copy(name = action.value),
                    nameError = null
                )
            }
            is AddEditEventActions.DescriptionChanged -> {
                _uiState.value = _uiState.value.copy(
                    event = _uiState.value.event.copy(description = action.value),
                    descriptionError = null
                )
            }
            is AddEditEventActions.CategoryChanged -> {
                _uiState.value = _uiState.value.copy(
                    event = _uiState.value.event.copy(category = action.value),
                    categoryError = null
                )
            }
            is AddEditEventActions.DateChanged -> {
                _uiState.value = _uiState.value.copy(
                    event = _uiState.value.event.copy(date = action.value),
                    dateError = null
                )
            }
            is AddEditEventActions.LocationChanged -> {
                _uiState.value =
                    _uiState.value.copy(
                        event = _uiState.value.event.copy(latitude = action.latitude, longitude = action.longitude),
                        locationError = null
                    )
            }
            is AddEditEventActions.Submit -> {
                submitEvent()
            }
        }
    }
    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            val event = repository.getEventById(eventId)
            event?.let {
                _uiState.update { currentState ->
                    currentState.copy(event = it)
                }
            }
        }
    }
    private fun submitEvent() {
        val currentState = _uiState.value
        val event = currentState.event.copy(ownerId = currentUserId)

        val nameError = if (event.name.isBlank()) R.string.error_empty else null
        val descriptionError = if (event.description.isBlank()) R.string.error_empty else null
        val categoryError = if (event.category.isBlank()) R.string.error_empty else null
        val dateError = if (event.date == null || event.date <= 0.0) R.string.error_empty else null
        val locationError = if (event.latitude == null || event.latitude <= 0.0 || event.longitude == null || event.longitude <= 0.0) R.string.error_empty else null

        if (listOf(nameError, descriptionError, categoryError, dateError, locationError).all { it == null }) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(loading = true)

                if (!event.id.isNullOrBlank()) {
                    repository.updateEvent(event)
                } else {
                    repository.addEvent(event)
                }

                _uiState.value = _uiState.value.copy(
                    loading = false,
                    eventSaved = true
                )
            }
        } else {
            _uiState.value = _uiState.value.copy(
                nameError = nameError,
                descriptionError = descriptionError,
                categoryError = categoryError,
                dateError = dateError,
                locationError = locationError
            )
        }
        Log.d("SubmitEvent", "Submitting event with ID: '${event.id}'")
    }
    fun setImageUrl(url: String) {
        _uiState.value = _uiState.value.copy(
            event = _uiState.value.event.copy(imageUrl = url)
        )
    }

    fun uploadImage(uri: Uri, getFile: (Uri) -> File?) {
        viewModelScope.launch {
            val imageUrl = EventImageHandler.uploadImageToCloudinary(uri, getFile)
            if (imageUrl != null) {
                setImageUrl(imageUrl)
            } else {
                _uiState.value = _uiState.value.copy(
                    imageUploadError = R.string.error_upload_failed
                )
            }
            Log.d("ImageUpload", "URL: $imageUrl")
        }
    }

    fun getFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            tempFile.outputStream().use { fileOut ->
                inputStream?.copyTo(fileOut)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
