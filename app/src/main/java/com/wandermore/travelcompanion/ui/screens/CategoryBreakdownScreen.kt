package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.util.ExpenseCategoryIcons
import com.wandermore.travelcompanion.util.formatMoney
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun CategoryBreakdownScreen(
    tripId: Long,
    currency: String,
    tripViewModel: TripViewModel,
    onCategorySelected: (String) -> Unit,
    onBack: () -> Unit
) {

// ---------------------------------------------------------
// LOAD EXPENSES
// ---------------------------------------------------------

    val expenses by tripViewModel
        .getExpensesForTrip(tripId)
        .collectAsState(
            initial = emptyList()
        )

// ---------------------------------------------------------
// LOAD ESTIMATES
// ---------------------------------------------------------

    val estimates by tripViewModel
        .getTripEstimatesForTrip(tripId)
        .collectAsState(
            initial = emptyList()
        )

// ---------------------------------------------------------
// LOAD TRIP
// ---------------------------------------------------------

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(
            initial = null
        )

    val trip = tripState

// ---------------------------------------------------------
// ACTUAL TOTAL
// ---------------------------------------------------------

    val totalSpent =
        expenses.sumOf {
            it.convertedAmount
        }

// ---------------------------------------------------------
// TRIP DAYS / NIGHTS
// ---------------------------------------------------------

    val tripDays =
        if (trip != null) {
            ChronoUnit.DAYS.between(
                trip.startDate,
                trip.endDate
            ) + 1
        } else {
            0L
        }

    val tripNights =
        if (trip != null) {
            ChronoUnit.DAYS.between(
                trip.startDate,
                trip.endDate
            )
        } else {
            0L
        }

// ---------------------------------------------------------
// ESTIMATED TOTAL
// ---------------------------------------------------------

    val estimatedTotal =
        estimates.sumOf { estimate ->

            val multiplier =
                when (estimate.estimateType) {

                    "PER_NIGHT" -> tripNights

                    "PER_DAY" -> tripDays

                    else -> 1L
                }

            estimate.convertedAmount * multiplier
        }

    val overallDifference =
        estimatedTotal - totalSpent

// ---------------------------------------------------------
// CATEGORY LIST
//
// Include categories appearing in either estimates
// or actual expenses.
//
// Sort by ACTUAL spending, highest first.
// ---------------------------------------------------------

    val categories =
        (
                estimates.map {
                    it.category
                } +
                        expenses.map {
                            it.category
                        }
                )
            .distinct()
            .sortedByDescending { categoryName ->

                expenses
                    .filter {
                        it.category == categoryName
                    }
                    .sumOf {
                        it.convertedAmount
                    }
            }

// ---------------------------------------------------------
// SCREEN
// ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "📊 Spending Breakdown",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // -----------------------------------------------------
        // OVERALL ESTIMATE VS ACTUAL
        // -----------------------------------------------------

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = "Total Trip Expenses",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Estimate vs Actual ($currency)",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    SummaryValue(
                        label = "Estimated",
                        value = formatCompactMoney(
                            estimatedTotal
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryValue(
                        label = "Actual",
                        value = formatCompactMoney(
                            totalSpent
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryValue(
                        label =
                            if (overallDifference >= 0) {
                                "Under"
                            } else {
                                "Over"
                            },
                        value = formatCompactMoney(
                            kotlin.math.abs(
                                overallDifference
                            )
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // -----------------------------------------------------
        // CATEGORY LIST
        // -----------------------------------------------------

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            items(
                categories
            ) { categoryName ->

                val categoryExpenses =
                    expenses.filter {
                        it.category == categoryName
                    }

                val categoryEstimates =
                    estimates.filter {
                        it.category == categoryName
                    }

                val categoryAmount =
                    categoryExpenses.sumOf {
                        it.convertedAmount
                    }

                val categoryEstimated =
                    categoryEstimates.sumOf { estimate ->

                        val multiplier =
                            when (estimate.estimateType) {

                                "PER_NIGHT" -> tripNights

                                "PER_DAY" -> tripDays

                                else -> 1L
                            }

                        estimate.convertedAmount * multiplier
                    }

                val categoryDifference =
                    categoryEstimated - categoryAmount

                val percentage =
                    if (totalSpent > 0) {
                        categoryAmount / totalSpent
                    } else {
                        0.0
                    }

                // -------------------------------------------------
                // ACCOMMODATION DETAILS
                // -------------------------------------------------

                val accommodationNights =
                    if (
                        categoryName ==
                        "Accommodation"
                    ) {

                        categoryExpenses.sumOf {
                            it.numberOfNights ?: 0
                        }

                    } else {
                        0
                    }

                val averageNightlyRate =
                    if (
                        accommodationNights > 0
                    ) {

                        categoryAmount /
                                accommodationNights

                    } else {
                        null
                    }

                // -------------------------------------------------
                // FOOD DETAILS
                // -------------------------------------------------

                val foodDays =
                    if (
                        categoryName ==
                        "Food & Drink" &&
                        trip != null
                    ) {

                        when (trip.status) {

                            TripStatus.ARCHIVED -> {

                                ChronoUnit.DAYS.between(
                                    trip.startDate,
                                    trip.endDate
                                ) + 1
                            }

                            TripStatus.CURRENT -> {

                                val today =
                                    LocalDate.now()

                                if (
                                    today >=
                                    trip.startDate
                                ) {

                                    val endDate =
                                        if (
                                            today <
                                            trip.endDate
                                        ) {
                                            today
                                        } else {
                                            trip.endDate
                                        }

                                    ChronoUnit.DAYS.between(
                                        trip.startDate,
                                        endDate
                                    ) + 1

                                } else {
                                    0L
                                }
                            }

                            TripStatus.PLANNED -> {
                                0L
                            }
                        }

                    } else {
                        0L
                    }

                val foodPerDay =
                    if (
                        categoryName ==
                        "Food & Drink" &&
                        foodDays > 0
                    ) {

                        categoryAmount /
                                foodDays

                    } else {
                        null
                    }

                // -------------------------------------------------
                // CATEGORY CARD
                // -------------------------------------------------

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCategorySelected(
                                categoryName
                            )
                        }
                ) {

                    Column(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        )
                    ) {

                        // -----------------------------------------
                        // CATEGORY NAME
                        // -----------------------------------------

                        Text(
                            text =
                                "${ExpenseCategoryIcons.getIcon(
                                    categoryName
                                )} $categoryName",
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        // -----------------------------------------
                        // EST / ACTUAL / DIFFERENCE
                        // -----------------------------------------

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            CategoryValue(
                                label = "Est.",
                                value =
                                    if (
                                        categoryEstimates.isNotEmpty()
                                    ) {
                                        formatCompactMoney(
                                            categoryEstimated
                                        )
                                    } else {
                                        "—"
                                    },
                                modifier =
                                    Modifier.weight(1f)
                            )

                            CategoryValue(
                                label = "Actual",
                                value =
                                    formatCompactMoney(
                                        categoryAmount
                                    ),
                                modifier =
                                    Modifier.weight(1f)
                            )

                            CategoryValue(
                                label =
                                    if (
                                        categoryEstimates.isNotEmpty()
                                    ) {
                                        if (
                                            categoryDifference >= 0
                                        ) {
                                            "Under"
                                        } else {
                                            "Over"
                                        }
                                    } else {
                                        "Difference"
                                    },
                                value =
                                    if (
                                        categoryEstimates.isNotEmpty()
                                    ) {
                                        formatCompactMoney(
                                            kotlin.math.abs(
                                                categoryDifference
                                            )
                                        )
                                    } else {
                                        "—"
                                    },
                                modifier =
                                    Modifier.weight(1f)
                            )
                        }

                        // -----------------------------------------
                        // ACCOMMODATION
                        // -----------------------------------------

                        if (
                            averageNightlyRate != null
                        ) {

                            Text(
                                text =
                                    "$accommodationNights nights • Average: ${
                                        formatMoney(
                                            averageNightlyRate,
                                            currency
                                        )
                                    }/night",
                                style =
                                    MaterialTheme.typography.bodySmall,
                                modifier =
                                    Modifier.padding(
                                        top = 4.dp
                                    )
                            )
                        }

                        // -----------------------------------------
                        // FOOD
                        // -----------------------------------------

                        if (
                            foodPerDay != null
                        ) {

                            Text(
                                text =
                                    "Average: ${
                                        formatMoney(
                                            foodPerDay,
                                            currency
                                        )
                                    }/day",
                                style =
                                    MaterialTheme.typography.bodySmall,
                                modifier =
                                    Modifier.padding(
                                        top = 4.dp
                                    )
                            )
                        }

                        // -----------------------------------------
                        // SPENDING PERCENTAGE
                        // -----------------------------------------

                        LinearProgressIndicator(
                            progress = {
                                percentage.toFloat()
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 6.dp
                                    )
                        )

                        Text(
                            text =
                                "%.1f%% of trip spending"
                                    .format(
                                        percentage * 100
                                    ),
                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // -----------------------------------------------------
        // BACK BUTTON
        // -----------------------------------------------------

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Button(
            onClick = {
                onBack()
            }
        ) {

            Text(
                text = "Back to Trip"
            )
        }
    }

}

// =============================================================
// OVERALL SUMMARY VALUE
// =============================================================

@Composable
private fun SummaryValue(
    label: String,
    value: String,
    modifier: Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }

}

// =============================================================
// CATEGORY VALUE
// =============================================================

@Composable
private fun CategoryValue(
    label: String,
    value: String,
    modifier: Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }

}

// =============================================================
// COMPACT MONEY FORMAT
// =============================================================

private fun formatCompactMoney(
    amount: Double
): String {

    return "%,.0f".format(
        Locale.US,
        amount
    )

}
