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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
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
    navBackStackEntry: NavBackStackEntry,
    onActivityClick: (Long) -> Unit,
    onItineraryClick: (Long) -> Unit,
    onBack: () -> Unit
) {

    var destination by remember {
        mutableStateOf<DestinationEntity?>(null)
    }

    var activities by remember {
        mutableStateOf<List<ActivityEntity>>(emptyList())
    }

    var itineraryItems by remember {
        mutableStateOf<List<ItineraryEntity>>(emptyList())
    }

    var refreshTrigger by remember {
        mutableStateOf(0)
    }

    // =========================================================
    // LOAD DESTINATION DATA
    // =========================================================

    suspend fun loadDestinationData() {

        destination =
            tripViewModel
                .getDestinationByIdFlow(destinationId)
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

    // =========================================================
    // INITIAL LOAD AND REFRESH
    // =========================================================

    LaunchedEffect(
        tripId,
        destinationId,
        refreshTrigger
    ) {

        loadDestinationData()
    }

    // =========================================================
    // REFRESH WHEN RETURNING FROM EDIT SCREEN
    // =========================================================

    DisposableEffect(navBackStackEntry) {

        val observer =
            LifecycleEventObserver { _, event ->

                if (event == Lifecycle.Event.ON_RESUME) {
                    refreshTrigger++
                }
            }

        navBackStackEntry.lifecycle.addObserver(observer)

        onDispose {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    }

    // =========================================================
    // DATE FORMATTERS
    // =========================================================

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "EEEE, dd MMM yyyy"
        )

    val shortDateFormatter =
        DateTimeFormatter.ofPattern(
            "dd MMM"
        )

    val timeFormatter =
        DateTimeFormatter.ofPattern(
            "HH:mm"
        )

    // =========================================================
    // GROUP ITINERARY ITEMS BY DATE
    // =========================================================

    val itineraryGroups =
        itineraryItems
            .groupBy {
                it.date
            }
            .toSortedMap()

    // =========================================================
    // GROUP ACTIVITIES BY DATE
    //
    // Activities can have a null date, so those are handled
    // separately and displayed at the end.
    // =========================================================

    val datedActivityGroups =
        activities
            .filter {
                it.date != null
            }
            .groupBy {
                it.date!!
            }
            .toSortedMap()

    val undatedActivities =
        activities.filter {
            it.date == null
        }

    // =========================================================
    // SCREEN
    // =========================================================

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        // =====================================================
        // DESTINATION TITLE
        // =====================================================

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

        // =====================================================
        // CONTENT
        // =====================================================

        LazyColumn(
            modifier =
                Modifier.weight(1f),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // =================================================
            // ITINERARY SECTION
            // =================================================

            item {

                Text(
                    text = "Itinerary",

                    style =
                        MaterialTheme.typography.titleLarge
                )
            }

            if (itineraryGroups.isEmpty()) {

                item {

                    Text(
                        text =
                            "No itinerary items at this destination.",

                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                itineraryGroups.forEach { (date, itemsForDate) ->

                    // -----------------------------------------
                    // DATE HEADER
                    // -----------------------------------------

                    item(
                        key = "itinerary_date_$date"
                    ) {

                        Text(
                            text =
                                date.format(
                                    dateFormatter
                                ),

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

                    // -----------------------------------------
                    // ITEMS FOR DATE
                    // -----------------------------------------

                    items(
                        items =
                            itemsForDate.sortedWith(
                                compareBy<ItineraryEntity> {
                                    it.time
                                }
                            ),

                        key = {
                            "itinerary_${it.id}"
                        }

                    ) { itinerary ->

                        ItineraryDestinationCard(
                            itinerary =
                                itinerary,

                            timeFormatter =
                                timeFormatter,

                            shortDateFormatter =
                                shortDateFormatter,

                            onClick = {
                                onItineraryClick(
                                    itinerary.id
                                )
                            }
                        )
                    }
                }
            }

            // =================================================
            // ACTIVITIES SECTION
            // =================================================

            item {

                Text(
                    text = "Activities",

                    style =
                        MaterialTheme.typography.titleLarge,

                    modifier =
                        Modifier.padding(
                            top = 12.dp
                        )
                )
            }

            if (
                datedActivityGroups.isEmpty() &&
                undatedActivities.isEmpty()
            ) {

                item {

                    Text(
                        text =
                            "No activities at this destination.",

                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                // ---------------------------------------------
                // DATED ACTIVITIES
                // ---------------------------------------------

                datedActivityGroups.forEach {
                        (date, activitiesForDate) ->

                    item(
                        key = "activity_date_$date"
                    ) {

                        Text(
                            text =
                                date.format(
                                    dateFormatter
                                ),

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

                    items(
                        items =
                            activitiesForDate.sortedWith(
                                compareBy<ActivityEntity> {
                                    it.startTime
                                }
                            ),

                        key = {
                            "activity_${it.id}"
                        }

                    ) { activity ->

                        ActivityDestinationCard(
                            activity =
                                activity,

                            timeFormatter =
                                timeFormatter,

                            onClick = {
                                onActivityClick(
                                    activity.id
                                )
                            }
                        )
                    }
                }

                // ---------------------------------------------
                // UNDATED ACTIVITIES
                // ---------------------------------------------

                if (undatedActivities.isNotEmpty()) {

                    item(
                        key = "activity_undated_header"
                    ) {

                        Text(
                            text = "Date not set",

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

                    items(
                        items = undatedActivities,

                        key = {
                            "activity_${it.id}"
                        }

                    ) { activity ->

                        ActivityDestinationCard(
                            activity =
                                activity,

                            timeFormatter =
                                timeFormatter,

                            onClick = {
                                onActivityClick(
                                    activity.id
                                )
                            }
                        )
                    }
                }
            }
        }

        // =====================================================
        // BACK
        // =====================================================

        Button(
            onClick = onBack,

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


// =================================================================
// ACTIVITY DESTINATION CARD
// =================================================================

@Composable
private fun ActivityDestinationCard(
    activity: ActivityEntity,
    timeFormatter: DateTimeFormatter,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    )
        ) {

            // -------------------------------------------------
            // TITLE ROW
            // -------------------------------------------------

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        activitySymbol(
                            activity.type
                        ),

                    style =
                        MaterialTheme.typography.titleMedium,

                    modifier =
                        Modifier.size(26.dp)
                )

                Spacer(
                    modifier =
                        Modifier.size(8.dp)
                )

                Text(
                    text =
                        activity.name,

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight =
                        FontWeight.SemiBold,

                    modifier =
                        Modifier.weight(1f)
                )
            }

            // -------------------------------------------------
            // ACTIVITY TYPE
            // -------------------------------------------------

            if (activity.type.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        activity.type,

                    style =
                        MaterialTheme.typography.labelSmall,

                    color =
                        MaterialTheme.colorScheme.primary,

                    fontWeight =
                        FontWeight.Medium
                )
            }

            // -------------------------------------------------
            // ACTIVITY TIME
            // -------------------------------------------------

            activity.startTime?.let { time ->

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "🕐 " +
                                time.format(
                                    timeFormatter
                                ),

                    style =
                        MaterialTheme.typography.bodySmall,

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


// =================================================================
// ITINERARY DESTINATION CARD
// =================================================================

@Composable
private fun ItineraryDestinationCard(
    itinerary: ItineraryEntity,
    timeFormatter: DateTimeFormatter,
    shortDateFormatter: DateTimeFormatter,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    )
        ) {

            // -------------------------------------------------
            // TITLE ROW
            // -------------------------------------------------

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        itinerarySymbol(
                            itinerary.type
                        ),

                    style =
                        MaterialTheme.typography.titleMedium,

                    modifier =
                        Modifier.size(26.dp)
                )

                Spacer(
                    modifier =
                        Modifier.size(8.dp)
                )

                Text(
                    text =
                        itinerary.title,

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight =
                        FontWeight.SemiBold,

                    modifier =
                        Modifier.weight(1f)
                )

                // -------------------------------------------------
                // BOOKED CHIP
                // -------------------------------------------------

                if (
                    itinerary.booked &&
                    itinerary.activityId == null
                ) {

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

            // -------------------------------------------------
            // ITINERARY TYPE
            // -------------------------------------------------

            if (itinerary.type.isNotBlank()) {

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        itinerary.type,

                    style =
                        MaterialTheme.typography.labelSmall,

                    color =
                        MaterialTheme.colorScheme.primary,

                    fontWeight =
                        FontWeight.Medium
                )
            }

            // -------------------------------------------------
            // TIME / NIGHTS / DEPARTURE
            // -------------------------------------------------

            val hasTime =
                itinerary.time != null

            val hasNights =
                itinerary.nights != null &&
                        itinerary.nights > 0

            if (hasTime || hasNights) {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // -----------------------------------------
                    // TIME
                    // -----------------------------------------

                    if (hasTime) {

                        Text(
                            text =
                                "🕐 " +
                                        itinerary.time!!
                                            .format(
                                                timeFormatter
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

                    // -----------------------------------------
                    // SPACE BETWEEN TIME AND NIGHTS
                    // -----------------------------------------

                    if (
                        hasTime &&
                        hasNights
                    ) {

                        Spacer(
                            modifier =
                                Modifier.size(16.dp)
                        )
                    }

                    // -----------------------------------------
                    // NIGHTS
                    // -----------------------------------------

                    if (hasNights) {

                        Text(
                            text =
                                if (
                                    itinerary.nights == 1
                                ) {
                                    "🛏 1 night"
                                } else {
                                    "🛏 ${itinerary.nights} nights"
                                },

                            style =
                                MaterialTheme.typography
                                    .bodySmall,

                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    // -----------------------------------------
                    // SPACE BEFORE DEPARTURE
                    // -----------------------------------------

                    if (hasNights) {

                        Spacer(
                            modifier =
                                Modifier.size(16.dp)
                        )

                        Text(
                            text =
                                "→ " +
                                        itinerary.date
                                            .plusDays(
                                                itinerary.nights!!
                                                    .toLong()
                                            )
                                            .format(
                                                shortDateFormatter
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
// ACTIVITY TYPE SYMBOL
// =================================================================

private fun activitySymbol(
    type: String
): String {

    return when (
        type.trim().lowercase()
    ) {

        "activity" ->
            "🎯"

        "attraction" ->
            "📸"

        else ->
            "🎯"
    }
}