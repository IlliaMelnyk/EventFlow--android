package com.example.eventflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUIState())
    val uiState: StateFlow<ForgotPasswordUIState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun sendPasswordReset() {
        val email = _uiState.value.email

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Zadejte email") }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, errorMessage = null, successMessage = null)
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Email pro obnovení hesla byl odeslán.",
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Chyba: ${task.exception?.message ?: "Neznámá chyba"}",
                            successMessage = null
                        )
                    }
                }
            }
    }
}