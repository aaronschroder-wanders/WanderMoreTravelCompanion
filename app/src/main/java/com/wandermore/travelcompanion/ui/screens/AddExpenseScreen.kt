package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.LocalDate


@Composable
fun AddExpenseScreen(
    trip: Trip,
    tripViewModel: TripViewModel,
    onExpenseAdded: () -> Unit
) {

    var description by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("")
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Add Expense"
        )


        Text(
            text = "Trip: ${trip.name}"
        )


        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text("Description")
            }
        )


        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
            },
            label = {
                Text("Amount")
            }
        )


        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
            },
            label = {
                Text("Category")
            }
        )


        Button(
            onClick = {

                val expense = ExpenseEntity(

                    tripId = trip.id,

                    description = description,

                    amount = amount.toDoubleOrNull()
                        ?: 0.0,

                    currency = trip.homeCurrency,

                    date = LocalDate.now(),

                    category = category

                )


                tripViewModel.addExpense(
                    expense
                )


                onExpenseAdded()

            }
        ) {

            Text(
                text = "Save Expense"
            )

        }

    }

}