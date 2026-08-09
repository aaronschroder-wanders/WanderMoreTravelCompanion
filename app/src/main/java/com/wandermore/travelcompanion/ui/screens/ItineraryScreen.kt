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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ItineraryScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onAddItinerary: () -> Unit,
    onItinerarySelected: (Long) -> Unit,
    onBack: () -> Unit
) {

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(initial = null)

    val currentTrip = tripState ?: return

    val itinerary by tripViewModel
        .getItineraryForTrip(currentTrip.id)
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        Text(
            text = "Itinerary",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = currentTrip.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ---------------------------------------------------------
        // ITINERARY LIST
        // ---------------------------------------------------------

        if (itinerary.isEmpty()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "No itinerary items yet.",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Add your travel plans, accommodation and other key events.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = itinerary,
                    key = { it.id }
                ) { item ->

                    ItineraryCard(
                        item = item,
                        onClick = {
                            onItinerarySelected(item.id)
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ---------------------------------------------------------
        // BOTTOM BUTTONS
        // ---------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = onAddItinerary,
                modifier = Modifier.weight(1.5f)
            ) {
                Text("＋ Add Item")
            }
        }
    }
}


// =================================================================
// ITINERARY CARD
// =================================================================

@Composable
private fun ItineraryCard(
    item: ItineraryEntity,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            // -----------------------------------------------------
            // DATE
            // -----------------------------------------------------

            Text(
                text = formatItineraryDate(item.date),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            // -----------------------------------------------------
            // TIME
            // -----------------------------------------------------

            if (item.time != null) {

                Text(
                    text = item.time.format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // -----------------------------------------------------
            // TITLE
            // -----------------------------------------------------

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // -----------------------------------------------------
            // TYPE
            // -----------------------------------------------------

            if (item.type.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = item.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // -----------------------------------------------------
            // LOCATION
            // -----------------------------------------------------

            if (!item.location.isNullOrBlank()) {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = item.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // -----------------------------------------------------
            // NIGHTS
            // -----------------------------------------------------

            if (item.nights != null && item.nights > 0) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = if (item.nights == 1) {
                        "1 night"
                    } else {
                        "${item.nights} nights"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


// =================================================================
// DATE FORMATTING
// =================================================================

private fun formatItineraryDate(
    date: java.time.LocalDate?
): String {

    if (date == null) {
        return "No date"
    }

    return date.format(
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy"
        )
    )
}
