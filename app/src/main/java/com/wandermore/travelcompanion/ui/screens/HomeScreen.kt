package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        // =====================================================
        // WANDER MORE HEADER
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 20.dp,
                    bottom = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Wander More",
                style = MaterialTheme.typography.displaySmall,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00A6A6)
            )

            Text(
                text = "Travel Companion",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF2A900),
                modifier = Modifier.padding(
                    top = 2.dp
                )
            )

            Text(
                text = "For wherever the journey takes you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    top = 6.dp
                )
            )

            Row(
                modifier = Modifier.padding(
                    top = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Text(
                    text = "•",
                    color = Color(0xFF00A6A6),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "•",
                    color = Color(0xFFF2A900),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "•",
                    color = Color(0xFF00A6A6),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // =====================================================
        // TRIP LIST
        // =====================================================

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // -------------------------------------------------
            // CURRENT TRIPS
            // -------------------------------------------------

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

                items(
                    items = currentTrips
                ) { trip ->

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

            // -------------------------------------------------
            // PLANNED TRIPS
            // -------------------------------------------------

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

                items(
                    items = plannedTrips
                ) { trip ->

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

        // =====================================================
        // BOTTOM BUTTONS
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    bottom = 16.dp
                )
        ) {

            Button(
                onClick = onCreateTrip,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(
                    horizontal = 4.dp
                )
            ) {
                Text("Create Trip")
            }

            Button(
                onClick = onArchivedTrips,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(
                    horizontal = 4.dp
                )
            ) {
                Text("Archived Trips")
            }

            Button(
                onClick = onSettings,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(
                    horizontal = 4.dp
                )
            ) {
                Text("Settings")
            }
        }
    }
}