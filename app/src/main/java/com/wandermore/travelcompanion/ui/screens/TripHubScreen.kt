package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel

@Composable
fun TripHubScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onItinerary: () -> Unit,
    onActivities: () -> Unit,
    onTodayTomorrow: () -> Unit,
    onToDo: () -> Unit,
    onDestinations: () -> Unit,
    onExpenses: () -> Unit,
    onEstimates: () -> Unit,
    onEditTrip: () -> Unit,
    onStartTrip: () -> Unit,
    onArchiveTrip: () -> Unit,
    onRestoreTrip: (TripStatus) -> Unit,
    onDeleteTrip: () -> Unit
) {

    var showRestoreDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(
            initial = null
        )

    val currentTrip = tripState ?: return

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // =====================================================
        // TRIP INFORMATION + MAIN TRIP AREAS
        // =====================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 4.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🌏",
                style = MaterialTheme.typography.displaySmall
            )

            Text(
                text = currentTrip.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(
                    top = 2.dp
                )
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "📅 ${formatDate(currentTrip.startDate)} - ${formatDate(currentTrip.endDate)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "💰 Home currency: ${currentTrip.homeCurrency}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Status: ${
                    currentTrip.status.name
                        .lowercase()
                        .replaceFirstChar {
                            it.uppercase()
                        }
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =================================================
            // MAIN TRIP AREAS
            //
            // The buttons share the available vertical space.
            // There is deliberately NO verticalScroll here.
            // =================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Button(
                    onClick = onItinerary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        "🗓️  Itinerary & Bookings"
                    )
                }

                Button(
                    onClick = onActivities,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        "⭐  Attractions & Activities"
                    )
                }

                Button(
                    onClick = onTodayTomorrow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        "🔵  Today / Tomorrow"
                    )
                }

                Button(
                    onClick = onToDo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        "✅  To Do"
                    )
                }

                Button(
                    onClick = onDestinations,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        "📍  Destinations"
                    )
                }

                // Estimates deliberately comes before Expenses.
                Button(
                    onClick = onEstimates,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        "📊  Estimates"
                    )
                }

                Button(
                    onClick = onExpenses,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        "💰  Expenses"
                    )
                }
            }
        }

        // =====================================================
        // TRIP ACTION BUTTONS
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 4.dp,
                    bottom = 12.dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = onEditTrip
            ) {
                Text(
                    "Edit Trip"
                )
            }

            when (currentTrip.status) {

                TripStatus.PLANNED -> {

                    Button(
                        onClick = onStartTrip
                    ) {
                        Text(
                            "Start Trip"
                        )
                    }
                }

                TripStatus.CURRENT -> {

                    Button(
                        onClick = onArchiveTrip
                    ) {
                        Text(
                            "Archive Trip"
                        )
                    }
                }

                TripStatus.ARCHIVED -> {

                    Button(
                        onClick = {
                            showRestoreDialog = true
                        }
                    ) {
                        Text(
                            "Restore"
                        )
                    }

                    TextButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Text(
                            "Delete Trip"
                        )
                    }
                }
            }
        }
    }

    // =========================================================
    // RESTORE DIALOG
    // =========================================================

    if (showRestoreDialog) {

        AlertDialog(
            onDismissRequest = {
                showRestoreDialog = false
            },

            title = {
                Text(
                    "Restore Trip?"
                )
            },

            text = {
                Text(
                    "Where should ${currentTrip.name} be moved?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        onRestoreTrip(
                            TripStatus.PLANNED
                        )

                        showRestoreDialog = false
                    }
                ) {
                    Text(
                        "Restore as Planned"
                    )
                }
            },

            dismissButton = {

                Button(
                    onClick = {

                        onRestoreTrip(
                            TripStatus.CURRENT
                        )

                        showRestoreDialog = false
                    }
                ) {
                    Text(
                        "Restore as Current"
                    )
                }
            }
        )
    }

    // =========================================================
    // DELETE DIALOG
    // =========================================================

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text(
                    "Delete Trip?"
                )
            },

            text = {
                Text(
                    "Are you sure you want to permanently delete ${currentTrip.name}?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {
                        onDeleteTrip()
                    }
                ) {
                    Text(
                        "Delete Permanently"
                    )
                }
            },

            dismissButton = {

                Button(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Cancel"
                    )
                }
            }
        )
    }
}