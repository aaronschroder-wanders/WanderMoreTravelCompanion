package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.util.formatDate


@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onClick()
            }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "🌏 ${trip.name}"
            )


            Text(
                text = "📅 ${formatDate(trip.startDate)} - ${formatDate(trip.endDate)}"
            )


            Text(
                text = "💰 ${trip.homeCurrency}"
            )

        }

    }

}