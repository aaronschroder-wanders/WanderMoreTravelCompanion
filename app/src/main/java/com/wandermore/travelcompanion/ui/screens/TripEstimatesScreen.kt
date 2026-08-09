package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.TripEstimateEntity
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun TripEstimatesScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onAddEstimate: () -> Unit,
    onEditEstimate: (Long) -> Unit,
    onBack: () -> Unit
) {

    // ---------------------------------------------------------
    // LOAD TRIP
    // ---------------------------------------------------------

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(
            initial = null
        )

    val currentTrip = tripState ?: return

    // ---------------------------------------------------------
    // LOAD ESTIMATES
    // ---------------------------------------------------------

    val estimates by tripViewModel
        .getTripEstimatesForTrip(currentTrip.id)
        .collectAsState(
            initial = emptyList()
        )

    // ---------------------------------------------------------
    // TRIP DAYS / NIGHTS
    // ---------------------------------------------------------

    val tripDays =
        ChronoUnit.DAYS.between(
            currentTrip.startDate,
            currentTrip.endDate
        ) + 1

    val tripNights =
        ChronoUnit.DAYS.between(
            currentTrip.startDate,
            currentTrip.endDate
        )

    // ---------------------------------------------------------
    // SCREEN
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        Text(
            text = "📊 Trip Estimates",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = currentTrip.name,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "${tripDays} days • ${tripNights} nights • Home currency: ${currentTrip.homeCurrency}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // -----------------------------------------------------
        // ESTIMATE LIST
        // -----------------------------------------------------

        if (estimates.isEmpty()) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "No estimates have been added yet.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Add estimates for accommodation, food, transport and other trip costs.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = estimates,
                    key = { it.id }
                ) { estimate ->

                    TripEstimateCard(
                        estimate = estimate,
                        homeCurrency = currentTrip.homeCurrency,
                        tripDays = tripDays,
                        tripNights = tripNights,
                        onClick = {
                            onEditEstimate(
                                estimate.id
                            )
                        }
                    )
                }
            }
        }

        // -----------------------------------------------------
        // BOTTOM BUTTONS
        // -----------------------------------------------------

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = onBack
            ) {
                Text(
                    "Back"
                )
            }

            Button(
                onClick = onAddEstimate
            ) {
                Text(
                    "Add Estimate"
                )
            }
        }
    }
}

// =============================================================
// ESTIMATE CARD
// =============================================================

@Composable
private fun TripEstimateCard(
    estimate: TripEstimateEntity,
    homeCurrency: String,
    tripDays: Long,
    tripNights: Long,
    onClick: () -> Unit
) {

    val multiplier =
        when (estimate.estimateType) {

            "PER_NIGHT" -> tripNights

            "PER_DAY" -> tripDays

            else -> 1L
        }

    // ---------------------------------------------------------
    // IMPORTANT
    //
    // amount = original amount entered by the traveller
    // convertedAmount = amount converted to home currency
    // ---------------------------------------------------------

    val calculatedAmount =
        estimate.convertedAmount * multiplier

    // ---------------------------------------------------------
    // SHOW THE ORIGINAL CURRENCY FOR THE BASIS
    // ---------------------------------------------------------

    val basisText =
        when (estimate.estimateType) {

            "PER_NIGHT" ->
                "${formatMoney(estimate.amount, estimate.currency)} × $tripNights nights"

            "PER_DAY" ->
                "${formatMoney(estimate.amount, estimate.currency)} × $tripDays days"

            else ->
                formatMoney(
                    estimate.amount,
                    estimate.currency
                )
        }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = estimate.category,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = basisText,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // -------------------------------------------------
            // TOTAL IS ALWAYS SHOWN IN HOME CURRENCY
            // -------------------------------------------------

            Text(
                text = "Estimated total: ${
                    formatMoney(
                        calculatedAmount,
                        homeCurrency
                    )
                }",
                style = MaterialTheme.typography.titleSmall
            )

            if (!estimate.notes.isNullOrBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = estimate.notes,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// =============================================================
// MONEY FORMATTING
// =============================================================

private fun formatMoney(
    amount: Double,
    currency: String
): String {

    return when (currency) {

        "NZD" -> {
            "NZ$ %,.2f".format(
                Locale.US,
                amount
            )
        }

        "AUD" -> {
            "A$ %,.2f".format(
                Locale.US,
                amount
            )
        }

        "USD" -> {
            "US$ %,.2f".format(
                Locale.US,
                amount
            )
        }

        "EUR" -> {
            "€ %,.2f".format(
                Locale.US,
                amount
            )
        }

        "GBP" -> {
            "£ %,.2f".format(
                Locale.US,
                amount
            )
        }

        "THB" -> {
            "฿ %,.2f".format(
                Locale.US,
                amount
            )
        }

        "VND" -> {
            "₫ %,.0f".format(
                Locale.US,
                amount
            )
        }

        "LAK" -> {
            "₭ %,.0f".format(
                Locale.US,
                amount
            )
        }

        "CNY" -> {
            "¥ %,.2f".format(
                Locale.US,
                amount
            )
        }

        else -> {
            "$currency %,.2f".format(
                Locale.US,
                amount
            )
        }
    }
}