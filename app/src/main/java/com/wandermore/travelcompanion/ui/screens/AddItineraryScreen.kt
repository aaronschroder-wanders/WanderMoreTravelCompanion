package com.wandermore.travelcompanion.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.ui.components.DestinationSelector
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.net.URI
import java.net.URISyntaxException
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

    // =========================================================
    // FORM STATE
    // =========================================================

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

    var notes by remember {
        mutableStateOf("")
    }

    var booked by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // WEB LINK STATE
    // =========================================================

    var webLink by remember {
        mutableStateOf("")
    }

    var editingWebLink by remember {
        mutableStateOf(true)
    }

    var webLinkError by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    // =========================================================
    // DESTINATION STATE
    // =========================================================

    var selectedDestinationIds by remember {
        mutableStateOf<Set<Long>>(emptySet())
    }

    /*
     * Use ALL ACTIVE destinations here.
     *
     * This means destinations created in Settings will appear
     * even if they have not yet been associated with this trip.
     *
     * The selected destinations are associated with the trip
     * when the itinerary item is saved.
     */

    val destinations by
    tripViewModel
        .getAllActiveDestinations()
        .collectAsState(
            initial = emptyList()
        )

    // =========================================================
    // DIALOG STATE
    // =========================================================

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

    // =========================================================
    // WEB LINK FUNCTIONS
    // =========================================================

    fun isValidWebLink(
        link: String
    ): Boolean {

        if (link.isBlank()) return false

        return try {

            val uri =
                URI(link)

            val scheme =
                uri.scheme?.lowercase()

            !uri.host.isNullOrBlank() &&
                    (
                            scheme == "http" ||
                                    scheme == "https"
                            )

        } catch (
            _: URISyntaxException
        ) {

            false
        }
    }

    fun linkWebAddress() {

        val cleanedLink =
            webLink.trim()

        if (
            !isValidWebLink(
                cleanedLink
            )
        ) {

            webLinkError =
                "Please enter a valid web link starting with https://"

        } else {

            webLink =
                cleanedLink

            webLinkError =
                ""

            editingWebLink =
                false
        }
    }

    fun openWebLink(
        context: Context,
        link: String
    ) {

        if (
            !isValidWebLink(
                link
            )
        ) {
            return
        }

        try {

            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(link)
                )

            context.startActivity(
                intent
            )

        } catch (
            _: Exception
        ) {

            // Do nothing if the link cannot be opened.
        }
    }

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

            // =================================================
            // HEADER
            // =================================================

            Text(
                text = "Add Itinerary Item",
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "Add a key event, stay or travel plan.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =================================================
            // DATE
            // =================================================

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

            // =================================================
            // TIME
            // =================================================

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

            // =================================================
            // TITLE
            // =================================================

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

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =================================================
            // TYPE
            // =================================================

            ExposedDropdownMenuBox(
                expanded = typeExpanded,

                onExpandedChange = {
                    typeExpanded =
                        !typeExpanded
                },

                modifier =
                    Modifier.fillMaxWidth()
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

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    typeExpanded
                            )
                    },

                    modifier =
                        Modifier
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

                    itineraryTypes.forEach {
                            itineraryType ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    itineraryType
                                )
                            },

                            onClick = {

                                type =
                                    itineraryType

                                typeExpanded =
                                    false
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =================================================
            // BOOKED
            // =================================================

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
                            "Mark this item as already booked.",

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

            // =================================================
            // NIGHTS
            // =================================================

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

            // =================================================
            // DESTINATION
            // =================================================

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

                    tripViewModel
                        .addDestinationAndReturnId(
                            destinationName
                        ) { destinationId ->

                            if (
                                destinationId != null
                            ) {

                                selectedDestinationIds =
                                    selectedDestinationIds +
                                            destinationId

                                onResult(true)

                            } else {

                                onResult(false)
                            }
                        }
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =================================================
            // WEB LINK
            // =================================================

            Text(
                text = "Web Link",
                style =
                    MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            if (
                editingWebLink
            ) {

                OutlinedTextField(
                    value =
                        webLink,

                    onValueChange = {
                        webLink = it
                        webLinkError = ""
                    },

                    label = {
                        Text("Web Link")
                    },

                    placeholder = {
                        Text("Paste web link")
                    },

                    isError =
                        webLinkError.isNotBlank(),

                    supportingText = {

                        if (
                            webLinkError.isNotBlank()
                        ) {

                            Text(
                                webLinkError
                            )
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Button(
                    onClick = {
                        linkWebAddress()
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text("Link")
                }

            } else {

                Text(
                    text = "✓ Link added",
                    style =
                        MaterialTheme.typography
                            .bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = {

                            openWebLink(
                                context,
                                webLink
                            )
                        },

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text("Open Link")
                    }

                    Button(
                        onClick = {

                            webLink =
                                ""

                            webLinkError =
                                ""

                            editingWebLink =
                                true
                        },

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text("Change Link")
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =================================================
            // NOTES
            // =================================================

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
            modifier =
                Modifier
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

            // =================================================
            // CANCEL
            // =================================================

            OutlinedButton(
                onClick = onBack,

                modifier =
                    Modifier.weight(1f)
            ) {

                Text("Cancel")
            }

            // =================================================
            // ADD ITEM
            // =================================================

            Button(
                onClick = {

                    val selectedDate =
                        date ?: return@Button

                    val nights =
                        nightsText.toIntOrNull()

                    val itinerary =
                        ItineraryEntity(

                            tripId =
                                tripId,

                            date =
                                selectedDate,

                            time =
                                time,

                            title =
                                title.trim(),

                            type =
                                type,

                            nights =
                                nights,

                            location =
                                null,

                            notes =
                                notes
                                    .trim()
                                    .ifBlank {
                                        null
                                    },

                            webLink =
                                webLink
                                    .trim()
                                    .ifBlank {
                                        null
                                    },

                            booked =
                                booked
                        )

                    tripViewModel.addItinerary(
                        itinerary,
                        selectedDestinationIds
                    )

                    onItineraryAdded()
                },

                enabled =
                    date != null &&
                            title.isNotBlank() &&
                            type.isNotBlank(),

                modifier =
                    Modifier.weight(1.5f)
            ) {

                Text("Add Item")
            }
        }
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
                state =
                    datePickerState
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
                state =
                    timePickerState
            )
        }
    }
}