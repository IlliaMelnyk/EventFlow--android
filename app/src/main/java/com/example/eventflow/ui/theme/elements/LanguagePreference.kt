package com.example.eventflow.ui.theme.elements

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.eventflow.extensions.languageDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LanguagePreference @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.languageDataStore

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    val languageFlow: Flow<String> = dataStore.data
        .map { preferences -> preferences[LANGUAGE_KEY] ?: "cs" }

    suspend fun saveLanguage(lang: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = lang
        }
    }
}