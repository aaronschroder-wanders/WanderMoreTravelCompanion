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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.DestinationEntity
import com.wandermore.travelcompanion.viewmodel.TripViewModel

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

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)

    ) {

        // =========================================================
        // HEADER
        // =========================================================

        Column {

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
                        top = 4.dp
                    )
            )
        }


        // =========================================================
        // DESTINATION LIST
        // =========================================================

        if (destinations.isEmpty()) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                verticalArrangement =
                    Arrangement.Center,

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                DestinationMarker()

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
                            .padding(top = 6.dp)
                            .padding(horizontal = 24.dp)
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

                        onClick = {

                            onDestinationClick(
                                destination.id
                            )
                        }
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

    onClick: () -> Unit

) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(16.dp)
                )
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )

    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp,
                        vertical = 16.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            // -----------------------------------------------------
            // DESTINATION MARKER
            // -----------------------------------------------------

            DestinationMarker()


            // -----------------------------------------------------
            // DESTINATION TEXT
            // -----------------------------------------------------

            Column(

                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            start = 16.dp
                        )

            ) {

                Text(

                    text =
                        destination.name,

                    style =
                        MaterialTheme.typography.titleLarge
                )

                Text(

                    text =
                        "View destination",

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


            // -----------------------------------------------------
            // CHEVRON
            // -----------------------------------------------------

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


// =============================================================
// DESTINATION MARKER
// =============================================================

@Composable
private fun DestinationMarker() {

    Surface(

        modifier =
            Modifier.size(42.dp),

        shape =
            CircleShape,

        color =
            MaterialTheme.colorScheme.primaryContainer

    ) {

        Box(

            contentAlignment =
                Alignment.Center

        ) {

            Text(

                text = "●",

                style =
                    MaterialTheme.typography.titleMedium,

                color =
                    MaterialTheme.colorScheme.primary
            )
        }
    }
}