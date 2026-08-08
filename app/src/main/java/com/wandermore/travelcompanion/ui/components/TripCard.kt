package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.util.formatDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun TripCard(
    trip: Trip,
    totalSpent: Double,
    onClick: () -> Unit
) {

    val tripDays =
        ChronoUnit.DAYS.between(
            trip.startDate,
            trip.endDate
        ) + 1

    val costPerDay =
        if (
            trip.status == TripStatus.ARCHIVED &&
            tripDays > 0
        ) {
            totalSpent / tripDays
        } else {
            null
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
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

            if (trip.status == TripStatus.ARCHIVED) {

                Text(
                    text = "💵 Total: ${
                        String.format(
                            Locale.US,
                            "%s %,.2f",
                            trip.homeCurrency,
                            totalSpent
                        )
                    }"
                )

                Text(
                    text = "📊 Per day: ${
                        String.format(
                            Locale.US,
                            "%s %,.2f",
                            trip.homeCurrency,
                            costPerDay ?: 0.0
                        )
                    }"
                )

            } else {

                Text(
                    text = "💵 Spent so far: ${
                        String.format(
                            Locale.US,
                            "%s %,.2f",
                            trip.homeCurrency,
                            totalSpent
                        )
                    }"
                )
            }
        }
    }
}