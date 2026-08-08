package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.ui.components.TripCard
import com.wandermore.travelcompanion.viewmodel.TripViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    trips: List<Trip>,
    tripViewModel: TripViewModel,
    onCreateTrip: () -> Unit,
    onArchivedTrips: () -> Unit,
    onSettings: () -> Unit,
    onTripSelected: (Trip) -> Unit
) {

    val currentTrips =
        trips.filter {
            it.status == TripStatus.CURRENT
        }

    val plannedTrips =
        trips.filter {
            it.status == TripStatus.PLANNED
        }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    bottom = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Wander More",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Travel Companion",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    top = 4.dp
                )
            )

            Text(
                text = "Plan. Track. Remember your journeys.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    top = 4.dp
                )
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            item {

                Text(
                    text = "Current Trip",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        16.dp
                    )
                )
            }

            if (currentTrips.isEmpty()) {

                item {

                    Text(
                        text = "No active trip",
                        modifier = Modifier.padding(
                            bottom = 16.dp
                        )
                    )
                }

            } else {

                items(currentTrips) { trip ->

                    val expenses by tripViewModel
                        .getExpensesForTrip(trip.id)
                        .collectAsState(
                            initial = emptyList()
                        )

                    val totalSpent =
                        expenses.sumOf {
                            it.convertedAmount
                        }

                    TripCard(
                        trip = trip,
                        totalSpent = totalSpent,
                        onClick = {
                            onTripSelected(trip)
                        }
                    )
                }
            }

            item {

                Text(
                    text = "Planned Trips",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        16.dp
                    )
                )
            }

            if (plannedTrips.isEmpty()) {

                item {

                    Text(
                        text = "No planned trips",
                        modifier = Modifier.padding(
                            bottom = 16.dp
                        )
                    )
                }

            } else {

                items(plannedTrips) { trip ->

                    val expenses by tripViewModel
                        .getExpensesForTrip(trip.id)
                        .collectAsState(
                            initial = emptyList()
                        )

                    val totalSpent =
                        expenses.sumOf {
                            it.convertedAmount
                        }

                    TripCard(
                        trip = trip,
                        totalSpent = totalSpent,
                        onClick = {
                            onTripSelected(trip)
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    bottom = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = onCreateTrip
            ) {
                Text("Create Trip")
            }

            Button(
                onClick = onArchivedTrips
            ) {
                Text("Archived Trips")
            }

            Button(
                onClick = onSettings
            ) {
                Text("Settings")
            }
        }
    }
}