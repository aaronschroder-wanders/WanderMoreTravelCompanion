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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import java.time.format.DateTimeFormatter

@Composable
fun ItineraryDetailsScreen(
    itinerary: ItineraryEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {

    val dateFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy")

    val timeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        Text(
            text = "Itinerary Item",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = itinerary.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // -----------------------------------------------------
        // DETAILS
        // -----------------------------------------------------

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceContainer
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    // -------------------------------------------------
                    // DATE
                    // -------------------------------------------------

                    DetailRow(
                        label = "Date",
                        value = itinerary.date.format(
                            dateFormatter
                        )
                    )

                    // -------------------------------------------------
                    // TIME
                    // -------------------------------------------------

                    itinerary.time?.let { time ->

                        DetailRow(
                            label = "Time",
                            value = time.format(
                                timeFormatter
                            )
                        )
                    }

                    // -------------------------------------------------
                    // TYPE
                    // -------------------------------------------------

                    if (itinerary.type.isNotBlank()) {

                        DetailRow(
                            label = "Type",
                            value = itinerary.type
                        )
                    }

                    // -------------------------------------------------
                    // NIGHTS
                    // -------------------------------------------------

                    itinerary.nights?.let { nights ->

                        if (nights > 0) {

                            DetailRow(
                                label = "Nights",
                                value =
                                    if (nights == 1) {
                                        "1 night"
                                    } else {
                                        "$nights nights"
                                    }
                            )
                        }
                    }

                    // -------------------------------------------------
                    // LOCATION
                    // -------------------------------------------------

                    itinerary.location?.let { location ->

                        if (location.isNotBlank()) {

                            DetailRow(
                                label = "Location",
                                value = location
                            )
                        }
                    }

                    // -------------------------------------------------
                    // NOTES
                    // -------------------------------------------------

                    itinerary.notes?.let { notes ->

                        if (notes.isNotBlank()) {

                            DetailRow(
                                label = "Notes",
                                value = notes
                            )
                        }
                    }

                    // -------------------------------------------------
                    // LINKED BOOKING
                    // -------------------------------------------------

                    itinerary.bookingId?.let { bookingId ->

                        DetailRow(
                            label = "Booking",
                            value = "Booking #$bookingId"
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // ACTION BUTTONS
        // -----------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            ) {
                Text("Edit")
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // -----------------------------------------------------
        // DELETE
        // -----------------------------------------------------

        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Itinerary Item")
        }
    }
}


// =============================================================
// DETAIL ROW
// =============================================================

@Composable
private fun DetailRow(
    label: String,
    value: String
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}