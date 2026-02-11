package com.example.eventflow

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eventflow.navigation.INavigationRouter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.eventflow.firestore.Event
import com.example.eventflow.ui.theme.elements.BottomNavigationBar
import com.example.eventflow.ui.theme.elements.EventCard
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


@Composable
fun HomeScreen(navController: NavHostController, navigation: INavigationRouter) {
    val viewModel = hiltViewModel<HomeFavouriteViewModel>()
    val events by viewModel.filteredEvents.collectAsState()  // používáme filtrovaná data
    val favoriteEventIds by viewModel.favorites.collectAsState()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val savedStateHandle = currentBackStackEntry?.savedStateHandle

    LaunchedEffect(currentBackStackEntry) {
        savedStateHandle?.get<String>("applied_filter_json")?.let { filterJson ->
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(FilterUIState::class.java)
            val filter = adapter.fromJson(filterJson)
            if (filter != null) {
                viewModel.updateFilter(filter)
            }
            savedStateHandle.remove<String>("applied_filter_json")
        }
        savedStateHandle?.get<Boolean>("event_deleted")?.let { deleted ->
            if (deleted) {
                viewModel.loadEvents()
                savedStateHandle.remove<Boolean>("event_deleted")
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navigation)
        }
    ) { padding ->
        HomeScreenContent(
            events = events,
            favoriteEventIds = favoriteEventIds,
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            paddingValues = padding,
            navigation = navigation
        )
    }
}

@Composable
fun HomeScreenContent(
    events: List<Event>,
    favoriteEventIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    paddingValues: PaddingValues,
    navigation: INavigationRouter
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        Button(
            onClick = { navigation.navigateToFilterScreen() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(stringResource(id = R.string.filter))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val filteredEvents = events.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }

            items(filteredEvents) { event ->
                val isFavorite = favoriteEventIds.contains(event.id)

                EventCard(
                    event = event,
                    isFavorite = isFavorite,
                    onClick = { navigation.navigateToEventDetailScreen(event.id) },
                    onFavoriteClick = { onToggleFavorite(event.id) }
                )
            }
        }
    }
}
