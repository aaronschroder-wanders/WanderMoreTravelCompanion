package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    onToDo: () -> Unit,
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

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🌏",
                style = MaterialTheme.typography.displaySmall
            )

            Text(
                text = currentTrip.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
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
                        .replaceFirstChar { it.uppercase() }
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =================================================
            // MAIN TRIP AREAS
            // =================================================

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    onClick = onItinerary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(
                        "🗓️  Itinerary & Bookings"
                    )
                }

                Button(
                    onClick = onActivities,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(
                        "⭐  Attractions & Activities"
                    )
                }

                Button(
                    onClick = onToDo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(
                        "✅  To Do"
                    )
                }

                // Estimates deliberately comes before Expenses.
                Button(
                    onClick = onEstimates,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(
                        "📊  Estimates"
                    )
                }

                Button(
                    onClick = onExpenses,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
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
                    top = 8.dp,
                    bottom = 12.dp
                ),
            horizontalArrangement =
                Arrangement.SpaceEvenly,
            verticalAlignment =
                Alignment.CenterVertically
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
