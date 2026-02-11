package com.example.eventflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eventflow.navigation.INavigationRouter


@Composable
fun SignUpScreen(navRouter: INavigationRouter) {

    val viewModel = hiltViewModel<SignUpViewModel>()
    val state = viewModel.uiState

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navRouter.navigateToHomeScreen()
        }
    }

    SignUpScreenContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = { navRouter.returnBack() }
    )
}
@Composable
fun SignUpScreenContent(
    state: SignUpUIState,
    onAction: (SignUpAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(id = R.string.registration), style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.displayName,
            onValueChange = { onAction(SignUpAction.DisplayNameChanged(it)) },
            label = { Text(stringResource(id = R.string.new_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = { onAction(SignUpAction.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = { onAction(SignUpAction.PasswordChanged(it)) },
            label = { Text(stringResource(id = R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { onAction(SignUpAction.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(id = R.string.registrate))
            }
        }

        TextButton(onClick = onNavigateBack) {
            Text(stringResource(id = R.string.back))
        }
    }
}

