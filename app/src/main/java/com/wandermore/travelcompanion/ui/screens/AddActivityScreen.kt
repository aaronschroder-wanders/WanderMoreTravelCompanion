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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ActivityEntity
import com.wandermore.travelcompanion.ui.components.DestinationSelector
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    exchangeRateViewModel: ExchangeRateViewModel,
    onActivityAdded: () -> Unit,
    onBack: () -> Unit
) {

    // =========================================================
    // LOAD TRIP
    // =========================================================

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(
            initial = null
        )

    val trip = tripState ?: return

    // =========================================================
    // TRIP HOME CURRENCY
    // =========================================================

    val tripHomeCurrency =
        trip.homeCurrency

    // =========================================================
    // DESTINATIONS
    //
    // Destinations are global and reusable.
    // The Activity stores references to the selected
    // destinations through ActivityDestinationEntity.
    // =========================================================

    val destinations by tripViewModel
        .getAllActiveDestinations()
        .collectAsState(
            initial = emptyList()
        )

    var selectedDestinationIds by remember {
        mutableStateOf<Set<Long>>(emptySet())
    }

    // =========================================================
    // FORM STATE
    // =========================================================

    var name by remember {
        mutableStateOf("")
    }

    var type by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var estimatedCost by remember {
        mutableStateOf("")
    }

    var currency by remember(tripHomeCurrency) {
        mutableStateOf(tripHomeCurrency)
    }

    var booked by remember {
        mutableStateOf(false)
    }

    var website by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf("")
    }

    var startTime by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // ACTIVITY TYPES
    // =========================================================

    val activityTypes = listOf(
        "Attraction",
        "Tour",
        "Activity",
        "Other"
    )

    // =========================================================
    // CURRENCIES
    // =========================================================

    val currencies = listOf(
        "NZD",
        "AUD",
        "USD",
        "EUR",
        "GBP",
        "THB",
        "VND",
        "LAK",
        "CNY"
    )

    var typeExpanded by remember {
        mutableStateOf(false)
    }

    var currencyExpanded by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // PARSED COST
    // =========================================================

    val parsedCost =
        estimatedCost.toDoubleOrNull()

    // =========================================================
    // PARSED DATE
    // =========================================================

    val parsedDate =
        try {

            if (date.isBlank()) {
                null
            } else {
                LocalDate.parse(date)
            }

        } catch (_: Exception) {

            null
        }

    // =========================================================
    // TIME NORMALISATION
    //
    // Accepts:
    //
    // 9       -> 09:00
    // 09      -> 09:00
    // 900     -> 09:00
    // 0900    -> 09:00
    // 09:00   -> 09:00
    // 1430    -> 14:30
    // 14:30   -> 14:30
    // =========================================================

    fun normaliseTime(
        input: String
    ): String? {

        val value =
            input.trim()

        if (value.isBlank()) {
            return null
        }

        return try {

            val hour: Int
            val minute: Int

            if (value.contains(":")) {

                val parts =
                    value.split(":")

                if (parts.size != 2) {
                    return null
                }

                hour =
                    parts[0].toInt()

                minute =
                    parts[1].toInt()

            } else {

                when (value.length) {

                    1,
                    2 -> {

                        hour =
                            value.toInt()

                        minute = 0
                    }

                    3 -> {

                        hour =
                            value.substring(
                                0,
                                1
                            ).toInt()

                        minute =
                            value.substring(
                                1,
                                3
                            ).toInt()
                    }

                    4 -> {

                        hour =
                            value.substring(
                                0,
                                2
                            ).toInt()

                        minute =
                            value.substring(
                                2,
                                4
                            ).toInt()
                    }

                    else -> {
                        return null
                    }
                }
            }

            if (
                hour !in 0..23 ||
                minute !in 0..59
            ) {
                return null
            }

            "%02d:%02d".format(
                hour,
                minute
            )

        } catch (_: Exception) {

            null
        }
    }

    // =========================================================
    // SCREEN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp
            )
    ) {

        // =====================================================
        // TITLE
        // =====================================================

        Text(
            text = "Add Activity",
            style =
                MaterialTheme.typography
                    .headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text = trip.name,
            style =
                MaterialTheme.typography
                    .titleMedium
        )

        Text(
            text =
                "Home currency: $tripHomeCurrency",
            style =
                MaterialTheme.typography
                    .bodyMedium
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

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
                .padding(
                    bottom = 24.dp
                )
        ) {

            // =================================================
            // NAME
            // =================================================

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Name")
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // =================================================
            // TYPE
            // =================================================

            ExposedDropdownMenuBox(
                expanded =
                    typeExpanded,
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
                            .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded =
                        typeExpanded,
                    onDismissRequest = {
                        typeExpanded =
                            false
                    }
                ) {

                    activityTypes.forEach {
                            activityType ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    activityType
                                )
                            },
                            onClick = {

                                type =
                                    activityType

                                typeExpanded =
                                    false
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // =================================================
            // DESTINATIONS
            // =================================================

            DestinationSelector(
                destinations =
                    destinations,

                selectedDestinationIds =
                    selectedDestinationIds,

                onSelectionChanged = {
                        updatedSelection ->

                    selectedDestinationIds =
                        updatedSelection
                },

                onAddDestination = {
                        destinationName,
                        onResult ->

                    tripViewModel
                        .addDestinationAndReturnId(
                            destinationName
                        ) { destinationId ->

                            if (destinationId != null) {

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
                modifier =
                    Modifier.height(10.dp)
            )

            // =================================================
            // LOCATION
            //
            // Kept as descriptive information.
            // It is NO LONGER used to determine Destination.
            // =================================================

            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                },
                label = {
                    Text("Location")
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // =================================================
            // ESTIMATED COST + CURRENCY
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value =
                        estimatedCost,

                    onValueChange = {
                        estimatedCost = it
                    },

                    label = {
                        Text("Estimated Cost")
                    },

                    modifier =
                        Modifier.weight(1f),

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        )
                )

                ExposedDropdownMenuBox(
                    expanded =
                        currencyExpanded,

                    onExpandedChange = {
                        currencyExpanded =
                            !currencyExpanded
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    OutlinedTextField(
                        value =
                            currency,

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Currency")
                        },

                        trailingIcon = {

                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        currencyExpanded
                                )
                        },

                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded =
                            currencyExpanded,

                        onDismissRequest = {
                            currencyExpanded =
                                false
                        }
                    ) {

                        currencies.forEach {
                                currencyCode ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        currencyCode
                                    )
                                },

                                onClick = {

                                    currency =
                                        currencyCode

                                    currencyExpanded =
                                        false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            // =================================================
            // CONVERSION PREVIEW
            // =================================================

            if (parsedCost != null) {

                val activityRateToNZD =
                    exchangeRateViewModel
                        .getRateToNZD(
                            currency
                        )

                val tripHomeRateToNZD =
                    exchangeRateViewModel
                        .getRateToNZD(
                            tripHomeCurrency
                        )

                val convertedAmount =
                    if (
                        currency ==
                        tripHomeCurrency
                    ) {

                        parsedCost

                    } else if (
                        activityRateToNZD > 0.0 &&
                        tripHomeRateToNZD > 0.0
                    ) {

                        parsedCost *
                                activityRateToNZD /
                                tripHomeRateToNZD

                    } else {

                        0.0
                    }

                Text(
                    text =
                        "≈ $tripHomeCurrency %.2f"
                            .format(
                                convertedAmount
                            ),

                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }

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

                Text(
                    text = "Booked"
                )

                Switch(
                    checked = booked,
                    onCheckedChange = {
                        booked = it
                    }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            // =================================================
            // WEBSITE
            // =================================================

            OutlinedTextField(
                value = website,
                onValueChange = {
                    website = it
                },
                label = {
                    Text("Website")
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            // =================================================
            // DATE + START TIME
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                // -------------------------------------------------
                // DATE
                // -------------------------------------------------

                OutlinedTextField(
                    value = date,

                    onValueChange = {},

                    readOnly = true,

                    label = {
                        Text("Date")
                    },

                    placeholder = {
                        Text("Select")
                    },

                    trailingIcon = {

                        TextButton(
                            onClick = {
                                showDatePicker =
                                    true
                            }
                        ) {

                            Text(
                                if (
                                    date.isBlank()
                                ) {
                                    "Select"
                                } else {
                                    "Change"
                                }
                            )
                        }
                    },

                    modifier =
                        Modifier.weight(1f)
                )

                // -------------------------------------------------
                // START TIME
                // -------------------------------------------------

                OutlinedTextField(
                    value =
                        startTime,

                    onValueChange = {
                        startTime = it
                    },

                    label = {
                        Text("Start Time")
                    },

                    placeholder = {
                        Text("HH:MM")
                    },

                    modifier =
                        Modifier
                            .weight(1f)
                            .onFocusChanged {
                                    focusState ->

                                if (
                                    !focusState
                                        .isFocused
                                ) {

                                    val formatted =
                                        normaliseTime(
                                            startTime
                                        )

                                    if (
                                        formatted != null
                                    ) {

                                        startTime =
                                            formatted
                                    }
                                }
                            },

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
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

                modifier =
                    Modifier.fillMaxWidth(),

                minLines = 3,

                maxLines = 3
            )

            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )
        }

        // =====================================================
        // FIXED BOTTOM BUTTONS
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    bottom = 8.dp
                ),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // =================================================
            // BACK
            // =================================================

            Button(
                onClick = onBack,
                modifier =
                    Modifier.weight(1f)
            ) {

                Text("Back")
            }

            // =================================================
            // SAVE
            // =================================================

            Button(
                onClick = {

                    if (name.isNotBlank()) {

                        // -----------------------------------------
                        // NORMALISE TIME
                        // -----------------------------------------

                        val formattedTime =
                            normaliseTime(
                                startTime
                            )

                        if (
                            startTime.isNotBlank() &&
                            formattedTime != null
                        ) {

                            startTime =
                                formattedTime
                        }

                        // -----------------------------------------
                        // CALCULATE CONVERSION
                        // -----------------------------------------

                        val convertedAmount =
                            if (
                                parsedCost != null
                            ) {

                                val activityRateToNZD =
                                    exchangeRateViewModel
                                        .getRateToNZD(
                                            currency
                                        )

                                val tripHomeRateToNZD =
                                    exchangeRateViewModel
                                        .getRateToNZD(
                                            tripHomeCurrency
                                        )

                                if (
                                    currency ==
                                    tripHomeCurrency
                                ) {

                                    parsedCost

                                } else if (
                                    activityRateToNZD > 0.0 &&
                                    tripHomeRateToNZD > 0.0
                                ) {

                                    parsedCost *
                                            activityRateToNZD /
                                            tripHomeRateToNZD

                                } else {

                                    null
                                }

                            } else {

                                null
                            }

                        // -----------------------------------------
                        // CREATE ACTIVITY
                        // -----------------------------------------

                        val activity =
                            ActivityEntity(

                                tripId =
                                    tripId,

                                name =
                                    name.trim(),

                                type =
                                    type,

                                location =
                                    location
                                        .trim()
                                        .ifBlank {
                                            null
                                        },

                                estimatedCost =
                                    parsedCost,

                                currency =
                                    if (
                                        parsedCost != null
                                    ) {
                                        currency
                                    } else {
                                        null
                                    },

                                convertedAmount =
                                    convertedAmount,

                                booked =
                                    booked,

                                website =
                                    website
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

                                date =
                                    parsedDate,

                                startTime =
                                    formattedTime?.let {

                                        try {

                                            LocalTime.parse(
                                                it
                                            )

                                        } catch (
                                            _: Exception
                                        ) {

                                            null
                                        }
                                    }
                            )

                        // -----------------------------------------
                        // SAVE ACTIVITY + DESTINATIONS
                        // -----------------------------------------

                        tripViewModel.addActivity(
                            activity =
                                activity,

                            destinationIds =
                                selectedDestinationIds
                        )

                        onActivityAdded()
                    }
                },

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
            rememberDatePickerState()

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker =
                    false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState
                            .selectedDateMillis
                            ?.let { millis ->

                                val selectedDate =
                                    Instant
                                        .ofEpochMilli(
                                            millis
                                        )
                                        .atZone(
                                            ZoneOffset.UTC
                                        )
                                        .toLocalDate()

                                date =
                                    selectedDate
                                        .toString()
                            }

                        showDatePicker =
                            false
                    }
                ) {

                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker =
                            false
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
}