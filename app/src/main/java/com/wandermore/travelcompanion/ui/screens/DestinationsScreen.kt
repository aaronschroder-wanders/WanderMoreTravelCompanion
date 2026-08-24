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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    tripId: Long?,
    tripViewModel: TripViewModel,
    onDestinationClick: (Long) -> Unit,
    onBack: () -> Unit
) {

    // =========================================================
    // DESTINATIONS
    // =========================================================

    val destinations by if (tripId != null) {

        tripViewModel
            .getDestinationsForTrip(tripId)
            .collectAsState(
                initial = emptyList()
            )

    } else {

        tripViewModel
            .getAllDestinations()
            .collectAsState(
                initial = emptyList()
            )
    }

    var summaries by remember {
        mutableStateOf(
            emptyMap<Long, DestinationSummary>()
        )
    }

    // =========================================================
    // SETTINGS DIALOG STATE
    // =========================================================

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var destinationToRename by remember {
        mutableStateOf<DestinationEntity?>(null)
    }

    var destinationToArchive by remember {
        mutableStateOf<DestinationEntity?>(null)
    }

    var destinationToDelete by remember {
        mutableStateOf<DestinationEntity?>(null)
    }

    var destinationToUnarchive by remember {
        mutableStateOf<DestinationEntity?>(null)
    }

    var deleteErrorMessage by remember {
        mutableStateOf<String?>(null)
    }

    // =========================================================
    // LOAD DESTINATION SUMMARY COUNTS
    // =========================================================

    LaunchedEffect(
        tripId,
        destinations
    ) {

        if (tripId != null) {

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
                        activityCount = activities.size,
                        itineraryCount = itineraryItems.size
                    )
                }

            summaries = newSummaries

        } else {

            summaries = emptyMap()
        }
    }

    // =========================================================
    // FILTER TRIP DESTINATIONS
    // =========================================================

    val visibleDestinations =
        if (tripId != null) {

            destinations.filter { destination ->

                val summary =
                    summaries[destination.id]

                summary != null &&
                        (
                                summary.activityCount > 0 ||
                                        summary.itineraryCount > 0
                                )
            }

        } else {

            destinations.filter {
                it.active
            }
        }

    val archivedDestinations =
        if (tripId == null) {

            destinations.filter {
                !it.active
            }

        } else {

            emptyList()
        }

    Column(
        modifier =
            Modifier
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
                        if (tripId != null) {
                            "Places you're visiting on this trip"
                        } else {
                            "Your saved travel destinations"
                        },

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,

                    modifier =
                        Modifier.padding(
                            top = 2.dp
                        )
                )
            }
        }

        // =====================================================
        // SETTINGS: ADD BUTTON
        // =====================================================

        if (tripId == null) {

            Button(
                onClick = {
                    showAddDialog = true
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "+ Add Destination"
                )
            }
        }

        // =====================================================
        // DESTINATION LIST
        // =====================================================

        if (
            visibleDestinations.isEmpty() &&
            archivedDestinations.isEmpty()
        ) {

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
                        if (tripId != null) {
                            "No destinations yet"
                        } else {
                            "No destinations saved"
                        },

                    style =
                        MaterialTheme.typography.titleMedium,

                    modifier =
                        Modifier.padding(
                            top = 12.dp
                        )
                )

                Text(
                    text =
                        if (tripId != null) {
                            "Destinations will appear here when you add a location to an activity or itinerary item."
                        } else {
                            "Add a destination to build your saved destination list."
                        },

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,

                    modifier =
                        Modifier.padding(
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
                    Arrangement.spacedBy(10.dp)
            ) {

                // =================================================
                // ACTIVE DESTINATIONS
                // =================================================

                if (
                    tripId == null &&
                    visibleDestinations.isNotEmpty()
                ) {

                    item {

                        Text(
                            text = "Active Destinations",

                            style =
                                MaterialTheme.typography.titleLarge
                        )
                    }
                }

                items(
                    items = visibleDestinations,
                    key = {
                        it.id
                    }
                ) { destination ->

                    val summary =
                        summaries[destination.id]
                            ?: DestinationSummary()

                    DestinationCard(
                        destination =
                            destination,

                        summary =
                            summary,

                        showSummary =
                            tripId != null,

                        clickable =
                            tripId != null,

                        managementMode =
                            tripId == null,

                        onClick = {

                            onDestinationClick(
                                destination.id
                            )
                        },

                        onRename = {

                            destinationToRename =
                                destination
                        },

                        onArchive = {

                            destinationToArchive =
                                destination
                        },

                        onUnarchive = null,

                        onDelete = {

                            destinationToDelete =
                                destination
                        }
                    )
                }

                // =================================================
                // ARCHIVED DESTINATIONS
                // =================================================

                if (
                    tripId == null &&
                    archivedDestinations.isNotEmpty()
                ) {

                    item {

                        Spacer(
                            modifier =
                                Modifier.size(6.dp)
                        )

                        Text(
                            text = "Archived Destinations",

                            style =
                                MaterialTheme.typography.titleLarge
                        )
                    }

                    items(
                        items = archivedDestinations,

                        key = {
                            "archived_${it.id}"
                        }
                    ) { destination ->

                        DestinationCard(
                            destination =
                                destination,

                            summary =
                                DestinationSummary(),

                            showSummary =
                                false,

                            clickable =
                                false,

                            managementMode =
                                true,

                            onClick = {},

                            onRename = {

                                destinationToRename =
                                    destination
                            },

                            onArchive = null,

                            onUnarchive = {

                                destinationToUnarchive =
                                    destination
                            },

                            onDelete = {

                                destinationToDelete =
                                    destination
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
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Back"
            )
        }
    }

    // =========================================================
    // ADD DESTINATION DIALOG
    // =========================================================

    if (showAddDialog) {

        var name by remember {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
            },

            title = {
                Text(
                    text = "Add Destination"
                )
            },

            text = {

                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text(
                            text = "Destination name"
                        )
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val trimmed =
                            name.trim()

                        if (trimmed.isNotBlank()) {

                            tripViewModel.addDestination(
                                trimmed
                            )

                            showAddDialog = false
                        }
                    }
                ) {

                    Text(
                        text = "Add"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showAddDialog = false
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }

    // =========================================================
    // RENAME DIALOG
    // =========================================================

    destinationToRename?.let { destination ->

        var name by remember(
            destination.id
        ) {
            mutableStateOf(
                destination.name
            )
        }

        AlertDialog(
            onDismissRequest = {
                destinationToRename = null
            },

            title = {
                Text(
                    text = "Rename Destination"
                )
            },

            text = {

                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text(
                            text = "Destination name"
                        )
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val trimmed =
                            name.trim()

                        if (trimmed.isNotBlank()) {

                            tripViewModel.updateDestination(
                                destination.copy(
                                    name = trimmed
                                )
                            )

                            destinationToRename = null
                        }
                    }
                ) {

                    Text(
                        text = "Save"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        destinationToRename = null
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }

    // =========================================================
    // ARCHIVE DIALOG
    // =========================================================

    destinationToArchive?.let { destination ->

        AlertDialog(
            onDismissRequest = {
                destinationToArchive = null
            },

            title = {
                Text(
                    text = "Archive Destination?"
                )
            },

            text = {
                Text(
                    text =
                        "Archive ${destination.name}? It will no longer appear in active destination lists, but it will not be deleted."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        tripViewModel.setDestinationActive(
                            destination.id,
                            false
                        )

                        destinationToArchive = null
                    }
                ) {

                    Text(
                        text = "Archive"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        destinationToArchive = null
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }

    // =========================================================
    // UNARCHIVE DIALOG
    // =========================================================

    destinationToUnarchive?.let { destination ->

        AlertDialog(
            onDismissRequest = {
                destinationToUnarchive = null
            },

            title = {
                Text(
                    text = "Restore Destination?"
                )
            },

            text = {
                Text(
                    text =
                        "Restore ${destination.name} to your active destinations?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        tripViewModel.setDestinationActive(
                            destination.id,
                            true
                        )

                        destinationToUnarchive = null
                    }
                ) {

                    Text(
                        text = "Restore"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        destinationToUnarchive = null
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }

    // =========================================================
    // DELETE DIALOG
    // =========================================================

    destinationToDelete?.let { destination ->

        AlertDialog(
            onDismissRequest = {
                destinationToDelete = null
            },

            title = {
                Text(
                    text = "Delete Destination?"
                )
            },

            text = {

                Text(
                    text =
                        "Delete ${destination.name} permanently? This cannot be undone."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        tripViewModel.deleteDestinationFromSettings(
                            destination.id,
                            onDeleted = {
                                destinationToDelete = null
                            },
                            onBlocked = { message ->
                                destinationToDelete = null
                                deleteErrorMessage = message
                            }
                        )
                    }
                ) {

                    Text(
                        text = "Delete"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        destinationToDelete = null
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }

    // =========================================================
    // DELETE BLOCKED DIALOG
    // =========================================================

    deleteErrorMessage?.let { message ->

        AlertDialog(
            onDismissRequest = {
                deleteErrorMessage = null
            },

            title = {
                Text(
                    text = "Destination In Use"
                )
            },

            text = {
                Text(
                    text = message
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        deleteErrorMessage = null
                    }
                ) {

                    Text(
                        text = "OK"
                    )
                }
            }
        )
    }
}


// =============================================================
// DESTINATION CARD
// =============================================================

@Composable
private fun DestinationCard(
    destination: DestinationEntity,
    summary: DestinationSummary,
    showSummary: Boolean,
    clickable: Boolean,
    managementMode: Boolean,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onArchive: (() -> Unit)?,
    onUnarchive: (() -> Unit)?,
    onDelete: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(
                    if (clickable) {
                        Modifier.clickable(
                            onClick = onClick
                        )
                    } else {
                        Modifier
                    }
                ),

        shape =
            RoundedCornerShape(12.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme
                        .surfaceVariant
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier =
                        Modifier.size(34.dp),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "📍",

                        style =
                            MaterialTheme.typography.titleLarge
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(
                                start = 8.dp
                            )
                ) {

                    Text(
                        text =
                            destination.name,

                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    if (showSummary) {

                        Spacer(
                            modifier =
                                Modifier.size(2.dp)
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(12.dp)
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
                                    MaterialTheme.typography.bodySmall,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
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
                                    MaterialTheme.typography.bodySmall,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }
                }

                if (clickable) {

                    Text(
                        text = "›",

                        style =
                            MaterialTheme.typography.headlineMedium,

                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }
            }

            // =================================================
            // SETTINGS MANAGEMENT BUTTONS
            // =================================================

            if (managementMode) {

                Spacer(
                    modifier =
                        Modifier.size(6.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    OutlinedButton(
                        onClick = onRename,

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Rename"
                        )
                    }

                    if (onArchive != null) {

                        OutlinedButton(
                            onClick = onArchive,

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Archive"
                            )
                        }
                    }

                    if (onUnarchive != null) {

                        OutlinedButton(
                            onClick = onUnarchive,

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Restore"
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onDelete,

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Delete"
                        )
                    }
                }
            }
        }
    }
}