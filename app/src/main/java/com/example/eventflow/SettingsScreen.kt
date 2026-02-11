package com.example.eventflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eventflow.navigation.INavigationRouter
import com.example.eventflow.ui.theme.elements.BaseScreen
import com.example.eventflow.ui.theme.elements.BottomNavigationBar
import com.example.eventflow.ui.theme.elements.DropdownMenuLanguage


@Composable
fun SettingsScreen(navigation: INavigationRouter) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    val displayName = viewModel.displayName
    val context = LocalContext.current
    val version = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    BaseScreen(
        title = stringResource(id = R.string.settings_greeting, displayName),
        showBackButton = false,
        bottomBar = {
            BottomNavigationBar(navigation)
        }
    ) { padding ->
        SettingsScreenContent(
            paddingValues = padding,
            onLogout = { navigation.navigateToLogInScreen() }
        )
    }
}
@Composable
fun SettingsScreenContent(
    paddingValues: PaddingValues,
    viewModel: SettingsViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val newName = viewModel.newDisplayName
    val updateMessage = viewModel.updateMessage
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current
    val version = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Language Dropdown
            Text(
                text = stringResource(id = R.string.language),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            DropdownMenuLanguage(
                selected = selectedLanguage,
                onLanguageSelected = { viewModel.changeLanguage(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Change Name
            /*OutlinedTextField(
                value = newName,
                onValueChange = { viewModel.onDisplayNameChange(it) },
                label = { Text(stringResource(id = R.string.change_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.updateDisplayName() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.save_name))
            }

            updateMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))*/

            // Logout
            Button(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(stringResource(id = R.string.logout), color = Color.White)
            }
        }

        // App version and description at the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.about_app),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${stringResource(id = R.string.app_version)} $version",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}