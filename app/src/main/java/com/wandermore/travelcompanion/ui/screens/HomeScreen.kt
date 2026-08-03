package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.ui.components.TripCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    trips: List<Trip>,
    onCreateTrip: () -> Unit,
    onTripSelected: (Trip) -> Unit
) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        item {

            Text(
                text = "Wander More Travel Companion",
                modifier = Modifier.padding(16.dp)
            )

        }


        if (trips.isEmpty()) {

            item {

                Text(
                    text = "No trips yet"
                )

            }

        } else {

            items(trips) { trip ->

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
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Create Trip"
                )

            }

        }
    }
}
