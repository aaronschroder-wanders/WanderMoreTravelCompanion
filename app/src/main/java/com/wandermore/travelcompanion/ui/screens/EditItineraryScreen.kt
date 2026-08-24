package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.ui.components.DestinationSelector
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItineraryScreen(
    itineraryId: Long,
    tripViewModel: TripViewModel,
    onItineraryUpdated: () -> Unit,
    onDeleteItinerary: (ItineraryEntity) -> Unit,
    onBack: () -> Unit
) {

    // =========================================================
    // FORM STATE
    // =========================================================

    var existingItem by remember {
        mutableStateOf<ItineraryEntity?>(null)
    }

    var date by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var time by remember {
        mutableStateOf<LocalTime?>(null)
    }

    var title by remember {
        mutableStateOf("")
    }

    var type by remember {
        mutableStateOf("")
    }

    var nightsText by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var booked by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // DESTINATION STATE
    // =========================================================

    var selectedDestinationIds by remember(
        itineraryId
    ) {
        mutableStateOf<Set<Long>>(emptySet())
    }

    var destinationsLoaded by remember(
        itineraryId
    ) {
        mutableStateOf(false)
    }

    // =========================================================
    // DIALOG STATE
    // =========================================================

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // LOAD EXISTING ITEM
    // =========================================================

    LaunchedEffect(itineraryId) {

        val item =
            tripViewModel.getItineraryById(
                itineraryId
            )

        if (item != null) {

            existingItem = item

            date = item.date
            time = item.time
            title = item.title
            type = item.type

            nightsText =
                item.nights?.toString() ?: ""

            location =
                item.location ?: ""

            notes =
                item.notes ?: ""

            booked =
                item.booked

            selectedDestinationIds =
                tripViewModel
                    .getDestinationIdsForItinerary(
                        item.id
                    )
                    .toSet()

            destinationsLoaded = true
        }
    }

    // =========================================================
    // CURRENT ITEM
    // =========================================================

    if (existingItem == null) {
        return
    }

    val currentItem =
        existingItem!!

    // =========================================================
    // DESTINATIONS FOR THIS TRIP
    // =========================================================

    val destinations by
    tripViewModel
        .getDestinationsForTrip(
            currentItem.tripId
        )
        .collectAsState(
            initial = emptyList()
        )

    // =========================================================
    // COROUTINE SCOPE
    // =========================================================

    val coroutineScope =
        rememberCoroutineScope()

    // =========================================================
    // FORMATTERS
    // =========================================================

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy"
        )

    val timeFormatter =
        DateTimeFormatter.ofPattern(
            "HH:mm"
        )

    // =========================================================
    // SCREEN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {

        // =====================================================
        // SCROLLABLE FORM
        // =====================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp)
        ) {

            // -------------------------------------------------
            // HEADER
            // -------------------------------------------------

            Text(
                text = "Edit Itinerary Item",
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "Update this travel plan, stay or event.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // -------------------------------------------------
            // DATE
            // -------------------------------------------------

            OutlinedButton(
                onClick = {
                    showDatePicker = true
                },
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        if (date == null) {
                            "Select date"
                        } else {
                            date!!.format(
                                dateFormatter
                            )
                        }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -------------------------------------------------
            // TIME
            // -------------------------------------------------

            OutlinedButton(
                onClick = {
                    showTimePicker = true
                },
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        if (time == null) {
                            "Select time — optional"
                        } else {
                            time!!.format(
                                timeFormatter
                            )
                        }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -------------------------------------------------
            // TITLE
            // -------------------------------------------------

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text("Title")
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -------------------------------------------------
            // TYPE
            // -------------------------------------------------

            OutlinedTextField(
                value = type,
                onValueChange = {
                    type = it
                },
                label = {
                    Text("Type")
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -------------------------------------------------
            // BOOKED
            // -------------------------------------------------

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = "Booked",
                        style =
                            MaterialTheme.typography
                                .bodyLarge
                    )

                    Text(
                        text =
                            if (booked) {
                                "This item is booked"
                            } else {
                                "Not booked yet"
                            },
                        style =
                            MaterialTheme.typography
                                .bodySmall,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                Switch(
                    checked = booked,
                    onCheckedChange = {
                        booked = it
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -------------------------------------------------
            // NIGHTS
            // -------------------------------------------------

            OutlinedTextField(
                value = nightsText,
                onValueChange = {

                    if (
                        it.all { character ->
                            character.isDigit()
                        }
                    ) {
                        nightsText = it
                    }
                },
                label = {
                    Text("Nights")
                },
                placeholder = {
                    Text("Optional")
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -------------------------------------------------
            // LOCATION
            // -------------------------------------------------

            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                },
                label = {
                    Text("Location")
                },
                placeholder = {
                    Text(
                        "City, address or destination"
                    )
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =================================================
            // DESTINATION
            // =================================================

            if (destinationsLoaded) {

                DestinationSelector(
                    destinations =
                        destinations,

                    selectedDestinationIds =
                        selectedDestinationIds,

                    onSelectionChanged = {
                        selectedDestinationIds =
                            it
                    },

                    onAddDestination = {
                            destinationName,
                            onResult ->

                        tripViewModel.addDestination(
                            destinationName
                        ) { success ->

                            if (success) {

                                coroutineScope.launch {

                                    val newDestination =
                                        tripViewModel
                                            .getDestinationByName(
                                                destinationName
                                            )

                                    if (
                                        newDestination != null
                                    ) {

                                        tripViewModel
                                            .addDestinationToTrip(
                                                currentItem.tripId,
                                                newDestination.id
                                            )

                                        selectedDestinationIds =
                                            selectedDestinationIds +
                                                    newDestination.id
                                    }
                                }
                            }

                            onResult(success)
                        }
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // -------------------------------------------------
            // NOTES
            // -------------------------------------------------

            OutlinedTextField(
                value = notes,
                onValueChange = {
                    notes = it
                },
                label = {
                    Text("Notes")
                },
                placeholder = {
                    Text(
                        "Transport information, address, reminders, etc."
                    )
                },
                modifier =
                    Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }

        // =====================================================
        // FIXED ACTION BUTTONS
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 10.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            // -------------------------------------------------
            // BACK
            // -------------------------------------------------

            Button(
                onClick = onBack,
                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Back")
            }

            // -------------------------------------------------
            // DELETE
            // -------------------------------------------------

            Button(
                onClick = {
                    showDeleteConfirmation = true
                },
                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Delete")
            }

            // -------------------------------------------------
            // SAVE
            // -------------------------------------------------

            Button(
                onClick = {

                    val selectedDate =
                        date ?: return@Button

                    val updatedItem =
                        currentItem.copy(

                            date =
                                selectedDate,

                            time =
                                time,

                            title =
                                title.trim(),

                            type =
                                type.trim(),

                            nights =
                                nightsText
                                    .toIntOrNull(),

                            location =
                                location
                                    .trim()
                                    .ifBlank {
                                        null
                                    },

                            notes =
                                notes
                                    .trim()
                                    .ifBlank {
                                        null
                                    },

                            booked =
                                booked
                        )

                    // =================================================
                    // SAVE WITH EXPLICIT DESTINATIONS
                    //
                    // An empty Set is intentional:
                    // it means the user has selected no destinations.
                    // =================================================

                    tripViewModel.updateItinerary(
                        updatedItem,
                        selectedDestinationIds
                    )

                    onItineraryUpdated()
                },

                enabled =
                    date != null &&
                            title.isNotBlank() &&
                            type.isNotBlank() &&
                            destinationsLoaded,

                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }

    // =========================================================
    // DATE PICKER
    // =========================================================

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis =
                    date
                        ?.atStartOfDay(
                            ZoneId.systemDefault()
                        )
                        ?.toInstant()
                        ?.toEpochMilli()
            )

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        datePickerState
                            .selectedDateMillis
                            ?.let { millis ->

                                date =
                                    Instant
                                        .ofEpochMilli(
                                            millis
                                        )
                                        .atZone(
                                            ZoneId
                                                .systemDefault()
                                        )
                                        .toLocalDate()
                            }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }

    // =========================================================
    // TIME PICKER
    // =========================================================

    if (showTimePicker) {

        val timePickerState =
            rememberTimePickerState(
                initialHour =
                    time?.hour ?: 12,
                initialMinute =
                    time?.minute ?: 0
            )

        TimePickerDialog(

            onDismissRequest = {
                showTimePicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        time =
                            LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )

                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },

            title = {
                Text("Select time")
            }
        ) {

            TimePicker(
                state = timePickerState
            )
        }
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    if (showDeleteConfirmation) {

        AlertDialog(

            onDismissRequest = {
                showDeleteConfirmation = false
            },

            title = {
                Text("Delete Item?")
            },

            text = {
                Text(
                    "Are you sure you want to delete this itinerary item?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showDeleteConfirmation = false

                        onDeleteItinerary(
                            currentItem
                        )
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}