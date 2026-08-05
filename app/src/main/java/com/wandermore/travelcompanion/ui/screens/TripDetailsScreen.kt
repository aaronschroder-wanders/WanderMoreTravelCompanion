package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.ui.components.ExpenseCard
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel


@Composable
fun TripDetailsScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onEditTrip: () -> Unit,
    onAddExpense: () -> Unit,
    onDeleteTrip: () -> Unit,
    onEditExpense: (Long) -> Unit
) {


    var trip by remember {
        mutableStateOf<Trip?>(null)
    }


    var showDeleteDialog by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(tripId) {

        trip = tripViewModel.getTripById(tripId)

    }


    val currentTrip = trip


    if (currentTrip == null) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Loading trip..."
            )

        }

        return
    }


    val expenses by tripViewModel
        .getExpensesForTrip(currentTrip.id)
        .collectAsState(
            initial = emptyList()
        )


    val total = expenses.sumOf {
        it.amount
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Text(
            text = "🌏 ${currentTrip.name}"
        )


        Text(
            text = "📅 Start: ${formatDate(currentTrip.startDate)}"
        )


        Text(
            text = "📅 End: ${formatDate(currentTrip.endDate)}"
        )


        Text(
            text = "💰 Home currency: ${currentTrip.homeCurrency}"
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        Text(
            text = "Expenses"
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        if (expenses.isEmpty()) {


            Text(
                text = "No expenses recorded yet."
            )


        } else {


            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {


                items(expenses) { expense ->


                    ExpenseCard(
                        expense = expense,
                        onClick = {
                            onEditExpense(expense.id.toLong())
                        }
                    )


                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )


                }


            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Text(
                text = "Total: $total ${currentTrip.homeCurrency}"
            )

        }


        Spacer(
            modifier = Modifier.height(16.dp)
        )


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


        Button(
            onClick = {

                showDeleteDialog = true

            }
        ) {

            Text(
                "Delete Trip"
            )

        }


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
                    "Are you sure you want to delete ${currentTrip.name}?"
                )

            },


            confirmButton = {


                Button(

                    onClick = {

                        onDeleteTrip()

                    }

                ) {

                    Text(
                        "Delete"
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