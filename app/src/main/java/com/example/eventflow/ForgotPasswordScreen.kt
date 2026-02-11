package com.example.eventflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.eventflow.navigation.INavigationRouter

@Composable
fun ForgotPasswordScreen(
    navigation: INavigationRouter
) {
    val viewModel: ForgotPasswordViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    ForgotPasswordScreenContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onSendClick = viewModel::sendPasswordReset,
        onBackClick = { navigation.returnBack() }
    )
}

@Composable
fun ForgotPasswordScreenContent(
    state: ForgotPasswordUIState,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Obnova hesla",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSendClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text("Odeslat email")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackClick) {
            Text("Zpět na přihlášení")
        }

        if (state.successMessage != null) {
            Text(text = state.successMessage, color = Color.Green)
        }

        if (state.errorMessage != null) {
            Text(text = state.errorMessage, color = Color.Red)
        }
    }
}