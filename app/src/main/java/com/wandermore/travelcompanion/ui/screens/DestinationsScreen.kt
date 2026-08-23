package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.DestinationEntity
import com.wandermore.travelcompanion.viewmodel.TripViewModel

private data class DestinationSummary(
    val activityCount: Int = 0,
    val itineraryCount: Int = 0
)

@Composable
fun DestinationsScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onDestinationClick: (Long) -> Unit,
    onBack: () -> Unit
) {

    val destinations by tripViewModel
        .getDestinationsForTrip(tripId)
        .collectAsState(
            initial = emptyList()
        )

    var summaries by remember {
        mutableStateOf(
            emptyMap<Long, DestinationSummary>()
        )
    }

    // ---------------------------------------------------------
    // LOAD DESTINATION SUMMARY COUNTS
    // ---------------------------------------------------------

    LaunchedEffect(
        tripId,
        destinations
    ) {

        val newSummaries =
            destinations.associate { destination ->

                val activities =
                    tripViewModel.getActivitiesForDestination(
                        tripId,
                        destination.id
                    )

                val itineraryItems =
                    tripViewModel.getItineraryForDestination(
                        tripId,
                        destination.id
                    )

                destination.id to DestinationSummary(
                    activityCount =
                        activities.size,

                    itineraryCount =
                        itineraryItems.size
                )
            }

        summaries = newSummaries
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "📍",
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Column(
                modifier =
                    Modifier.padding(
                        start = 10.dp
                    )
            ) {

                Text(
                    text = "Destinations",
                    style =
                        MaterialTheme.typography.headlineMedium
                )

                Text(
                    text =
                        "Places you're visiting on this trip",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,

                    modifier =
                        Modifier.padding(
                            top = 2.dp
                        )
                )
            }
        }

        // =====================================================
        // DESTINATION LIST
        // =====================================================

        if (destinations.isEmpty()) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),

                verticalArrangement =
                    Arrangement.Center,

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "📍",
                    style =
                        MaterialTheme.typography.displaySmall
                )

                Text(
                    text =
                        "No destinations yet",

                    style =
                        MaterialTheme.typography.titleMedium,

                    modifier =
                        Modifier.padding(
                            top = 12.dp
                        )
                )

                Text(
                    text =
                        "Destinations will appear here when you add a location to an activity or itinerary item.",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,

                    modifier =
                        Modifier
                            .padding(
                                top = 6.dp,
                                start = 24.dp,
                                end = 24.dp
                            )
                )
            }

        } else {

            LazyColumn(
                modifier =
                    Modifier.weight(1f),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = destinations,
                    key = {
                        it.id
                    }
                ) { destination ->

                    DestinationCard(
                        destination =
                            destination,

                        summary =
                            summaries[
                                destination.id
                            ]
                                ?: DestinationSummary(),

                        onClick = {
                            onDestinationClick(
                                destination.id
                            )
                        }
                    )
                }
            }
        }

        // =====================================================
        // BACK
        // =====================================================

        Button(
            onClick = onBack,

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Back"
            )
        }
    }
}


// =============================================================
// DESTINATION CARD
// =============================================================

@Composable
private fun DestinationCard(
    destination: DestinationEntity,
    summary: DestinationSummary,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceVariant
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // -------------------------------------------------
            // DESTINATION ICON
            // -------------------------------------------------

            Box(
                modifier =
                    Modifier.size(42.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "📍",
                    style =
                        MaterialTheme.typography.headlineSmall
                )
            }

            // -------------------------------------------------
            // DESTINATION INFORMATION
            // -------------------------------------------------

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            start = 10.dp
                        )
            ) {

                Text(
                    text =
                        destination.name,

                    style =
                        MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier =
                        Modifier.size(5.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    Text(
                        text =
                            "⭐ ${summary.activityCount} " +
                                    if (
                                        summary.activityCount == 1
                                    ) {
                                        "Activity"
                                    } else {
                                        "Activities"
                                    },

                        style =
                            MaterialTheme.typography.bodyMedium,

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text =
                            "🗓️ ${summary.itineraryCount} " +
                                    if (
                                        summary.itineraryCount == 1
                                    ) {
                                        "Itinerary"
                                    } else {
                                        "Itinerary items"
                                    },

                        style =
                            MaterialTheme.typography.bodyMedium,

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // -------------------------------------------------
            // CHEVRON
            // -------------------------------------------------

            Text(
                text = "›",

                style =
                    MaterialTheme.typography.headlineMedium,

                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}