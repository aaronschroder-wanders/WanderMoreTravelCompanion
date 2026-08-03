package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wandermore.travelcompanion.data.model.Trip

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    trips: List<Trip>,
    onCreateTrip: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Wander More Travel Companion"
        )

        if (trips.isEmpty()) {

            Text(
                text = "No trips yet"
            )

        } else {

            trips.forEach { trip ->

                Text(
                    text = trip.name
                )

                Text(
                    text = "${trip.startDate} - ${trip.endDate}"
                )

                Text(
                    text = trip.homeCurrency
                )
            }
        }

        Button(
            onClick = onCreateTrip
        ) {
            Text("Create Trip")
        }
    }
}
