package com.example.eventflow

import com.example.eventflow.ui.theme.elements.BaseScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.eventflow.constants.Constants
import com.example.eventflow.extensions.getValue
import com.example.eventflow.extensions.removeValue
import com.example.eventflow.extensions.round
import com.example.eventflow.model.Location
import com.example.eventflow.navigation.INavigationRouter
import com.example.eventflow.ui.theme.elements.CustomDataPickerDialog
import com.example.eventflow.ui.theme.elements.InfoElement
import com.example.eventflow.utils.DateUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.time.LocalDate


@Composable
fun FilterScreen(navigation: INavigationRouter) {
    val viewModel = hiltViewModel<FilterViewModel>()
    val state by viewModel.uiState.collectAsState()
    val navController = navigation.getNavController()

    // ⬇️ Přenést data ze previous zpět do current při návratu
    LaunchedEffect(Unit) {
        val currentHandle = navController.currentBackStackEntry?.savedStateHandle
        val previousHandle = navController.previousBackStackEntry?.savedStateHandle

        val keys = listOf("filter_startDate", "filter_endDate", "filter_category", "filter_latitude", "filter_longitude")

        keys.forEach { key ->
            val value = previousHandle?.get<Any?>(key)
            value?.let { currentHandle?.set(key, it) }
        }
    }

    // 🌍 Zpracování návratu z mapy
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val mapLocationResult = navController.getValue<String>(Constants.LOCATION)
        mapLocationResult?.value?.let {
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<Location> = moshi.adapter(Location::class.java)
            val location = jsonAdapter.fromJson(it)
            navController.removeValue<String>(Constants.LOCATION)
            location?.let {
                viewModel.onAction(FilterActions.LocationChanged(it.latitude, it.longitude))
            }
        }
    }

    BaseScreen(
        title = stringResource(R.string.filter),
        showBackButton = true,
        onBackClick = { navigation.returnBack() }
    ) { padding ->
        FilterScreenContent(
            paddingValues = padding,
            uiState = state,
            onAction = viewModel::onAction,
            navigation = navigation
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreenContent(
    paddingValues: PaddingValues,
    uiState: FilterUIState,
    onAction: (FilterActions) -> Unit,
    navigation: INavigationRouter
) {
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 📅 Date From
        if (showFromDatePicker) {
            CustomDataPickerDialog(
                date = uiState.startDate,
                onDateSelected = { onAction(FilterActions.StartDateChanged(it)) },
                onDismiss = { showFromDatePicker = false }
            )
        }
        InfoElement(
            value = uiState.startDate?.let { DateUtils.getDateString(it) },
            hint = stringResource(id = R.string.from_date),
            leadingIcon = Icons.Default.DateRange,
            onClick = { showFromDatePicker = true },
            onClearClick = { onAction(FilterActions.StartDateChanged(null)) }
        )

        // 📅 Date To
        if (showToDatePicker) {
            CustomDataPickerDialog(
                date = uiState.endDate,
                onDateSelected = { onAction(FilterActions.EndDateChanged(it)) },
                onDismiss = { showToDatePicker = false }
            )
        }
        InfoElement(
            value = uiState.endDate?.let { DateUtils.getDateString(it) },
            hint = stringResource(id = R.string.to_date),
            leadingIcon = Icons.Default.DateRange,
            onClick = { showToDatePicker = true },
            onClearClick = { onAction(FilterActions.EndDateChanged(null)) }
        )

        val categoryOptions = listOf("Music", "Dance", "Workshop", "Festival", "Other")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = uiState.selectedCategory ?: "",
                onValueChange = {},
                label = { Text(stringResource(id = R.string.category)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoryOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onAction(FilterActions.CategoryChanged(option))
                            expanded = false
                        }
                    )
                }
            }
        }

        // 📍 Location
        InfoElement(
            value = if (uiState.latitude != null && uiState.longitude != null)
                "%.4f, %.4f".format(uiState.latitude, uiState.longitude) else null,
            hint = stringResource(id = R.string.location),
            leadingIcon = Icons.Default.LocationOn,
            onClick = {
                navigation.navigateToMap(uiState.latitude, uiState.longitude)
            },
            onClearClick = {
                onAction(FilterActions.LocationChanged(null, null))
            }
        )

        Button(
            onClick = {
                onAction(FilterActions.Submit)

                val navController = navigation.getNavController()
                val previousHandle = navController.previousBackStackEntry?.savedStateHandle

                uiState.startDate?.let { previousHandle?.set("filter_startDate", it) }
                uiState.endDate?.let { previousHandle?.set("filter_endDate", it) }
                previousHandle?.set("filter_category", uiState.selectedCategory)
                previousHandle?.set("filter_latitude", uiState.latitude)
                previousHandle?.set("filter_longitude", uiState.longitude)

                // nebo volitelně jako JSON (což už tam máš):
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter(FilterUIState::class.java)
                val filterJson = jsonAdapter.toJson(uiState)
                previousHandle?.set("applied_filter_json", filterJson)

                navigation.returnBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.apply_filters))
        }
    }
}
