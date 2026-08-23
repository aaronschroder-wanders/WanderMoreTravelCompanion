package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
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
            // ACTIVITIES
            // =================================================

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
                        activity = activity,

                        onClick = {
                            onActivityClick(
                                activity.id
                            )
                        }
                    )
                }
            }

            // =================================================
            // ITINERARY
            // =================================================

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
                        itinerary = itinerary,

                        onClick = {
                            onItineraryClick(
                                itinerary.id
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
                    ),

            verticalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {

            // -------------------------------------------------
            // ACTIVITY NAME
            // -------------------------------------------------

            Text(
                text =
                    activity.name,

                style =
                    MaterialTheme.typography.titleMedium,

                fontWeight =
                    FontWeight.SemiBold
            )

            // -------------------------------------------------
            // ACTIVITY TYPE
            // -------------------------------------------------

            if (activity.type.isNotBlank()) {

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
            // ACTIVITY DATE
            // -------------------------------------------------

            activity.date?.let { date ->

                Text(
                    text =
                        "📅 " +
                                date.format(
                                    DateTimeFormatter.ofPattern(
                                        "dd MMM yyyy"
                                    )
                                ),

                    style =
                        MaterialTheme.typography.bodySmall,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
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
                    ),

            verticalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {

            // -------------------------------------------------
            // ITINERARY TITLE
            // -------------------------------------------------

            Text(
                text =
                    itinerary.title,

                style =
                    MaterialTheme.typography.titleMedium,

                fontWeight =
                    FontWeight.SemiBold
            )

            // -------------------------------------------------
            // ITINERARY TYPE
            // -------------------------------------------------

            if (itinerary.type.isNotBlank()) {

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
            // DATE + TIME
            // -------------------------------------------------

            Row(
                verticalAlignment =
                    androidx.compose.ui.Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "📅 " +
                                itinerary.date.format(
                                    DateTimeFormatter.ofPattern(
                                        "dd MMM yyyy"
                                    )
                                ),

                    style =
                        MaterialTheme.typography.bodySmall,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (itinerary.time != null) {

                    Spacer(
                        modifier =
                            Modifier.size(16.dp)
                    )

                    Text(
                        text =
                            "🕐 " +
                                    itinerary.time!!.format(
                                        DateTimeFormatter.ofPattern(
                                            "HH:mm"
                                        )
                                    ),

                        style =
                            MaterialTheme.typography.bodySmall,

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}