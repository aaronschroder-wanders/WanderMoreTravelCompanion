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

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(
            initial = null
        )

    val currentTrip = tripState ?: return

    val expenses by tripViewModel
        .getExpensesForTrip(currentTrip.id)
        .collectAsState(
            initial = emptyList()
        )

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

    val plannedDays =
        ChronoUnit.DAYS.between(
            currentTrip.startDate,
            currentTrip.endDate
        ) + 1

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Expenses",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = currentTrip.name,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        TripSummaryCard(
            plannedDays = daysForAverages,
            expenseCount = expenses.size,
            totalSpent = total,
            airfareTotal = airfareTotal,
            currency = currentTrip.homeCurrency,
            onClick = onCategoryBreakdown
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

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
                                onEditExpense(
                                    expense.id
                                )
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