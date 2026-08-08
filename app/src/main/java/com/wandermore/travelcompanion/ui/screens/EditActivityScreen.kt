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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ActivityEntity
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    activity: ActivityEntity,
    tripViewModel: TripViewModel,
    exchangeRateViewModel: ExchangeRateViewModel,
    onActivityUpdated: () -> Unit,
    onDeleteActivity: () -> Unit,
    onBack: () -> Unit
) {

    var name by remember {
        mutableStateOf(activity.name)
    }

    var type by remember {
        mutableStateOf(activity.type)
    }

    var location by remember {
        mutableStateOf(activity.location ?: "")
    }

    var estimatedCost by remember {
        mutableStateOf(
            activity.estimatedCost?.toString() ?: ""
        )
    }

    var currency by remember {
        mutableStateOf(
            activity.currency ?: "NZD"
        )
    }

    var booked by remember {
        mutableStateOf(activity.booked)
    }

    var website by remember {
        mutableStateOf(activity.website ?: "")
    }

    var date by remember {
        mutableStateOf(
            activity.date?.toString() ?: ""
        )
    }

    var startTime by remember {
        mutableStateOf(
            activity.startTime?.let {
                String.format(
                    "%02d:%02d",
                    it.hour,
                    it.minute
                )
            } ?: ""
        )
    }

    var notes by remember {
        mutableStateOf(
            activity.notes ?: ""
        )
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    val activityTypes = listOf(
        "Attraction",
        "Tour",
        "Activity",
        "Other"
    )

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

    val parsedCost =
        estimatedCost.toDoubleOrNull()

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

    /*
     * Accept flexible 24-hour time input:
     *
     * 9     -> 09:00
     * 900   -> 09:00
     * 0900  -> 09:00
     * 09:00 -> 09:00
     * 930   -> 09:30
     * 23:45 -> 23:45
     */
    fun parseFlexibleTime(
        input: String
    ): LocalTime? {

        val value =
            input.trim()

        if (value.isBlank()) {
            return null
        }

        return try {

            if (value.contains(":")) {

                val parts =
                    value.split(":")

                if (parts.size != 2) {
                    null
                } else {

                    val hour =
                        parts[0].toInt()

                    val minute =
                        parts[1].toInt()

                    if (
                        hour in 0..23 &&
                        minute in 0..59
                    ) {
                        LocalTime.of(
                            hour,
                            minute
                        )
                    } else {
                        null
                    }
                }

            } else {

                when (value.length) {

                    1,
                    2 -> {

                        val hour =
                            value.toInt()

                        if (hour in 0..23) {
                            LocalTime.of(
                                hour,
                                0
                            )
                        } else {
                            null
                        }
                    }

                    3 -> {

                        val hour =
                            value.substring(
                                0,
                                1
                            ).toInt()

                        val minute =
                            value.substring(
                                1,
                                3
                            ).toInt()

                        if (
                            hour in 0..23 &&
                            minute in 0..59
                        ) {
                            LocalTime.of(
                                hour,
                                minute
                            )
                        } else {
                            null
                        }
                    }

                    4 -> {

                        val hour =
                            value.substring(
                                0,
                                2
                            ).toInt()

                        val minute =
                            value.substring(
                                2,
                                4
                            ).toInt()

                        if (
                            hour in 0..23 &&
                            minute in 0..59
                        ) {
                            LocalTime.of(
                                hour,
                                minute
                            )
                        } else {
                            null
                        }
                    }

                    else -> null
                }
            }

        } catch (_: Exception) {
            null
        }
    }

    val parsedStartTime =
        parseFlexibleTime(
            startTime
        )

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

        // ---------------------------------------------------------
        // TITLE
        // ---------------------------------------------------------

        Text(
            text = "Edit Activity",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ---------------------------------------------------------
        // SCROLLABLE FORM
        // ---------------------------------------------------------

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    bottom = 24.dp
                )
        ) {

            // -----------------------------------------------------
            // NAME
            // -----------------------------------------------------

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // -----------------------------------------------------
            // TYPE + LOCATION
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = {
                        typeExpanded =
                            !typeExpanded
                    },
                    modifier = Modifier.weight(1f)
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
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = {
                            typeExpanded = false
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

                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                    },
                    label = {
                        Text("Location")
                    },
                    modifier =
                        Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // -----------------------------------------------------
            // ESTIMATED COST + CURRENCY
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = estimatedCost,
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
                        value = currency,
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
                        modifier = Modifier
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
                modifier = Modifier.height(8.dp)
            )

            // -----------------------------------------------------
            // BOOKED
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.height(8.dp)
            )

            // -----------------------------------------------------
            // WEBSITE
            // -----------------------------------------------------

            OutlinedTextField(
                value = website,
                onValueChange = {
                    website = it
                },
                label = {
                    Text("Website")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // -----------------------------------------------------
            // DATE + START TIME
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

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
                                showDatePicker = true
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

                OutlinedTextField(
                    value = startTime,
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
                        Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
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
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 3
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // -----------------------------------------------------
            // DELETE
            // -----------------------------------------------------

            OutlinedButton(
                onClick = {
                    showDeleteConfirmation = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Activity")
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }

        // ---------------------------------------------------------
        // FIXED BACK / SAVE BUTTONS
        // ---------------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    bottom = 8.dp
                ),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }

            Button(
                onClick = {

                    if (name.isNotBlank()) {

                        /*
                         * Calculate the NZD value at save time.
                         *
                         * The database stores:
                         *
                         * 1 foreign currency = X NZD
                         *
                         * So:
                         *
                         * foreign amount × rateToNZD = NZD amount
                         */

                        val convertedAmount =
                            if (parsedCost != null) {

                                if (
                                    currency == "NZD"
                                ) {

                                    parsedCost

                                } else {

                                    val rate =
                                        exchangeRateViewModel
                                            .getRate(
                                                currency
                                            )

                                    if (rate > 0.0) {

                                        parsedCost * rate

                                    } else {

                                        null
                                    }
                                }

                            } else {
                                null
                            }

                        val updatedActivity =
                            activity.copy(

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
                                    parsedStartTime
                            )

                        tripViewModel.updateActivity(
                            updatedActivity
                        )

                        onActivityUpdated()
                    }
                }
            ) {
                Text("Save")
            }
        }
    }

    // -------------------------------------------------------------
    // DATE PICKER
    // -------------------------------------------------------------

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
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
                                    selectedDate.toString()
                            }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(
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

    // -------------------------------------------------------------
    // DELETE CONFIRMATION
    // -------------------------------------------------------------

    if (showDeleteConfirmation) {

        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },

            title = {
                Text("Delete Activity?")
            },

            text = {
                Text(
                    "Are you sure you want to delete " +
                            "\"${activity.name}\"? This cannot be undone."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showDeleteConfirmation = false

                        onDeleteActivity()
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