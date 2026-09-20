package com.wandermore.travelcompanion.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.wandermore.travelcompanion.database.ActivityEntity
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.database.ItineraryLinkEntity
import com.wandermore.travelcompanion.ui.components.itinerarySymbol
import com.wandermore.travelcompanion.util.formatMoney
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

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // ---------------------------------------------------------
    // DESTINATIONS
    // ---------------------------------------------------------

    var destinationNames by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    LaunchedEffect(
        itinerary.id,
        lifecycleOwner
    ) {

        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED
        ) {

            val destinationIds =
                tripViewModel
                    .getDestinationIdsForItinerary(
                        itinerary.id
                    )

            destinationNames =
                destinationIds.mapNotNull { destinationId ->

                    tripViewModel
                        .getDestinationById(
                            destinationId
                        )
                        ?.name
                }
        }
    }

    // ---------------------------------------------------------
    // LINKS / DOCUMENTS
    // ---------------------------------------------------------

    val links by tripViewModel
        .getItineraryLinks(itinerary.id)
        .collectAsState(initial = emptyList())

    // ---------------------------------------------------------
    // LINKED ACTIVITY
    //
    // Activity-created itinerary items retain the Activity ID.
    // The Activity remains the source of truth for estimated cost.
    // ---------------------------------------------------------

    var linkedActivity by remember {
        mutableStateOf<ActivityEntity?>(null)
    }

    LaunchedEffect(itinerary.activityId) {

        linkedActivity =
            itinerary.activityId?.let { activityId ->
                tripViewModel.getActivityById(activityId)
            }
    }

    // ---------------------------------------------------------
    // TRIP HOME CURRENCY
    //
    // Needed to display the converted Activity cost using the
    // same formatting as the Activities screen.
    // ---------------------------------------------------------

    var tripHomeCurrency by remember {
        mutableStateOf("NZD")
    }

    LaunchedEffect(itinerary.tripId) {

        val trip =
            tripViewModel.getTripById(
                itinerary.tripId
            )

        if (trip != null) {
            tripHomeCurrency = trip.homeCurrency
        }
    }

    // ---------------------------------------------------------
    // DELETE CONFIRMATION
    // ---------------------------------------------------------

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    // ---------------------------------------------------------
    // SCREEN
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Itinerary Item",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // -----------------------------------------------------
            // TITLE / TYPE / BOOKED
            // -----------------------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            itinerarySymbol(
                                itinerary.type
                            ),
                        style =
                            MaterialTheme.typography
                                .headlineMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.size(12.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text = itinerary.title,
                            style =
                                MaterialTheme.typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.SemiBold
                        )

                        if (itinerary.type.isNotBlank()) {

                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )

                            Text(
                                text = itinerary.type,
                                style =
                                    MaterialTheme.typography
                                        .bodyMedium,
                                color =
                                    MaterialTheme.colorScheme
                                        .primary
                            )
                        }
                    }

                    // Activity-created itinerary items are
                    // automatically booked and therefore do not
                    // need the separate BOOKED chip.
                    if (
                        itinerary.booked &&
                        itinerary.activityId == null
                    ) {

                        AssistChip(
                            onClick = {},
                            label = {
                                Text("BOOKED")
                            },
                            colors =
                                AssistChipDefaults
                                    .assistChipColors(
                                        containerColor =
                                            MaterialTheme
                                                .colorScheme
                                                .primaryContainer,
                                        labelColor =
                                            MaterialTheme
                                                .colorScheme
                                                .onPrimaryContainer
                                    )
                        )
                    }
                }
            }

            // -----------------------------------------------------
            // DATE / TIME / STAY
            // -----------------------------------------------------

            DetailSectionCard(
                title = "Date / Time"
            ) {

                DetailLine(
                    label = "Date",
                    value =
                        itinerary.date.format(
                            DateTimeFormatter.ofPattern(
                                "EEE, d MMM yyyy"
                            )
                        )
                )

                if (itinerary.time != null) {

                    DetailLine(
                        label = "Time",
                        value =
                            itinerary.time.format(
                                DateTimeFormatter.ofPattern(
                                    "HH:mm"
                                )
                            )
                    )
                }

                if (
                    itinerary.nights != null &&
                    itinerary.nights > 0
                ) {

                    DetailLine(
                        label = "Nights",
                        value =
                            itinerary.nights.toString()
                    )
                }
            }

            // -----------------------------------------------------
            // DESTINATIONS
            // -----------------------------------------------------

            if (destinationNames.isNotEmpty()) {

                DetailSectionCard(
                    title = "Destinations"
                ) {

                    destinationNames.forEach { name ->

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text = "📍",
                                modifier =
                                    Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.size(6.dp)
                            )

                            Text(
                                text = name,
                                style =
                                    MaterialTheme.typography
                                        .bodyLarge
                            )
                        }

                        if (
                            name !=
                            destinationNames.last()
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------
            // COST
            //
            // Only shown when this itinerary item is linked to
            // an Activity which has an estimated cost.
            // -----------------------------------------------------

            val activityCost =
                linkedActivity?.estimatedCost

            if (
                linkedActivity != null &&
                activityCost != null
            ) {

                DetailSectionCard(
                    title = "Cost"
                ) {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.Top
                    ) {

                        Text(
                            text = "Estimated cost",
                            style =
                                MaterialTheme.typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.SemiBold,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )

                        Column(
                            horizontalAlignment =
                                Alignment.End
                        ) {

                            Text(
                                text =
                                    formatActivityCost(
                                        activityCost,
                                        linkedActivity
                                            ?.currency
                                    ),
                                style =
                                    MaterialTheme.typography
                                        .titleMedium,
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            if (
                                linkedActivity
                                    ?.convertedAmount != null
                            ) {

                                Text(
                                    text =
                                        "≈ ${
                                            formatMoney(
                                                linkedActivity
                                                    ?.convertedAmount!!,
                                                tripHomeCurrency
                                            )
                                        }",
                                    style =
                                        MaterialTheme.typography
                                            .bodyMedium,
                                    color =
                                        MaterialTheme.colorScheme
                                            .primary
                                )
                            }
                        }
                    }
                }
            }

            // -----------------------------------------------------
            // LINKS / DOCUMENTS
            // -----------------------------------------------------

            if (links.isNotEmpty()) {

                DetailSectionCard(
                    title = "Links / Documents"
                ) {

                    links.forEach { link ->

                        TextButton(
                            onClick = {
                                openItineraryLink(
                                    context,
                                    link.url
                                )
                            }
                        ) {

                            Text(
                                text =
                                    link.label.ifBlank {
                                        link.url
                                    }
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------
            // LEGACY WEB LINK
            //
            // Retained for older itinerary records which still
            // contain the original webLink value.
            // -----------------------------------------------------

            if (
                links.isEmpty() &&
                !itinerary.webLink.isNullOrBlank()
            ) {

                DetailSectionCard(
                    title = "Web Link"
                ) {

                    TextButton(
                        onClick = {
                            openItineraryLink(
                                context,
                                itinerary.webLink!!
                            )
                        }
                    ) {

                        Text(
                            text = itinerary.webLink!!
                        )
                    }
                }
            }

            // -----------------------------------------------------
            // NOTES
            // -----------------------------------------------------

            if (!itinerary.notes.isNullOrBlank()) {

                DetailSectionCard(
                    title = "Notes"
                ) {

                    Text(
                        text = itinerary.notes!!,
                        style =
                            MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // -----------------------------------------------------
            // LINKED BOOKING
            // -----------------------------------------------------

            if (itinerary.bookingId != null) {

                DetailSectionCard(
                    title = "Linked Booking"
                ) {

                    DetailLine(
                        label = "Booking ID",
                        value =
                            itinerary.bookingId.toString()
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ---------------------------------------------------------
        // BOTTOM BUTTONS
        // ---------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = onBack,
                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = onEdit,
                modifier =
                    Modifier.weight(1f)
            ) {
                Text("Edit")
            }

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

    // ---------------------------------------------------------
    // DELETE DIALOG
    // ---------------------------------------------------------

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text("Delete itinerary item?")
            },

            text = {
                Text(
                    "Are you sure you want to delete \"${itinerary.title}\"?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete")
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
}


// =================================================================
// OPEN ITINERARY LINK
// =================================================================

private fun openItineraryLink(
    context: android.content.Context,
    url: String
) {

    try {

        val uri =
            URI(url.trim())

        val intent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(uri.toString())
            )

        context.startActivity(intent)

    } catch (_: Exception) {
        // Ignore invalid or unsupported URLs.
    }
}


// =================================================================
// DETAIL SECTION CARD
// =================================================================

@Composable
private fun DetailSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme
                        .surfaceContainer
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        ) {

            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            content()
        }
    }
}


// =================================================================
// DETAIL LINE
// =================================================================

@Composable
private fun DetailLine(
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.Top
    ) {

        Text(
            text = label,
            style =
                MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
        )

        Spacer(
            modifier =
                Modifier.size(12.dp)
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodyLarge,
            fontWeight =
                FontWeight.Medium
        )
    }
}


// =================================================================
// CURRENCY DISPLAY FORMATTING
// =================================================================

private fun formatActivityCost(
    amount: Double,
    currency: String?
): String {

    if (currency.isNullOrBlank()) {

        return String.format(
            java.util.Locale.US,
            "%,.2f",
            amount
        )
    }

    return formatMoney(
        amount,
        currency
    )
}