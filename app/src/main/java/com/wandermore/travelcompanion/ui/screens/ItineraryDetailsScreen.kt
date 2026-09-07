package com.wandermore.travelcompanion.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.ui.components.itinerarySymbol
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.net.URI
import java.time.format.DateTimeFormatter

@Composable
fun ItineraryDetailsScreen(
    itinerary: ItineraryEntity,
    tripViewModel: TripViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // DESTINATION STATE
    // =========================================================

    var destinationNames by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var destinationsLoaded by remember {
        mutableStateOf(false)
    }

    // =========================================================
    // CONTEXT
    // =========================================================

    val context = LocalContext.current

    // =========================================================
    // LIFECYCLE
    // =========================================================

    val lifecycleOwner =
        LocalLifecycleOwner.current

    // =========================================================
    // LOAD DESTINATIONS
    // =========================================================

    LaunchedEffect(
        lifecycleOwner,
        itinerary.id
    ) {

        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.RESUMED
        ) {

            destinationsLoaded = false

            val destinationIds =
                tripViewModel
                    .getDestinationIdsForItinerary(
                        itinerary.id
                    )

            val names =
                destinationIds.mapNotNull { destinationId ->

                    tripViewModel
                        .getDestinationById(
                            destinationId
                        )
                        ?.name
                }

            destinationNames = names

            destinationsLoaded = true
        }
    }

    // =========================================================
    // FORMATTERS
    // =========================================================

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "EEEE, dd MMM yyyy"
        )

    val timeFormatter =
        DateTimeFormatter.ofPattern(
            "HH:mm"
        )

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text("Delete Item?")
            },

            text = {
                Text(
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
        modifier =
            Modifier.fillMaxSize()
    ) {

        // =====================================================
        // SCROLLABLE CONTENT
        // =====================================================

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
        ) {

            // -------------------------------------------------
            // HEADER
            // -------------------------------------------------

            Text(
                text = "Itinerary Item",
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
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
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp),

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
                            Modifier.size(40.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.size(10.dp)
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
                                    Modifier.height(2.dp)
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

                    if (
                        itinerary.booked &&
                        itinerary.activityId == null
                    ) {

                        Spacer(
                            modifier =
                                Modifier.size(6.dp)
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
                modifier =
                    Modifier.height(10.dp)
            )

            // -------------------------------------------------
            // DATE / TIME / STAY
            // -------------------------------------------------

            DetailSectionCard {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    DetailLine(
                        icon = "📅",
                        label = "Date",
                        value =
                            itinerary.date.format(
                                dateFormatter
                            )
                    )

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

                    itinerary.nights?.let { nights ->

                        if (nights > 0) {

                            val departureDate =
                                itinerary.date.plusDays(
                                    nights.toLong()
                                )

                            DetailLine(
                                icon = "🛏",
                                label = "Stay",
                                value =
                                    if (nights == 1) {
                                        "1 night • Departure " +
                                                departureDate.format(
                                                    dateFormatter
                                                )
                                    } else {
                                        "$nights nights • Departure " +
                                                departureDate.format(
                                                    dateFormatter
                                                )
                                    }
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------
            // DESTINATIONS
            // -------------------------------------------------

            if (
                destinationsLoaded &&
                destinationNames.isNotEmpty()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                DetailSectionCard {

                    Column {

                        Text(
                            text = "Destinations",

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
                                Modifier.height(6.dp)
                        )

                        destinationNames.forEach { name ->

                            DetailLine(
                                icon = "📍",
                                label = "",
                                value = name
                            )

                            if (
                                name !=
                                destinationNames.last()
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(7.dp)
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------
            // WEB LINK
            //
            // Read-only here. The link is edited from the
            // Add/Edit Itinerary screens.
            // -------------------------------------------------

            if (
                !itinerary.webLink
                    .isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                DetailSectionCard {

                    Column {

                        Text(
                            text = "Web Link",

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
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                itinerary.webLink!!,

                            style =
                                MaterialTheme.typography
                                    .bodyMedium,

                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Button(
                            onClick = {

                                try {

                                    val uri =
                                        URI(
                                            itinerary.webLink!!
                                                .trim()
                                        )

                                    if (
                                        (uri.scheme.equals(
                                            "http",
                                            ignoreCase = true
                                        ) ||
                                                uri.scheme.equals(
                                                    "https",
                                                    ignoreCase = true
                                                )) &&
                                        !uri.host.isNullOrBlank()
                                    ) {

                                        val intent =
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(
                                                    itinerary.webLink!!
                                                        .trim()
                                                )
                                            )

                                        context.startActivity(
                                            intent
                                        )
                                    }

                                } catch (
                                    _: Exception
                                ) {
                                    // Do nothing if the link
                                    // cannot be opened safely.
                                }
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {
                            Text("Open Link")
                        }
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
                        Modifier.height(10.dp)
                )

                DetailSectionCard {

                    Column {

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
                                Modifier.height(6.dp)
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
                        Modifier.height(10.dp)
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
                    Modifier.height(12.dp)
            )
        }

        // =====================================================
        // FIXED ACTION BUTTONS
        // =====================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 6.dp,
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
                    .padding(14.dp)
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
                Modifier.size(28.dp)
        )

        Spacer(
            modifier =
                Modifier.size(7.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            if (label.isNotBlank()) {

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
                        Modifier.height(1.dp)
                )
            }

            Text(
                text = value,

                style =
                    MaterialTheme.typography
                        .bodyLarge
            )
        }
    }
}