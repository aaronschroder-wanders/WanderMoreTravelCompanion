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
import com.wandermore.travelcompanion.ui.components.CategoryDropdown
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.ui.components.DatePickerButton
import com.wandermore.travelcompanion.util.ExchangeRates
import com.wandermore.travelcompanion.util.ExpenseCategories
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
        mutableStateOf(
            ExpenseCategories.categories.first()
        )
    }


    var numberOfNights by remember {
        mutableStateOf("")
    }


    var currency by remember {
        mutableStateOf(
            trip.homeCurrency
        )
    }


    // Expense date defaults to today but can be changed
    var expenseDate by remember {
        mutableStateOf(
            LocalDate.now()
        )
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



        CurrencyDropdown(

            selectedCurrency = currency,

            onCurrencySelected = {
                currency = it
            },

            label = "Expense Currency"

        )



        val previewAmount =
            amount.toDoubleOrNull()


        if (previewAmount != null) {

            val previewRate =
                ExchangeRates.getRate(currency)


            val previewNZD =
                previewAmount * previewRate


            Text(
                text = "≈ NZ$ %.2f".format(previewNZD)
            )
        }



        CategoryDropdown(

            selectedCategory = category,

            onCategorySelected = {

                category = it

            }

        )



        if (category == "Accommodation") {

            OutlinedTextField(

                value = numberOfNights,

                onValueChange = {
                    numberOfNights = it
                },

                label = {
                    Text("Number of nights")
                }

            )
        }



        DatePickerButton(

            selectedDate = expenseDate,

            onDateSelected = {

                expenseDate = it

            }

        )



        Button(

            onClick = {


                val enteredAmount =
                    amount.toDoubleOrNull()
                        ?: 0.0



                val exchangeRate =
                    ExchangeRates.getRate(currency)



                val convertedAmount =
                    enteredAmount * exchangeRate



                val expense = ExpenseEntity(

                    tripId = trip.id,

                    description = description,

                    amount = enteredAmount,

                    currency = currency,

                    category = category,

                    date = expenseDate,

                    exchangeRate = exchangeRate,

                    convertedAmount = convertedAmount,

                    numberOfNights = if (category == "Accommodation") {
                        numberOfNights.toIntOrNull()
                    } else {
                        null
                    }

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