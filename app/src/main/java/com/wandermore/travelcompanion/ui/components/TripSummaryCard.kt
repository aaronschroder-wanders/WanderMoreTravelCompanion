package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.util.formatMoney

@Composable
fun TripSummaryCard(
    plannedDays: Long,
    totalEstimate: Double,
    totalSpent: Double,
    airfareTotal: Double,
    foodDrinkPerDay: Double = 0.0,
    currency: String,
    tripStatus: TripStatus,
    onClick: () -> Unit
) {

    val averagePerDay =
        if (plannedDays > 0) {
            totalSpent / plannedDays
        } else {
            0.0
        }

    val spendingExcludingFlights =
        totalSpent - airfareTotal

    val averageExcludingFlights =
        if (plannedDays > 0) {
            spendingExcludingFlights / plannedDays
        } else {
            0.0
        }

    val remaining =
        totalEstimate - totalSpent

    val durationLabel =
        when (tripStatus) {
            TripStatus.PLANNED ->
                "📅 Planned duration"

            TripStatus.CURRENT ->
                "📅 Days used"

            TripStatus.ARCHIVED ->
                "📅 Trip duration"
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onClick()
            }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "🌏 Trip Summary",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "$durationLabel: $plannedDays days",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "🎯 Total estimate: ${
                    formatMoney(
                        totalEstimate,
                        currency
                    )
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "💰 Total spent: ${
                    formatMoney(
                        totalSpent,
                        currency
                    )
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text =
                    if (remaining >= 0) {
                        "💵 Remaining: ${
                            formatMoney(
                                remaining,
                                currency
                            )
                        }"
                    } else {
                        "⚠️ Over by: ${
                            formatMoney(
                                -remaining,
                                currency
                            )
                        }"
                    },
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "📊 Average per day: ${
                    formatMoney(
                        averagePerDay,
                        currency
                    )
                }  /  Excl. flights: ${
                    formatMoney(
                        averageExcludingFlights,
                        currency
                    )
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "🍜 Food & Drink per day: ${
                    formatMoney(
                        foodDrinkPerDay,
                        currency
                    )
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Tap for spending breakdown",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}