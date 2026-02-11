package com.example.eventflow

sealed interface SignUpAction {
    data class EmailChanged(val value: String) : SignUpAction
    data class PasswordChanged(val value: String) : SignUpAction
    data class DisplayNameChanged(val value: String) : SignUpAction
    object Submit : SignUpAction
}