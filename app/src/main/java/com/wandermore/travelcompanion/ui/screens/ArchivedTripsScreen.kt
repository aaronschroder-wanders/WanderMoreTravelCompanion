package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun ArchivedTripsScreen(
    trips: List<Trip>,
    tripViewModel: TripViewModel,
    onTripSelected: (Trip) -> Unit,
    onBack: () -> Unit
) {

    val archivedTrips =
        trips.filter {
            it.status == TripStatus.ARCHIVED
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = "Archived Trips",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Your completed journeys",
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

            if (archivedTrips.isEmpty()) {

                item {

                    Text(
                        text = "No archived trips yet.",
                        modifier = Modifier.padding(
                            top = 32.dp
                        )
                    )
                }

            } else {

                items(archivedTrips) { trip ->

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

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    top = 8.dp,
                    bottom = 16.dp
                )
        ) {

            Text(
                text = "Back to Trips"
            )
        }
    }
}