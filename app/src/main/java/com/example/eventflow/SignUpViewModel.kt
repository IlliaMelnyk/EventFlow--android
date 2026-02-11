package com.example.eventflow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var uiState by mutableStateOf(SignUpUIState())
        private set

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.EmailChanged -> uiState = uiState.copy(email = action.value)
            is SignUpAction.PasswordChanged -> uiState = uiState.copy(password = action.value)
            is SignUpAction.DisplayNameChanged -> uiState = uiState.copy(displayName = action.value)
            SignUpAction.Submit -> signUp()
        }
    }

    private fun signUp() {
        if (uiState.email.isBlank() || uiState.password.length < 6 || uiState.displayName.isBlank()) {
            uiState = uiState.copy(errorMessage = "Zadej všechny údaje a heslo alespoň 6 znaků")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        auth.createUserWithEmailAndPassword(uiState.email, uiState.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(uiState.displayName)
                        .build()
                    user?.updateProfile(profileUpdates)
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                } else {
                    uiState = uiState.copy(isLoading = false, errorMessage = task.exception?.message)
                }
            }
    }
}