package com.example.eventflow

data class LoginUIState(
    val email: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)