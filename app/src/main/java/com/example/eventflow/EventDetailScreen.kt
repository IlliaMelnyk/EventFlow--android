package com.example.eventflow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eventflow.firestore.Event
import com.example.eventflow.navigation.INavigationRouter
import com.example.eventflow.ui.theme.elements.BaseScreen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun EventDetailScreen(navRouter: INavigationRouter, eventId: String?) {
    val viewModel: EventDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }

    when (uiState) {
        is EventDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is EventDetailUiState.Error -> {
            val message = (uiState as EventDetailUiState.Error).message
            Text("Error: $message", modifier = Modifier.padding(16.dp))
        }

        is EventDetailUiState.Success -> {
            val event = (uiState as EventDetailUiState.Success).event
            val isFavorite = favorites.contains(event.id)

            BaseScreen(
                title = event.name,
                showBackButton = true,
                onBackClick = { navRouter.returnBack() },
                actions = {
                    if (event.ownerId == FirebaseAuth.getInstance().currentUser?.uid) {
                        IconButton(onClick = {
                            navRouter.navigateToAddEditEventScreen(event.id)
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }

                        IconButton(onClick = {
                            showDeleteDialog.value = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            ) {
                EventDetailScreenContent(
                    event = event,
                    isFavorite = isFavorite,
                    onToggleFavorite = { viewModel.toggleFavorite(event) },
                    paddingValues = it,
                    viewModel = viewModel
                )
            }

            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog.value = false
                    },
                    title = {
                        Text(text = stringResource(id = R.string.confirm_delete_title))
                    },
                    text = {
                        Text(text = stringResource(id = R.string.confirm_delete_message))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog.value = false
                                viewModel.deleteEvent(event.id) {
                                    val navController = navRouter.getNavController()
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("event_deleted", true)

                                    navRouter.returnBack()
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.delete),
                                color = Color.Red // volitelné: zvýraznit smazání
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteDialog.value = false
                        }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreenContent(
    event: Event,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    paddingValues: PaddingValues,
    viewModel: EventDetailViewModel
) {

    val categoryLocalized = when (event.category) {
        "music" -> stringResource(R.string.category_music)
        "dance" -> stringResource(R.string.category_dance)
        "workshop" -> stringResource(R.string.category_workshop)
        "festival" -> stringResource(R.string.category_festival)
        "other" -> stringResource(R.string.category_other)
        else -> event.category
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        if (!event.imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Spacer(modifier = Modifier.height(250.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(event.name, style = MaterialTheme.typography.headlineMedium)

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            stringResource(R.string.category_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            categoryLocalized,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            stringResource(id = R.string.description),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            event.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            stringResource(id = R.string.date),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            viewModel.formatDate(event.date),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (event.latitude != null && event.longitude != null) {
            val location = LatLng(event.latitude!!, event.longitude!!)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 14f)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 16.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize().fillMaxWidth(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = location),
                        title = event.name
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}