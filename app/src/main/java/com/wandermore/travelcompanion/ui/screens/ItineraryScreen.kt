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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.ui.components.TripSectionHeader
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.LocalDate
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
    // LOAD DESTINATION NAMES FOR ITINERARY ITEMS
    // ---------------------------------------------------------

    var itineraryDestinations by remember {
        mutableStateOf(
            emptyMap<Long, List<String>>()
        )
    }

    LaunchedEffect(itinerary) {

        val destinationMap =
            itinerary.associate { item ->

                val destinationIds =
                    tripViewModel
                        .getDestinationIdsForItinerary(
                            item.id
                        )

                val destinationNames =
                    destinationIds.mapNotNull { destinationId ->

                        tripViewModel
                            .getDestinationById(
                                destinationId
                            )
                            ?.name
                    }

                item.id to destinationNames
            }

        itineraryDestinations =
            destinationMap
    }

    // ---------------------------------------------------------
    // FILTER
    // ---------------------------------------------------------

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    val filteredItinerary =
        when (selectedFilter) {

            "Accom." ->
                itinerary.filter {
                    it.type.equals(
                        "Accommodation",
                        ignoreCase = true
                    )
                }

            "Travel" ->
                itinerary.filter {
                    it.type.equals(
                        "Travel",
                        ignoreCase = true
                    )
                }

            "Others" ->
                itinerary.filter {
                    it.type.equals(
                        "Activity",
                        ignoreCase = true
                    ) ||
                            it.type.equals(
                                "Attraction",
                                ignoreCase = true
                            ) ||
                            it.type.equals(
                                "Arrival",
                                ignoreCase = true
                            ) ||
                            it.type.equals(
                                "Departure",
                                ignoreCase = true
                            ) ||
                            it.type.equals(
                                "Other",
                                ignoreCase = true
                            )
                }

            else ->
                itinerary
        }

    // ---------------------------------------------------------
    // SORT
    // ---------------------------------------------------------

    val sortedItinerary =
        filteredItinerary.sortedWith(
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

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // -----------------------------------------------------
        // FILTER CHIPS
        // -----------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            FilterChip(
                selected = selectedFilter == "All",
                onClick = {
                    selectedFilter = "All"
                },
                label = {
                    Text("All")
                },
                modifier = Modifier.weight(1f),
                colors =
                    FilterChipDefaults.filterChipColors()
            )

            FilterChip(
                selected = selectedFilter == "Accom.",
                onClick = {
                    selectedFilter = "Accom."
                },
                label = {
                    Text("Accom.")
                },
                modifier = Modifier.weight(1f),
                colors =
                    FilterChipDefaults.filterChipColors()
            )

            FilterChip(
                selected = selectedFilter == "Travel",
                onClick = {
                    selectedFilter = "Travel"
                },
                label = {
                    Text("Travel")
                },
                modifier = Modifier.weight(1f),
                colors =
                    FilterChipDefaults.filterChipColors()
            )

            FilterChip(
                selected = selectedFilter == "Others",
                onClick = {
                    selectedFilter = "Others"
                },
                label = {
                    Text("Others")
                },
                modifier = Modifier.weight(1f),
                colors =
                    FilterChipDefaults.filterChipColors()
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
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
                    text =
                        if (itinerary.isEmpty()) {
                            "No itinerary items yet."
                        } else {
                            "No items in this category."
                        },
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text =
                        if (itinerary.isEmpty()) {
                            "Add your travel plans, accommodation and other key events."
                        } else {
                            "Try selecting a different filter."
                        },
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
                                MaterialTheme.typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.SemiBold,
                            color =
                                MaterialTheme.colorScheme
                                    .primary,
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

                            destinations =
                                itineraryDestinations[
                                    item.id
                                ] ?: emptyList(),

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
                Text("Add Item")
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
    destinations: List<String>,
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

                // -------------------------------------------------
                // BOOKED CHIP
                // -------------------------------------------------

                if (item.booked) {

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
            // TYPE
            // -----------------------------------------------------

            if (item.type.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

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

            // -----------------------------------------------------
            // DESTINATIONS
            // -----------------------------------------------------

            if (destinations.isNotEmpty()) {

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        "📍 " +
                                destinations.joinToString(
                                    separator = " • "
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
    date: LocalDate?
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