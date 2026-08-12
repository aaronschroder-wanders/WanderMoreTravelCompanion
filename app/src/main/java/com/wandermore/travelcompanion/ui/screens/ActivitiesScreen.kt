package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ActivityEntity
import com.wandermore.travelcompanion.ui.components.TripSectionHeader
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.util.Locale

@Composable
fun ActivitiesScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onAddActivity: () -> Unit,
    onEditActivity: (Long) -> Unit,
    onBack: () -> Unit
) {

    // ---------------------------------------------------------
    // LOAD TRIP
    // ---------------------------------------------------------

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(initial = null)

    val currentTrip = tripState ?: return

    // ---------------------------------------------------------
    // LOAD ACTIVITIES
    // ---------------------------------------------------------

    val activities by tripViewModel
        .getActivitiesForTrip(currentTrip.id)
        .collectAsState(initial = emptyList())

    // ---------------------------------------------------------
    // SCREEN
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // -----------------------------------------------------
        // SHARED TRIP SECTION HEADER
        // -----------------------------------------------------

        TripSectionHeader(
            title = "Activities & Attractions",
            tripName = currentTrip.name,
            startDate = currentTrip.startDate,
            endDate = currentTrip.endDate,
            icon = "🎯"
        )

        // -----------------------------------------------------
        // ACTIVITY LIST
        // -----------------------------------------------------

        if (activities.isEmpty()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement =
                    Arrangement.Center,
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "No activities or attractions yet.",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text =
                        "Add places and experiences you may want to visit.",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = activities,
                    key = { it.id }
                ) { activity ->

                    ActivityCard(
                        activity = activity,
                        onClick = {
                            onEditActivity(
                                activity.id
                            )
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // BOTTOM BUTTONS
        // -----------------------------------------------------

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // Back is now a standard blue Button,
            // matching the other screens.

            Button(
                onClick = onBack,
                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = onAddActivity,
                modifier =
                    Modifier.weight(1.5f)
            ) {
                Text("Add Activity")
            }
        }
    }
}


// =================================================================
// ACTIVITY CARD
// =================================================================

@Composable
private fun ActivityCard(
    activity: ActivityEntity,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape =
            RoundedCornerShape(12.dp),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme
                        .surfaceContainer
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            // -----------------------------------------------------
            // NAME + BOOKED
            // -----------------------------------------------------

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.Top
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = activity.name,
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    if (activity.type.isNotBlank()) {

                        Text(
                            text = activity.type,
                            style =
                                MaterialTheme.typography
                                    .bodyMedium,
                            color =
                                MaterialTheme.colorScheme
                                    .primary
                        )
                    }
                }

                if (activity.booked) {

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "BOOKED",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        },
                        colors =
                            AssistChipDefaults
                                .assistChipColors(
                                    containerColor =
                                        Color(0xFFB6FF00),
                                    labelColor =
                                        Color.Black
                                )
                    )
                }
            }

            // -----------------------------------------------------
            // LOCATION
            // -----------------------------------------------------

            if (!activity.location.isNullOrBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "📍",
                        modifier =
                            Modifier.size(20.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.size(6.dp)
                    )

                    Text(
                        text =
                            activity.location!!,
                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )
                }
            }

            // -----------------------------------------------------
            // DATE + TIME
            // -----------------------------------------------------

            if (
                activity.date != null ||
                activity.startTime != null
            ) {

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "📅",
                        modifier =
                            Modifier.size(20.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.size(6.dp)
                    )

                    if (activity.date != null) {

                        Text(
                            text =
                                formatDate(
                                    activity.date
                                ),
                            style =
                                MaterialTheme.typography
                                    .bodyMedium
                        )
                    }

                    if (
                        activity.date != null &&
                        activity.startTime != null
                    ) {

                        Spacer(
                            modifier =
                                Modifier.size(6.dp)
                        )

                        Text(
                            text = "•",
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )

                        Spacer(
                            modifier =
                                Modifier.size(6.dp)
                        )
                    }

                    if (activity.startTime != null) {

                        Text(
                            text = "🕐",
                            modifier =
                                Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.size(4.dp)
                        )

                        Text(
                            text =
                                activity.startTime
                                    .toString(),
                            style =
                                MaterialTheme.typography
                                    .bodyMedium
                        )
                    }
                }
            }

            // -----------------------------------------------------
            // COST
            // -----------------------------------------------------

            if (activity.estimatedCost != null) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.Top
                ) {

                    Text(
                        text = "Estimated cost",
                        style =
                            MaterialTheme.typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )

                    Column(
                        horizontalAlignment =
                            Alignment.End
                    ) {

                        Text(
                            text = buildString {

                                append(
                                    formatActivityCost(
                                        activity.estimatedCost,
                                        activity.currency
                                    )
                                )

                                if (
                                    !activity.currency
                                        .isNullOrBlank()
                                ) {
                                    append(
                                        " ${activity.currency}"
                                    )
                                }
                            },
                            style =
                                MaterialTheme.typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.SemiBold
                        )

                        if (
                            activity.convertedAmount != null
                        ) {

                            Text(
                                text =
                                    "≈ NZD ${
                                        formatNzdAmount(
                                            activity.convertedAmount
                                        )
                                    }",
                                style =
                                    MaterialTheme.typography
                                        .bodyMedium,
                                color =
                                    MaterialTheme.colorScheme
                                        .primary
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------
            // NOTES
            // -----------------------------------------------------

            if (!activity.notes.isNullOrBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text = activity.notes!!,
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}


// =================================================================
// CURRENCY DISPLAY FORMATTING
// =================================================================

private fun formatActivityCost(
    amount: Double,
    currency: String?
): String {

    val noDecimalCurrencies =
        setOf(
            "VND",
            "JPY",
            "KRW",
            "IDR",
            "LAK"
        )

    return if (
        currency != null &&
        currency in noDecimalCurrencies
    ) {

        String.format(
            Locale.US,
            "%,.0f",
            amount
        )

    } else {

        String.format(
            Locale.US,
            "%,.2f",
            amount
        )
    }
}


// =================================================================
// NZD DISPLAY FORMATTING
// =================================================================

private fun formatNzdAmount(
    amount: Double
): String {

    return String.format(
        Locale.US,
        "%,.2f",
        amount
    )
}