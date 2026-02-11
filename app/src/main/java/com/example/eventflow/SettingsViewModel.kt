package com.example.eventflow

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventflow.ui.theme.elements.LanguagePreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languagePreference: LanguagePreference,
    application: Application
) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var displayName by mutableStateOf(currentUser?.displayName ?: "")
        private set

    var newDisplayName by mutableStateOf("")
    var updateMessage by mutableStateOf<String?>(null)

    val selectedLanguage = languagePreference.languageFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "cs")

    fun changeLanguage(newLang: String) {
        viewModelScope.launch {
            languagePreference.saveLanguage(newLang)
            val intent = Intent(getApplication(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            getApplication<Application>().startActivity(intent)
        }
    }

    fun onDisplayNameChange(value: String) {
        newDisplayName = value
    }

    fun updateDisplayName() {
        val user = currentUser
        if (user != null && newDisplayName.isNotBlank()) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnSuccessListener {
                    displayName = newDisplayName
                    updateMessage = "Jméno bylo úspěšně změněno"
                }
                .addOnFailureListener {
                    updateMessage = "Chyba při změně jména"
                }
        }
    }

    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        onLogout()
    }
}
