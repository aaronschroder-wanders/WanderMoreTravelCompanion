package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.wandermore.travelcompanion.data.model.Trip
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

    var name by remember { mutableStateOf(trip.name) }

    var startDate by remember { mutableStateOf<LocalDate?>(trip.startDate) }

    var endDate by remember { mutableStateOf<LocalDate?>(trip.endDate) }

    var currency by remember { mutableStateOf(trip.homeCurrency) }


    var showStartPicker by remember { mutableStateOf(false) }

    var showEndPicker by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Edit Trip"
        )


        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Trip name") }
        )


        Button(
            onClick = { showStartPicker = true }
        ) {

            Text(
                text = startDate?.toString()
                    ?: "Select start date"
            )

        }


        Button(
            onClick = { showEndPicker = true }
        ) {

            Text(
                text = endDate?.toString()
                    ?: "Select end date"
            )

        }


        OutlinedTextField(
            value = currency,
            onValueChange = { currency = it },
            label = { Text("Home currency") }
        )


        Button(
            onClick = {

                val updatedTrip = trip.copy(
                    name = name,
                    startDate = startDate ?: trip.startDate,
                    endDate = endDate ?: trip.endDate,
                    homeCurrency = currency
                )


                tripViewModel.updateTrip(
                    updatedTrip
                )


                onTripUpdated()

            }
        ) {

            Text(
                text = "Save Changes"
            )

        }

    }


    if (showStartPicker) {

        val datePickerState = rememberDatePickerState()


        DatePickerDialog(
            onDismissRequest = {
                showStartPicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        startDate =
                            datePickerState.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }

                        showStartPicker = false
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


    if (showEndPicker) {

        val datePickerState = rememberDatePickerState()


        DatePickerDialog(
            onDismissRequest = {
                showEndPicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        endDate =
                            datePickerState.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
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