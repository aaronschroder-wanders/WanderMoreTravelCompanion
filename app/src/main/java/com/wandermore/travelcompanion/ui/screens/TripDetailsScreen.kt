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
fun TripDetailsScreen(
    trip: Trip,
    onEditTrip: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(
            text = "🌏 ${trip.name}"
        )


        Text(
            text = "📅 Start: ${trip.startDate}"
        )


        Text(
            text = "📅 End: ${trip.endDate}"
        )


        Text(
            text = "💰 Home currency: ${trip.homeCurrency}"
        )


        Button(
            onClick = onEditTrip
        ) {

            Text(
                text = "Edit Trip"
            )

        }

    }
}