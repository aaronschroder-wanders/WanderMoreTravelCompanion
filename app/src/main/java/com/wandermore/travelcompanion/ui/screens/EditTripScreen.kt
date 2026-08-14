package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripScreen(
    trip: Trip,
    tripViewModel: TripViewModel,
    onTripUpdated: () -> Unit
) {

    var name by remember {
        mutableStateOf(trip.name)
    }

    var startDate by remember {
        mutableStateOf<LocalDate?>(trip.startDate)
    }

    var endDate by remember {
        mutableStateOf<LocalDate?>(trip.endDate)
    }

    var status by remember {
        mutableStateOf(trip.status)
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

    var statusExpanded by remember {
        mutableStateOf(false)
    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .imePadding()
            .padding(16.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        Text(
            text = "Edit Trip"
        )


        // ---------------------------------------------------------
        // TRIP NAME
        // ---------------------------------------------------------

        OutlinedTextField(

            value = name,

            onValueChange = {
                name = it
            },

            label = {
                Text("Trip name")
            }
        )


        // ---------------------------------------------------------
        // START DATE
        // ---------------------------------------------------------

        Button(

            onClick = {
                showStartPicker = true
            }

        ) {

            Text(

                text =
                    startDate?.let {
                        formatDate(it)
                    }
                        ?: "Select start date"

            )

        }


        // ---------------------------------------------------------
        // END DATE
        // ---------------------------------------------------------

        Button(

            onClick = {
                showEndPicker = true
            }

        ) {

            Text(

                text =
                    endDate?.let {
                        formatDate(it)
                    }
                        ?: "Select end date"

            )

        }


        // ---------------------------------------------------------
        // HOME CURRENCY
        //
        // This is the currency stored with this trip.
        // It is deliberately not editable.
        // ---------------------------------------------------------

        Text(
            text = "Home Currency: ${trip.homeCurrency}"
        )

        Text(
            text = "Trip currency cannot be changed."
        )


        // ---------------------------------------------------------
        // TRIP STATUS
        // ---------------------------------------------------------

        Text(
            text = "Trip Status"
        )


        OutlinedButton(

            onClick = {
                statusExpanded = true
            }

        ) {

            Text(

                status.name
                    .lowercase()
                    .replaceFirstChar {
                        it.uppercase()
                    }

            )

        }


        DropdownMenu(

            expanded = statusExpanded,

            onDismissRequest = {
                statusExpanded = false
            }

        ) {

            TripStatus.values().forEach { option ->

                DropdownMenuItem(

                    text = {

                        Text(

                            option.name
                                .lowercase()
                                .replaceFirstChar {
                                    it.uppercase()
                                }

                        )

                    },

                    onClick = {

                        status = option
                        statusExpanded = false

                    }

                )

            }

        }


        // ---------------------------------------------------------
        // SAVE
        // ---------------------------------------------------------

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

                    val updatedTrip =
                        trip.copy(

                            name = name,

                            startDate =
                                startDate!!,

                            endDate =
                                endDate!!,

                            // IMPORTANT:
                            // Preserve the currency already
                            // belonging to this trip.
                            homeCurrency =
                                trip.homeCurrency,

                            status =
                                status

                        )

                    tripViewModel.updateTrip(
                        updatedTrip
                    )

                    onTripUpdated()

                }

            }

        ) {

            Text(
                text = "Save Changes"
            )

        }


        // ---------------------------------------------------------
        // ERROR
        // ---------------------------------------------------------

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

                        startDate =
                            datePickerState
                                .selectedDateMillis
                                ?.let {

                                    Instant
                                        .ofEpochMilli(it)
                                        .atZone(
                                            ZoneId.systemDefault()
                                        )
                                        .toLocalDate()

                                }


                        showStartPicker = false

                        showEndPicker = true

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

    if (showEndPicker) {

        val datePickerState =
            rememberDatePickerState()


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
                                ?.let {

                                    Instant
                                        .ofEpochMilli(it)
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

            }

        ) {

            DatePicker(
                state = datePickerState
            )

        }

    }

}