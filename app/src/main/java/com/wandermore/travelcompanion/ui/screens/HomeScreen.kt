package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.ui.components.TripCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    trips: List<Trip>,
    onCreateTrip: () -> Unit,
    onTripSelected: (Trip) -> Unit
) {

    val currentTrips =
        trips.filter { it.status == TripStatus.CURRENT }

    val plannedTrips =
        trips.filter { it.status == TripStatus.PLANNED }

    val archivedTrips =
        trips.filter { it.status == TripStatus.ARCHIVED }


    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        item {

            Text(
                text = "Wander More",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(
                    top = 24.dp,
                    bottom = 4.dp
                )
            )

            Text(
                text = "Travel Companion",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    bottom = 4.dp
                )
            )

            Text(
                text = "Plan. Track. Remember your journeys.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    bottom = 24.dp
                )
            )

        }


        if (currentTrips.isNotEmpty()) {

            item {

                Text(
                    text = "Current Trips",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

            }


            items(currentTrips) { trip ->

                TripCard(
                    trip = trip,
                    onClick = {
                        onTripSelected(trip)
                    }
                )

            }

        }



        if (plannedTrips.isNotEmpty()) {

            item {

                Text(
                    text = "Planned Trips",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

            }


            items(plannedTrips) { trip ->

                TripCard(
                    trip = trip,
                    onClick = {
                        onTripSelected(trip)
                    }
                )

            }

        }



        if (archivedTrips.isNotEmpty()) {

            item {

                Text(
                    text = "Archived Trips",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

            }


            items(archivedTrips) { trip ->

                TripCard(
                    trip = trip,
                    onClick = {
                        onTripSelected(trip)
                    }
                )

            }

        }



        item {

            Button(
                onClick = onCreateTrip,
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "Create Trip"
                )

            }

        }

    }
}