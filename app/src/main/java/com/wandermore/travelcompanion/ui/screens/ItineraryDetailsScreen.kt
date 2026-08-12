package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "EEEE, dd MMM yyyy"
        )

    val timeFormatter =
        DateTimeFormatter.ofPattern(
            "HH:mm"
        )

    // =========================================================
    // DELETE CONFIRMATION DIALOG
    // =========================================================

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text(
                    text = "Delete Item?"
                )
            },

            text = {
                Text(
                    text =
                        "Are you sure you want to delete " +
                                "\"${itinerary.title}\"? " +
                                "This cannot be undone."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text(
                        text = "Delete",
                        color =
                            MaterialTheme.colorScheme.error,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // =========================================================
    // SCREEN
    // =========================================================

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // =====================================================
        // SCROLLABLE CONTENT
        // =====================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp)
        ) {

            // -------------------------------------------------
            // HEADER
            // -------------------------------------------------

            Text(
                text = "Itinerary Item",
                style =
                    MaterialTheme.typography.headlineMedium,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // -------------------------------------------------
            // MAIN TITLE CARD
            // -------------------------------------------------

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment =
                        Alignment.Top
                ) {

                    // -----------------------------------------
                    // TYPE SYMBOL
                    // -----------------------------------------

                    Text(
                        text =
                            itinerarySymbol(
                                itinerary.type
                            ),
                        style =
                            MaterialTheme.typography
                                .headlineMedium,
                        modifier =
                            Modifier.size(42.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.size(12.dp)
                    )

                    // -----------------------------------------
                    // TITLE + TYPE
                    // -----------------------------------------

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                itinerary.title,
                            style =
                                MaterialTheme.typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold
                        )

                        if (
                            itinerary.type.isNotBlank()
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    itinerary.type,
                                style =
                                    MaterialTheme.typography
                                        .bodyMedium,
                                color =
                                    MaterialTheme.colorScheme
                                        .primary,
                                fontWeight =
                                    FontWeight.Medium
                            )
                        }
                    }

                    // -----------------------------------------
                    // BOOKED
                    // -----------------------------------------

                    if (itinerary.booked) {

                        Spacer(
                            modifier =
                                Modifier.size(8.dp)
                        )

                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = "BOOKED",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            },
                            colors =
                                AssistChipDefaults
                                    .assistChipColors(
                                        containerColor =
                                            Color(0xFFB6FF00),
                                        labelColor =
                                            Color.Black
                                    )
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // -------------------------------------------------
            // DATE & TIME CARD
            // -------------------------------------------------

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    // -----------------------------------------
                    // DATE
                    // -----------------------------------------

                    DetailLine(
                        icon = "📅",
                        label = "Date",
                        value =
                            itinerary.date.format(
                                dateFormatter
                            )
                    )

                    // -----------------------------------------
                    // TIME
                    // -----------------------------------------

                    itinerary.time?.let { time ->

                        DetailLine(
                            icon = "🕐",
                            label = "Time",
                            value =
                                time.format(
                                    timeFormatter
                                )
                        )
                    }

                    // -----------------------------------------
                    // DEPARTURE
                    // -----------------------------------------

                    itinerary.nights?.let { nights ->

                        if (nights > 0) {

                            val departureDate =
                                itinerary.date.plusDays(
                                    nights.toLong()
                                )

                            DetailLine(
                                icon = "🚪",
                                label = "Departure",
                                value =
                                    departureDate.format(
                                        dateFormatter
                                    )
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------
            // LOCATION
            // -------------------------------------------------

            if (
                !itinerary.location
                    .isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                DetailSectionCard {

                    DetailLine(
                        icon = "📍",
                        label = "Location",
                        value =
                            itinerary.location!!
                    )
                }
            }

            // -------------------------------------------------
            // NIGHTS
            // -------------------------------------------------

            itinerary.nights?.let { nights ->

                if (nights > 0) {

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    DetailSectionCard {

                        DetailLine(
                            icon = "🛏",
                            label = "Stay",
                            value =
                                if (nights == 1) {
                                    "1 night"
                                } else {
                                    "$nights nights"
                                }
                        )
                    }
                }
            }

            // -------------------------------------------------
            // NOTES
            // -------------------------------------------------

            if (
                !itinerary.notes
                    .isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                    ) {

                        Text(
                            text = "Notes",
                            style =
                                MaterialTheme.typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.SemiBold,
                            color =
                                MaterialTheme.colorScheme
                                    .primary
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                itinerary.notes!!,
                            style =
                                MaterialTheme.typography
                                    .bodyLarge
                        )
                    }
                }
            }

            // -------------------------------------------------
            // LINKED BOOKING
            // -------------------------------------------------

            itinerary.bookingId?.let { bookingId ->

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                DetailSectionCard {

                    DetailLine(
                        icon = "🎫",
                        label = "Booking",
                        value =
                            "Booking #$bookingId"
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }

        // =====================================================
        // FIXED ACTION BUTTONS
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 10.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            // -------------------------------------------------
            // BACK
            // -------------------------------------------------

            Button(
                onClick = onBack,
                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Back")
            }

            // -------------------------------------------------
            // EDIT
            // -------------------------------------------------

            Button(
                onClick = onEdit,
                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Edit")
            }

            // -------------------------------------------------
            // DELETE
            // -------------------------------------------------

            Button(
                onClick = {
                    showDeleteDialog = true
                },
                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Delete")
            }
        }
    }
}

// =============================================================
// DETAIL SECTION CARD
// =============================================================

@Composable
private fun DetailSectionCard(
    content: @Composable () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        ) {

            content()
        }
    }
}

// =============================================================
// DETAIL LINE
// =============================================================

@Composable
private fun DetailLine(
    icon: String,
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.Top
    ) {

        Text(
            text = icon,
            style =
                MaterialTheme.typography
                    .titleMedium,
            modifier =
                Modifier.size(30.dp)
        )

        Spacer(
            modifier =
                Modifier.size(8.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = label,
                style =
                    MaterialTheme.typography
                        .labelLarge,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = value,
                style =
                    MaterialTheme.typography
                        .bodyLarge
            )
        }
    }
}

// =============================================================
// ITINERARY TYPE SYMBOL
// =============================================================

private fun itinerarySymbol(
    type: String
): String {

    return when (
        type.trim().lowercase()
    ) {

        "travel" ->
            "🚆"

        "accommodation" ->
            "🏨"

        "activity" ->
            "🎯"

        "attraction" ->
            "📸"

        "arrival" ->
            "🛬"

        "departure" ->
            "🛫"

        "other" ->
            "📌"

        else ->
            "📅"
    }
}