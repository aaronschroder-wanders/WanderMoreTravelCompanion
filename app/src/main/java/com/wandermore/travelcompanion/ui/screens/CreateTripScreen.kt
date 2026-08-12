package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    tripViewModel: TripViewModel,
    onTripCreated: () -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var startDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var endDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var currency by remember {
        mutableStateOf("NZD")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var showStartPicker by remember {
        mutableStateOf(false)
    }

    var showEndPicker by remember {
        mutableStateOf(false)
    }

    // ---------------------------------------------------------
    // SCREEN
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .imePadding(),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "Create New Trip"
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Trip name")
            }
        )

        // -----------------------------------------------------
        // START DATE
        // -----------------------------------------------------

        Button(
            onClick = {
                showStartPicker = true
            }
        ) {

            Text(
                text =
                    startDate?.let {
                        formatDate(it)
                    } ?: "Select start date"
            )
        }

        // -----------------------------------------------------
        // END DATE
        // -----------------------------------------------------

        Button(
            onClick = {

                // Only allow the end-date picker if a
                // start date has already been selected.
                if (startDate != null) {
                    showEndPicker = true
                }
            }
        ) {

            Text(
                text =
                    endDate?.let {
                        formatDate(it)
                    } ?: "Select end date"
            )
        }

        // -----------------------------------------------------
        // CURRENCY
        // -----------------------------------------------------

        CurrencyDropdown(
            selectedCurrency = currency,
            onCurrencySelected = {
                currency = it
            }
        )

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Button(
            onClick = {

                errorMessage = ""

                if (
                    name.isBlank()
                    || startDate == null
                    || endDate == null
                ) {

                    errorMessage =
                        "Please complete all fields"

                } else if (
                    endDate!! < startDate!!
                ) {

                    errorMessage =
                        "End date cannot be before start date"

                } else {

                    tripViewModel.addTrip(
                        name = name,
                        startDate = startDate!!,
                        endDate = endDate!!,
                        homeCurrency = currency
                    )

                    onTripCreated()
                }
            }
        ) {

            Text("Save Trip")
        }

        // -----------------------------------------------------
        // ERROR
        // -----------------------------------------------------

        if (errorMessage.isNotBlank()) {

            Text(
                text = errorMessage
            )
        }
    }

    // =========================================================
    // START DATE PICKER
    // =========================================================

    if (showStartPicker) {

        val datePickerState =
            rememberDatePickerState()

        DatePickerDialog(

            onDismissRequest = {
                showStartPicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        val selectedDate =
                            datePickerState
                                .selectedDateMillis
                                ?.let { millis ->

                                    Instant
                                        .ofEpochMilli(millis)
                                        .atZone(
                                            ZoneId.systemDefault()
                                        )
                                        .toLocalDate()
                                }

                        if (selectedDate != null) {

                            startDate =
                                selectedDate

                            // If an existing end date is now
                            // before the new start date,
                            // clear it.
                            if (
                                endDate != null &&
                                endDate!! < selectedDate
                            ) {
                                endDate = null
                            }

                            showStartPicker = false

                            // Automatically move to the
                            // end-date picker.
                            showEndPicker = true
                        }
                    }
                ) {

                    Text("OK")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }

    // =========================================================
    // END DATE PICKER
    // =========================================================

    if (showEndPicker && startDate != null) {

        // Convert the selected start date into milliseconds.
        val startDateMillis =
            startDate!!
                .atStartOfDay(
                    ZoneId.systemDefault()
                )
                .toInstant()
                .toEpochMilli()

        // -----------------------------------------------------
        // Only dates on or after the start date can be selected.
        // -----------------------------------------------------

        val selectableDates =
            object : SelectableDates {

                override fun isSelectableDate(
                    utcTimeMillis: Long
                ): Boolean {

                    val date =
                        Instant
                            .ofEpochMilli(
                                utcTimeMillis
                            )
                            .atZone(
                                ZoneId.systemDefault()
                            )
                            .toLocalDate()

                    return !date.isBefore(
                        startDate!!
                    )
                }
            }

        // -----------------------------------------------------
        // Start the calendar on the selected start-date month.
        // -----------------------------------------------------

        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis =
                    startDateMillis,
                initialDisplayedMonthMillis =
                    startDateMillis,
                selectableDates =
                    selectableDates
            )

        DatePickerDialog(

            onDismissRequest = {
                showEndPicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        endDate =
                            datePickerState
                                .selectedDateMillis
                                ?.let { millis ->

                                    Instant
                                        .ofEpochMilli(millis)
                                        .atZone(
                                            ZoneId.systemDefault()
                                        )
                                        .toLocalDate()
                                }

                        showEndPicker = false
                    }
                ) {

                    Text("OK")
                }
            },

            dismissButton = {

                Button(
                    onClick = {
                        showEndPicker = false
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
}