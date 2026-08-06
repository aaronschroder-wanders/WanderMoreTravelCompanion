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



    val expenses by tripViewModel
        .getExpensesForTrip(currentTrip.id)
        .collectAsState(
            initial = emptyList()
        )



    val total = expenses.sumOf {
        it.convertedAmount
    }



    val plannedDays =
        ChronoUnit.DAYS.between(
            currentTrip.startDate,
            currentTrip.endDate
        ) + 1



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
            text = "Status: ${currentTrip.status.name.lowercase()
                .replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        TripSummaryCard(

            plannedDays = plannedDays,

            expenseCount = expenses.size,

            totalSpent = total,

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



                    items(dailyExpenses) { expense ->


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


            Spacer(
                modifier = Modifier.height(8.dp)
            )

        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),

            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {


            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceEvenly

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


            }



            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceEvenly

            ) {


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



                        Button(
                            onClick = {

                                showDeleteDialog = true

                            }

                        ) {

                            Text(
                                "Delete"

                            )

                        }


                    }


                }


            }


        }


    }



    if (showRestoreDialog) {


        AlertDialog(

            onDismissRequest = {
                showRestoreDialog = false
            },


            title = {

                Text(
                    "Restore Trip?"
                )

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




    if (showDeleteDialog) {


        AlertDialog(

            onDismissRequest = {
                showDeleteDialog = false
            },


            title = {

                Text(
                    "Delete Trip?"
                )

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