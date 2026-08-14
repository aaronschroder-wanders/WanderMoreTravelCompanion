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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.ui.components.ExpenseCard
import com.wandermore.travelcompanion.ui.components.TripSectionHeader
import com.wandermore.travelcompanion.ui.components.TripSummaryCard
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun TripExpensesScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onAddExpense: () -> Unit,
    onEditExpense: (Long) -> Unit,
    onCategoryBreakdown: () -> Unit,
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
    // LOAD EXPENSES
    // ---------------------------------------------------------

    val expenses by tripViewModel
        .getExpensesForTrip(currentTrip.id)
        .collectAsState(
            initial = emptyList()
        )

    // ---------------------------------------------------------
    // LOAD ESTIMATES
    // ---------------------------------------------------------

    val estimates by tripViewModel
        .getTripEstimatesForTrip(currentTrip.id)
        .collectAsState(
            initial = emptyList()
        )

    // ---------------------------------------------------------
    // TOTALS
    // ---------------------------------------------------------

    val total = expenses.sumOf {
        it.convertedAmount
    }

    val airfareTotal = expenses
        .filter {
            it.category == "Airfares"
        }
        .sumOf {
            it.convertedAmount
        }

    // ---------------------------------------------------------
    // TRIP DAYS / NIGHTS
    // ---------------------------------------------------------

    val plannedDays =
        ChronoUnit.DAYS.between(
            currentTrip.startDate,
            currentTrip.endDate
        ) + 1

    val plannedNights =
        ChronoUnit.DAYS.between(
            currentTrip.startDate,
            currentTrip.endDate
        )

    // ---------------------------------------------------------
    // TOTAL ESTIMATE
    //
    // PER_NIGHT = estimate × planned nights
    // PER_DAY   = estimate × planned days
    // OTHER     = estimate × 1
    //
    // convertedAmount is already in home currency.
    // ---------------------------------------------------------

    val totalEstimate =
        estimates.sumOf { estimate ->

            val multiplier =
                when (estimate.estimateType) {

                    "PER_NIGHT" -> plannedNights

                    "PER_DAY" -> plannedDays

                    else -> 1L
                }

            estimate.convertedAmount * multiplier
        }

    // ---------------------------------------------------------
    // DAYS USED FOR AVERAGES
    //
    // CURRENT:
    // Count from trip start through today, inclusive.
    // If today is after the trip end date, cap at end date.
    //
    // PLANNED / ARCHIVED:
    // Use the full planned trip duration.
    // ---------------------------------------------------------

    val daysForAverages =
        when (currentTrip.status) {

            TripStatus.CURRENT -> {

                val today = LocalDate.now()

                if (today >= currentTrip.startDate) {

                    val endDate =
                        if (today < currentTrip.endDate) {
                            today
                        } else {
                            currentTrip.endDate
                        }

                    ChronoUnit.DAYS.between(
                        currentTrip.startDate,
                        endDate
                    ) + 1

                } else {
                    0L
                }
            }

            TripStatus.PLANNED,
            TripStatus.ARCHIVED -> plannedDays
        }

    // ---------------------------------------------------------
    // FOOD & DRINK DAILY AVERAGE
    // ---------------------------------------------------------

    val foodDrinkTotal = expenses
        .filter {
            it.category == "Food & Drink"
        }
        .sumOf {
            it.convertedAmount
        }

    val foodDrinkPerDay =
        if (daysForAverages > 0) {
            foodDrinkTotal / daysForAverages
        } else {
            0.0
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

        TripSectionHeader(
            title = "Expenses",
            tripName = currentTrip.name,
            startDate = currentTrip.startDate,
            endDate = currentTrip.endDate,
            icon = "💰"
        )

        // -----------------------------------------------------
        // EXPENSE SUMMARY
        // -----------------------------------------------------

        TripSummaryCard(
            plannedDays = daysForAverages,
            totalEstimate = totalEstimate,
            totalSpent = total,
            airfareTotal = airfareTotal,
            foodDrinkPerDay = foodDrinkPerDay,
            currency = currentTrip.homeCurrency,
            onClick = onCategoryBreakdown
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        // -----------------------------------------------------
        // EXPENSE LIST
        // -----------------------------------------------------

        Text(
            text = "Expense List",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        if (expenses.isEmpty()) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "No expenses recorded yet."
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                val groupedExpenses =
                    expenses
                        .groupBy {
                            it.date
                        }
                        .toSortedMap(
                            compareByDescending { it }
                        )

                groupedExpenses.forEach { (date, dailyExpenses) ->

                    item {

                        Text(
                            text = formatDate(date),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(
                                top = 4.dp,
                                bottom = 2.dp
                            )
                        )
                    }

                    items(
                        dailyExpenses.sortedByDescending {
                            it.id
                        }
                    ) { expense ->

                        ExpenseCard(
                            expense = expense,
                            homeCurrency = currentTrip.homeCurrency,
                            onClick = {
                                onEditExpense(
                                    expense.id
                                )
                            }
                        )
                    }
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
                onClick = onAddExpense
            ) {
                Text("Add Expense")
            }
        }
    }
}