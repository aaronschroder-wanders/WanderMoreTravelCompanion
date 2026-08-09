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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItineraryScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onItineraryAdded: () -> Unit,
    onBack: () -> Unit
) {

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

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var typeExpanded by remember {
        mutableStateOf(false)
    }

    val itineraryTypes = listOf(
        "Travel",
        "Accommodation",
        "Activity",
        "Attraction",
        "Arrival",
        "Departure",
        "Other"
    )

    val dateFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy")

    val timeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .imePadding()
            .padding(16.dp)
    ) {

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        Text(
            text = "Add Itinerary Item",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Add a key event, stay or travel plan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // -----------------------------------------------------
        // DATE
        // -----------------------------------------------------

        OutlinedButton(
            onClick = {
                showDatePicker = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = if (date == null) {
                    "Select date"
                } else {
                    date!!.format(dateFormatter)
                }
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // TIME
        // -----------------------------------------------------

        OutlinedButton(
            onClick = {
                showTimePicker = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = if (time == null) {
                    "Select time — optional"
                } else {
                    time!!.format(timeFormatter)
                }
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = {
                Text("Title")
            },
            placeholder = {
                Text("e.g. London to Budapest")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // TYPE
        // -----------------------------------------------------

        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = {
                typeExpanded = !typeExpanded
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = type,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Type")
                },
                placeholder = {
                    Text("Select type")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = typeExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = {
                    typeExpanded = false
                }
            ) {

                itineraryTypes.forEach { itineraryType ->

                    DropdownMenuItem(
                        text = {
                            Text(itineraryType)
                        },
                        onClick = {

                            type = itineraryType
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // NIGHTS
        // -----------------------------------------------------

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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // LOCATION
        // -----------------------------------------------------

        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
            },
            label = {
                Text("Location")
            },
            placeholder = {
                Text("City, address or destination")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // NOTES
        // -----------------------------------------------------

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
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // -----------------------------------------------------
        // BUTTONS
        // -----------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {

                    val selectedDate =
                        date ?: return@Button

                    val nights =
                        nightsText.toIntOrNull()

                    val itinerary =
                        ItineraryEntity(
                            tripId = tripId,
                            date = selectedDate,
                            time = time,
                            title = title.trim(),
                            type = type,
                            nights = nights,
                            location =
                                location.trim()
                                    .ifBlank {
                                        null
                                    },
                            notes =
                                notes.trim()
                                    .ifBlank {
                                        null
                                    }
                        )

                    tripViewModel.addItinerary(
                        itinerary
                    )

                    onItineraryAdded()
                },
                enabled =
                    date != null &&
                            title.isNotBlank() &&
                            type.isNotBlank(),
                modifier = Modifier.weight(1.5f)
            ) {
                Text("Add Item")
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }

// =========================================================
// DATE PICKER
// =========================================================

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState()

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
                                            ZoneId.systemDefault()
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

}
