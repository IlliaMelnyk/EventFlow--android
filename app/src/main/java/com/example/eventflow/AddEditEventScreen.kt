package com.example.eventflow

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.eventflow.constants.Constants
import com.example.eventflow.extensions.getValue
import com.example.eventflow.extensions.removeValue
import com.example.eventflow.extensions.round
import com.example.eventflow.image.FileUtil
import com.example.eventflow.model.Location
import com.example.eventflow.navigation.INavigationRouter
import com.example.eventflow.ui.theme.elements.BaseScreen
import com.example.eventflow.ui.theme.elements.BottomNavigationBar
import com.example.eventflow.ui.theme.elements.CategoryDropdown
import com.example.eventflow.ui.theme.elements.CustomDataPickerDialog
import com.example.eventflow.ui.theme.elements.InfoElement
import com.example.eventflow.ui.theme.halfMargin
import com.example.eventflow.utils.DateUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

@Composable
fun AddEditEventScreen(
    navigation: INavigationRouter,
    eventId: String? = null
) {
    val viewModel = hiltViewModel<AddEditEventViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        if (!eventId.isNullOrEmpty() && eventId != "null") {
            viewModel.loadEvent(eventId)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val mapLocationResult = navigation.getNavController().getValue<String>(Constants.LOCATION)
        mapLocationResult?.value?.let {
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<Location> = moshi.adapter(Location::class.java)
            val location = jsonAdapter.fromJson(it)
            navigation.getNavController().removeValue<Double>(Constants.LOCATION)
            location?.let {
                viewModel.onAction(AddEditEventActions.LocationChanged(it.latitude, it.longitude))
            }
        }
    }


    BaseScreen(
        title = stringResource(id = R.string.new_event),
        showBackButton = false,
        bottomBar = {
            BottomNavigationBar(navigation)
        }
    ) { padding ->
        AddEditEventScreenContent(
            paddingValues = padding,
            viewModel = viewModel,
            data = state.value,
            navigation = navigation
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreenContent(
    paddingValues: PaddingValues,
    viewModel: AddEditEventViewModel,
    data: AddEditEventUIState,
    navigation: INavigationRouter
) {
    val categoryMap = mapOf(
        "Music" to stringResource(R.string.category_music),
        "Dance" to stringResource(R.string.category_dance),
        "Workshop" to stringResource(R.string.category_workshop),
        "Festival" to stringResource(R.string.category_festival),
        "Other" to stringResource(R.string.category_other)
    )
    val selectedCategoryName = categoryMap[data.event.category] ?: stringResource(R.string.select_category)
    var categoryExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showDatePicker by remember { mutableStateOf(false) }

    // 📸 Launcher pro obrázek
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadImage(it) { u -> viewModel.getFile(context, u) }
        }
    }

    // 🔁 Když je event uložen, naviguj
    LaunchedEffect(data.eventSaved) {
        if (data.eventSaved) {
            navigation.navigateToHomeScreen()
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 📷 Add Photo Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (data.event.imageUrl.isNullOrBlank()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add photo",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(stringResource(id = R.string.add_photo))
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(model = data.event.imageUrl),
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        val maxNameLength = 25

        OutlinedTextField(
            value = data.event.name,
            onValueChange = { newText ->
                if (newText.length <= maxNameLength && !newText.contains('\n')) {
                    viewModel.onAction(AddEditEventActions.NameChanged(newText))
                }
            },
            maxLines = 1,
            singleLine = true,
            label = { Text(stringResource(id = R.string.new_name)) },
            isError = data.nameError != null,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.event.description,
            maxLines = 5,
            onValueChange = { viewModel.onAction(AddEditEventActions.DescriptionChanged(it)) },
            label = { Text(stringResource(id = R.string.description))},
            isError = data.descriptionError != null,
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.category)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categoryMap.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            viewModel.onAction(AddEditEventActions.CategoryChanged(key))
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        if (showDatePicker) {
            CustomDataPickerDialog(
                date = data.event.date,
                onDateSelected = { viewModel.onAction(AddEditEventActions.DateChanged(it)) },
                onDismiss = { showDatePicker = false }
            )
        }

        InfoElement(
            value = if (data.event.date != null) DateUtils.getDateString(data.event.date!!) else null,
            hint = stringResource(R.string.date),
            leadingIcon = Icons.Default.DateRange,
            onClick = { showDatePicker = true },
            onClearClick = { viewModel.onAction(AddEditEventActions.DateChanged(null)) }
        )

        InfoElement(
            value = if (data.event.hasLocation()) "${data.event.latitude!!.round()}, ${data.event.longitude!!.round()} " else null,
            hint = stringResource(id = R.string.location),
            leadingIcon = Icons.Default.LocationOn,
            onClick = {
                navigation.navigateToMap(data.event.latitude, data.event.longitude)
            }, onClearClick = {
                viewModel.onAction(AddEditEventActions.LocationChanged(null,null))
            })

        // 💾 Save button
        Button(
            onClick = { viewModel.onAction(AddEditEventActions.Submit) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.save))
        }

        if (data.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
