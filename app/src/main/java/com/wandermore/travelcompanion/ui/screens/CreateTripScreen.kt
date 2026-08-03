package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.LocalDate

@Composable
fun CreateTripScreen(
    tripViewModel: TripViewModel,
    onTripCreated: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("NZD") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Create New Trip"
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Trip name") }
        )

        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start date YYYY-MM-DD") }
        )

        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End date YYYY-MM-DD") }
        )

        OutlinedTextField(
            value = currency,
            onValueChange = { currency = it },
            label = { Text("Home currency") }
        )

        Button(
            onClick = {

                tripViewModel.addTrip(
                    name = name,
                    startDate = LocalDate.parse(startDate),
                    endDate = LocalDate.parse(endDate),
                    homeCurrency = currency
                )

                onTripCreated()
            }
        ) {
            Text("Save Trip")
        }
    }
}
