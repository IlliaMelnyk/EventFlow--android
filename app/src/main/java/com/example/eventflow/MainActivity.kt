package com.example.eventflow

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.eventflow.navigation.Destination
import com.example.eventflow.navigation.NavGraph
import com.example.eventflow.ui.theme.EventFlowTheme
import com.example.eventflow.ui.theme.elements.LanguagePreference
import com.example.eventflow.ui.theme.elements.LocaleHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventFlowTheme {
                NavGraph(startDestination = Destination.SplashScreen.route)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = runBlocking { LanguagePreference(newBase).languageFlow.first() }
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EventFlowTheme {
        Greeting("Android")
    }
}