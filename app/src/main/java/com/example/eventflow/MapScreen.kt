package com.example.eventflow

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventflow.navigation.INavigationRouter
import com.example.eventflow.navigation.MapScreenDestination
import com.example.eventflow.ui.theme.basicMargin
import com.example.eventflow.ui.theme.elements.BaseScreen
import com.example.eventflow.ui.theme.halfMargin
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navigation: INavigationRouter,
              mapScreenDestination: MapScreenDestination,
              ) {

    val viewModel = hiltViewModel<MapScreenViewModel>()

    val state = viewModel.mapScreenUIState.collectAsStateWithLifecycle()

    LaunchedEffect(mapScreenDestination) {
        if (mapScreenDestination.latitude != null && mapScreenDestination.longitude != null){
            viewModel.locationChanged(mapScreenDestination.latitude!!, mapScreenDestination.longitude!!)
        }
    }

    BaseScreen(
        title = stringResource(R.string.map),
        content = {
            MapScreenContent(
                latitude = state.value.latitude,
                longitude = state.value.longitude,
                actions = viewModel,
                paddingValues = it,
                onButtonClick = {
                    if (state.value.locationChanged) {
                        navigation.returnFromMap(state.value.latitude, state.value.longitude)
                    } else {
                        navigation.returnBack()
                    }
                })
        },
        onBackClick = { navigation.returnBack() }
    )
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreenContent(
    paddingValues: PaddingValues,
    latitude: Double,
    longitude: Double,
    actions: MapActions,
    onButtonClick: () -> Unit,){

    val mapUiSettings by remember { mutableStateOf(
        MapUiSettings(
        zoomControlsEnabled = false,
        mapToolbarEnabled = false)
    ) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 10f)
    }
    Box(Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        GoogleMap(modifier = Modifier.fillMaxHeight(),
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState
        ){

            MapEffect { map ->
                map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
                    override fun onMarkerDrag(p0: Marker) {}
                    override fun onMarkerDragEnd(p0: Marker) {
                        actions.locationChanged(p0.position.latitude, p0.position.longitude)
                    }
                    override fun onMarkerDragStart(p0: Marker) {}
                })
            }

            Marker(
                state = MarkerState(position = LatLng(latitude,longitude)),
                draggable = true
            )
        }

        Box(modifier = Modifier
            .padding(halfMargin)
            .align(Alignment.TopCenter)) {
            MarkerHelp()
        }

        OutlinedButton(
            modifier = Modifier
                .padding(
                    start = basicMargin,
                    end = basicMargin,
                    bottom = basicMargin
                )
                .align(Alignment.BottomCenter),
            onClick = onButtonClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White, containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = stringResource(R.string.save_location))
        }

    }
}

@Composable
fun MarkerHelp(){
    Card(
        colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                start = basicMargin,
                end = basicMargin,
                top = halfMargin,
                bottom = halfMargin)
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                text = stringResource(R.string.marker_help)
            )
        }
    }
}