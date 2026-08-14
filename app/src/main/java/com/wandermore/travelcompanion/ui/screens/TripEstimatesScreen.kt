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
import com.wandermore.travelcompanion.util.formatMoney
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.temporal.ChronoUnit

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
    // CALCULATE TOTAL ESTIMATED COST
    //
    // convertedAmount is already in the trip home currency.
    // Apply the appropriate day/night multiplier here.
    // ---------------------------------------------------------

    val estimatedTripTotal =
        estimates.sumOf { estimate ->

            val multiplier =
                when (estimate.estimateType) {

                    "PER_NIGHT" -> tripNights

                    "PER_DAY" -> tripDays

                    else -> 1L
                }

            estimate.convertedAmount * multiplier
        }

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
            text =
                "${tripDays} days • ${tripNights} nights • " +
                        "Home currency: ${currentTrip.homeCurrency}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // TOTAL ESTIMATE SUMMARY
        // -----------------------------------------------------

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Estimated Trip Total",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = formatMoney(
                        estimatedTripTotal,
                        currentTrip.homeCurrency
                    ),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

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
                    text =
                        "Add estimates for accommodation, food, " +
                                "transport and other trip costs.",
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
                Text("Back")
            }

            Button(
                onClick = onAddEstimate
            ) {
                Text("Add Estimate")
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

    // ---------------------------------------------------------
    // CALCULATE MULTIPLIER
    // ---------------------------------------------------------

    val multiplier =
        when (estimate.estimateType) {

            "PER_NIGHT" -> tripNights

            "PER_DAY" -> tripDays

            else -> 1L
        }

    // ---------------------------------------------------------
    // CALCULATED TOTAL
    //
    // convertedAmount is already in the home currency.
    // ---------------------------------------------------------

    val calculatedAmount =
        estimate.convertedAmount * multiplier

    // ---------------------------------------------------------
    // SHOW THE ORIGINAL CURRENCY FOR THE BASIS
    // ---------------------------------------------------------

    val basisText =
        when (estimate.estimateType) {

            "PER_NIGHT" ->
                "${formatMoney(
                    estimate.amount,
                    estimate.currency
                )} × $tripNights nights"

            "PER_DAY" ->
                "${formatMoney(
                    estimate.amount,
                    estimate.currency
                )} × $tripDays days"

            else ->
                formatMoney(
                    estimate.amount,
                    estimate.currency
                )
        }

    // ---------------------------------------------------------
    // SIMPLE HOME-CURRENCY ONE-OFF
    //
    // In this case the basis amount IS already the final total,
    // so displaying "Estimated total" underneath would duplicate
    // the same information.
    // ---------------------------------------------------------

    val isSimpleHomeCurrencyOneOff =
        estimate.estimateType == "ONE_OFF" &&
                estimate.currency == homeCurrency

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // -------------------------------------------------
            // CATEGORY
            // -------------------------------------------------

            Text(
                text = estimate.category,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // -------------------------------------------------
            // BASIS AMOUNT
            // -------------------------------------------------

            Text(
                text = basisText,
                style = MaterialTheme.typography.bodyMedium
            )

            // -------------------------------------------------
            // CALCULATED TOTAL
            //
            // Only show this when it adds useful information.
            // -------------------------------------------------

            if (!isSimpleHomeCurrencyOneOff) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text =
                        "Estimated total: ${
                            formatMoney(
                                calculatedAmount,
                                homeCurrency
                            )
                        }",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            // -------------------------------------------------
            // NOTES
            // -------------------------------------------------

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