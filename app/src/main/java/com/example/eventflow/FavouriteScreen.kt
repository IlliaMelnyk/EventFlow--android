package com.example.eventflow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eventflow.firestore.Event
import com.example.eventflow.navigation.INavigationRouter
import com.example.eventflow.ui.theme.elements.BaseScreen
import com.example.eventflow.ui.theme.elements.BottomNavigationBar
import com.example.eventflow.ui.theme.elements.EventCard


@Composable
fun FavouriteScreen(navigation: INavigationRouter) {
    val viewModel = hiltViewModel<HomeFavouriteViewModel>()
    val events by viewModel.events.collectAsState()
    val favoriteEventIds by viewModel.favorites.collectAsState()

    val favoriteEvents = events.filter { favoriteEventIds.contains(it.id) }

    BaseScreen(
        title = stringResource(id = R.string.nav_favorites),
        showBackButton = true,
        bottomBar = {
            BottomNavigationBar(navigation)
        }
    ) { padding ->
        FavouriteScreenContent(
            favoriteEvents = favoriteEvents,
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            paddingValues = padding,
            navigation = navigation
        )
    }
}

@Composable
fun FavouriteScreenContent(
    favoriteEvents: List<Event>,
    onToggleFavorite: (String) -> Unit,
    paddingValues: PaddingValues,
    navigation: INavigationRouter
) {
    if (favoriteEvents.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("No favourite events yet.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(favoriteEvents) { event ->
                EventCard(
                    event = event,
                    isFavorite = true,
                    onClick = { navigation.navigateToEventDetailScreen(event.id) },
                    onFavoriteClick = { onToggleFavorite(event.id) }
                )
            }
        }
    }
}
