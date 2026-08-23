package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ActivityEntity
import com.wandermore.travelcompanion.database.DestinationEntity
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter

@Composable
fun DestinationDetailsScreen(

    tripId: Long,

    destinationId: Long,

    tripViewModel: TripViewModel,

    onBack: () -> Unit

) {

    var destination by remember {
        mutableStateOf<DestinationEntity?>(null)
    }

    var activities by remember {
        mutableStateOf<List<ActivityEntity>>(
            emptyList()
        )
    }

    var itineraryItems by remember {
        mutableStateOf<List<ItineraryEntity>>(
            emptyList()
        )
    }

    LaunchedEffect(
        tripId,
        destinationId
    ) {

        destination =
            tripViewModel
                .getDestinationByIdFlow(
                    destinationId
                )
                .first()

        activities =
            tripViewModel
                .getActivitiesForDestination(
                    tripId,
                    destinationId
                )

        itineraryItems =
            tripViewModel
                .getItineraryForDestination(
                    tripId,
                    destinationId
                )
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {

        // =========================================================
        // DESTINATION TITLE
        // =========================================================

        Text(

            text =
                destination?.name
                    ?: "Destination",

            style =
                MaterialTheme.typography.headlineMedium,

            modifier =
                Modifier.padding(
                    bottom = 16.dp
                )

        )


        // =========================================================
        // CONTENT
        // =========================================================

        LazyColumn(

            modifier =
                Modifier.weight(1f),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)

        ) {

            // =====================================================
            // ACTIVITIES
            // =====================================================

            item {

                Text(

                    text = "Activities",

                    style =
                        MaterialTheme.typography.titleLarge

                )
            }

            if (activities.isEmpty()) {

                item {

                    Text(

                        text =
                            "No activities at this destination.",

                        style =
                            MaterialTheme.typography.bodyMedium

                    )
                }

            } else {

                items(
                    items = activities,
                    key = {
                        "activity_${it.id}"
                    }
                ) { activity ->

                    ActivityDestinationCard(
                        activity = activity
                    )
                }
            }


            // =====================================================
            // ITINERARY
            // =====================================================

            item {

                Text(

                    text = "Itinerary",

                    style =
                        MaterialTheme.typography.titleLarge,

                    modifier =
                        Modifier.padding(
                            top = 8.dp
                        )

                )
            }

            if (itineraryItems.isEmpty()) {

                item {

                    Text(

                        text =
                            "No itinerary items at this destination.",

                        style =
                            MaterialTheme.typography.bodyMedium

                    )
                }

            } else {

                items(
                    items = itineraryItems,
                    key = {
                        "itinerary_${it.id}"
                    }
                ) { itinerary ->

                    ItineraryDestinationCard(
                        itinerary = itinerary
                    )
                }
            }
        }


        // =========================================================
        // BACK
        // =========================================================

        Button(

            onClick =
                onBack,

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary,
                    contentColor =
                        MaterialTheme.colorScheme.onPrimary
                ),

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp
                    )

        ) {

            Text(
                text = "Back"
            )
        }
    }
}


// =============================================================
// ACTIVITY CARD
// =============================================================

@Composable
private fun ActivityDestinationCard(

    activity: ActivityEntity

) {

    Card(

        modifier =
            Modifier.fillMaxWidth()

    ) {

        Column(

            modifier =
                Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(4.dp)

        ) {

            Text(

                text =
                    activity.name,

                style =
                    MaterialTheme.typography.titleMedium

            )

            Text(

                text =
                    activity.type,

                style =
                    MaterialTheme.typography.bodyMedium

            )

            activity.date?.let { date ->

                Text(

                    text =
                        date.format(
                            DateTimeFormatter.ofPattern(
                                "dd MMM yyyy"
                            )
                        ),

                    style =
                        MaterialTheme.typography.bodyMedium

                )
            }
        }
    }
}


// =============================================================
// ITINERARY CARD
// =============================================================

@Composable
private fun ItineraryDestinationCard(

    itinerary: ItineraryEntity

) {

    Card(

        modifier =
            Modifier.fillMaxWidth()

    ) {

        Column(

            modifier =
                Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(4.dp)

        ) {

            Text(

                text =
                    itinerary.title,

                style =
                    MaterialTheme.typography.titleMedium

            )

            Text(

                text =
                    itinerary.type,

                style =
                    MaterialTheme.typography.bodyMedium

            )

            Text(

                text =
                    itinerary.date.format(
                        DateTimeFormatter.ofPattern(
                            "dd MMM yyyy"
                        )
                    ),

                style =
                    MaterialTheme.typography.bodyMedium

            )
        }
    }
}