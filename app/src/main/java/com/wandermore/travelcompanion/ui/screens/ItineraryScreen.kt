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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.ui.components.TripSectionHeader
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

    // ---------------------------------------------------------
    // LOAD TRIP
    // ---------------------------------------------------------

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(initial = null)

    val currentTrip =
        tripState ?: return

    // ---------------------------------------------------------
    // LOAD ITINERARY
    // ---------------------------------------------------------

    val itinerary by tripViewModel
        .getItineraryForTrip(currentTrip.id)
        .collectAsState(initial = emptyList())

    // ---------------------------------------------------------
    // SORT
    // ---------------------------------------------------------

    val sortedItinerary =
        itinerary.sortedWith(
            compareBy<ItineraryEntity> {
                it.date
            }.thenBy {
                it.time
            }
        )

    // ---------------------------------------------------------
    // GROUP BY DATE
    // ---------------------------------------------------------

    val itineraryByDate =
        sortedItinerary.groupBy {
            it.date
        }

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
            title = "Itinerary",
            tripName = currentTrip.name,
            startDate = currentTrip.startDate,
            endDate = currentTrip.endDate,
            icon = "🗓️"
        )

        // -----------------------------------------------------
        // ITINERARY LIST
        // -----------------------------------------------------

        if (sortedItinerary.isEmpty()) {

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
                    text = "No itinerary items yet.",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text =
                        "Add your travel plans, accommodation and other key events.",
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

                itineraryByDate.forEach {
                        (date, itemsForDate) ->

                    // -------------------------------------------------
                    // DATE HEADING
                    // -------------------------------------------------

                    item(
                        key = "date_$date"
                    ) {

                        Text(
                            text =
                                formatItineraryDate(date),
                            style =
                                MaterialTheme.typography.titleMedium,
                            fontWeight =
                                FontWeight.SemiBold,
                            color =
                                MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier.padding(
                                    top = 4.dp,
                                    bottom = 2.dp
                                )
                        )
                    }

                    // -------------------------------------------------
                    // ITEMS FOR DATE
                    // -------------------------------------------------

                    items(
                        items = itemsForDate,
                        key = {
                            it.id
                        }
                    ) { item ->

                        ItineraryCard(
                            item = item,
                            onClick = {
                                onItinerarySelected(
                                    item.id
                                )
                            }
                        )
                    }
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
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Button(
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
            .clickable(
                onClick = onClick
            ),
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
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                )
        ) {

            // -----------------------------------------------------
            // TITLE ROW
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // Small type symbol

                Text(
                    text =
                        itinerarySymbol(item.type),
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    modifier =
                        Modifier.size(26.dp)
                )

                Spacer(
                    modifier =
                        Modifier.size(8.dp)
                )

                // Title

                Text(
                    text = item.title,
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.SemiBold,
                    modifier =
                        Modifier.weight(1f)
                )

                // Type — top right

                if (item.type.isNotBlank()) {

                    Text(
                        text = item.type,
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        color =
                            MaterialTheme.colorScheme
                                .primary,
                        fontWeight =
                            FontWeight.Medium
                    )
                }
            }

            // -----------------------------------------------------
            // TIME / NIGHTS / DEPARTURE
            // -----------------------------------------------------

            val hasTime =
                item.time != null

            val hasNights =
                item.nights != null &&
                        item.nights > 0

            if (hasTime || hasNights) {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // TIME

                    if (hasTime) {

                        Text(
                            text =
                                "🕐 " +
                                        item.time!!.format(
                                            DateTimeFormatter.ofPattern(
                                                "HH:mm"
                                            )
                                        ),
                            style =
                                MaterialTheme.typography
                                    .bodySmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant,
                            fontWeight =
                                FontWeight.Medium
                        )
                    }

                    // SPACE BETWEEN TIME AND NIGHTS

                    if (hasTime && hasNights) {

                        Spacer(
                            modifier =
                                Modifier.size(16.dp)
                        )
                    }

                    // NIGHTS

                    if (hasNights) {

                        Text(
                            text =
                                if (item.nights == 1) {
                                    "🛏 1 night"
                                } else {
                                    "🛏 ${item.nights} nights"
                                },
                            style =
                                MaterialTheme.typography
                                    .bodySmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    // SPACE BEFORE DEPARTURE

                    if (hasNights) {

                        Spacer(
                            modifier =
                                Modifier.size(16.dp)
                        )

                        // DEPARTURE DATE

                        Text(
                            text =
                                "→ " +
                                        item.date
                                            .plusDays(
                                                item.nights!!
                                                    .toLong()
                                            )
                                            .format(
                                                DateTimeFormatter.ofPattern(
                                                    "dd MMM"
                                                )
                                            ),
                            style =
                                MaterialTheme.typography
                                    .bodySmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant,
                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


// =================================================================
// ITINERARY TYPE SYMBOL
// =================================================================

private fun itinerarySymbol(
    type: String
): String {

    return when (
        type.trim().lowercase()
    ) {

        "travel" ->
            "🚆"

        "accommodation" ->
            "🏨"

        "activity" ->
            "🎯"

        "attraction" ->
            "📸"

        "arrival" ->
            "🛬"

        "departure" ->
            "🛫"

        "other" ->
            "📌"

        else ->
            "📅"
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
            "EEEE, dd MMM yyyy"
        )
    )
}