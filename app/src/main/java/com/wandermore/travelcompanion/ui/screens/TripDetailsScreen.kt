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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.ui.components.ExpenseCard
import com.wandermore.travelcompanion.ui.components.TripSummaryCard
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun TripDetailsScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onEditTrip: () -> Unit,
    onAddExpense: () -> Unit,
    onDeleteTrip: () -> Unit,
    onStartTrip: () -> Unit,
    onArchiveTrip: () -> Unit,
    onRestoreTrip: (TripStatus) -> Unit,
    onEditExpense: (Long) -> Unit,
    onCategoryBreakdown: () -> Unit
) {

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showRestoreDialog by remember {
        mutableStateOf(false)
    }

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
    // ACTUAL EXPENSE TOTALS
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
    // Uses the same calculation as the Trip Estimates screen:
    // PER_NIGHT = estimate × nights
    // PER_DAY   = estimate × days
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
    // SCREEN
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "🌏",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Text(
            text = currentTrip.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "📅 ${formatDate(currentTrip.startDate)} - ${formatDate(currentTrip.endDate)}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Text(
            text = "💰 Home currency: ${currentTrip.homeCurrency}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Text(
            text = "Status: ${
                currentTrip.status.name
                    .lowercase()
                    .replaceFirstChar { it.uppercase() }
            }",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        if (currentTrip.status == TripStatus.ARCHIVED) {

            TextButton(
                onClick = {
                    showDeleteDialog = true
                },
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            ) {

                Text(
                    text = "Delete Trip"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // -----------------------------------------------------
        // TRIP SUMMARY
        // -----------------------------------------------------

        TripSummaryCard(
            plannedDays = daysForAverages,
            totalEstimate = totalEstimate,
            totalSpent = total,
            airfareTotal = airfareTotal,
            currency = currentTrip.homeCurrency,
            onClick = {
                onCategoryBreakdown()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Expenses",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        if (expenses.isEmpty()) {

            Text(
                text = "No expenses recorded yet."
            )

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f)
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
                                top = 6.dp,
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
                            onClick = {
                                onEditExpense(expense.id)
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // -----------------------------------------------------
        // BOTTOM BUTTONS
        // -----------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = onEditTrip
            ) {
                Text(
                    "Edit Trip"
                )
            }

            Button(
                onClick = onAddExpense
            ) {
                Text(
                    "Add Expense"
                )
            }

            when (currentTrip.status) {

                TripStatus.PLANNED -> {

                    Button(
                        onClick = onStartTrip
                    ) {
                        Text(
                            "Start Trip"
                        )
                    }
                }

                TripStatus.CURRENT -> {

                    Button(
                        onClick = onArchiveTrip
                    ) {
                        Text(
                            "Archive Trip"
                        )
                    }
                }

                TripStatus.ARCHIVED -> {

                    Button(
                        onClick = {
                            showRestoreDialog = true
                        }
                    ) {
                        Text(
                            "Restore"
                        )
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------
    // RESTORE DIALOG
    // ---------------------------------------------------------

    if (showRestoreDialog) {

        AlertDialog(
            onDismissRequest = {
                showRestoreDialog = false
            },
            title = {
                Text("Restore Trip?")
            },
            text = {
                Text(
                    "Where should ${currentTrip.name} be moved?"
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        onRestoreTrip(
                            TripStatus.PLANNED
                        )

                        showRestoreDialog = false
                    }
                ) {
                    Text(
                        "Restore as Planned"
                    )
                }
            },
            dismissButton = {

                Button(
                    onClick = {

                        onRestoreTrip(
                            TripStatus.CURRENT
                        )

                        showRestoreDialog = false
                    }
                ) {
                    Text(
                        "Restore as Current"
                    )
                }
            }
        )
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
                Text("Delete Trip?")
            },
            text = {
                Text(
                    "Are you sure you want to permanently delete ${currentTrip.name}?"
                )
            },
            confirmButton = {

                Button(
                    onClick = {
                        onDeleteTrip()
                    }
                ) {
                    Text(
                        "Delete Permanently"
                    )
                }
            },
            dismissButton = {

                Button(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Cancel"
                    )
                }
            }
        )
    }
}