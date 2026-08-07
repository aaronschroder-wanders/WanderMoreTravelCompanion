package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.ui.components.CategoryDropdown
import com.wandermore.travelcompanion.ui.components.CurrencyDropdown
import com.wandermore.travelcompanion.ui.components.DatePickerButton
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import androidx.compose.material3.MaterialTheme

@Composable
fun EditExpenseScreen(
    expense: ExpenseEntity,
    tripViewModel: TripViewModel,
    exchangeRateViewModel: ExchangeRateViewModel,
    onExpenseUpdated: () -> Unit,
    onDeleteExpense: () -> Unit
) {

    var description by remember {
        mutableStateOf(expense.description)
    }

    var amount by remember {
        mutableStateOf(expense.amount.toString())
    }

    var category by remember {
        mutableStateOf(expense.category)
    }

    var currency by remember {
        mutableStateOf(expense.currency)
    }

    var numberOfNights by remember {
        mutableStateOf(
            expense.numberOfNights?.toString() ?: ""
        )
    }

    var expenseDate by remember {
        mutableStateOf(expense.date)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showRecalculatedMessage by remember {
        mutableStateOf(false)
    }

    var displayedExchangeRate by remember {
        mutableStateOf(expense.exchangeRate)
    }

    var displayedConvertedAmount by remember {
        mutableStateOf(expense.convertedAmount)
    }


    val scrollState = rememberScrollState()


    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Top

    ) {


        Text(
            text = "Edit Expense",
            style = MaterialTheme.typography.titleLarge
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


            val amountChanged =
                previewAmount != expense.amount


            val currencyChanged =
                currency != expense.currency



            val previewNZD =

                if (
                    amountChanged ||
                    currencyChanged
                ) {

                    val currentRate =
                        exchangeRateViewModel.getRate(currency)

                    previewAmount * currentRate

                } else {

                    displayedConvertedAmount

                }



            Text(

                text = "≈ NZ$ %.2f".format(previewNZD),

                modifier = Modifier.padding(
                    top = 8.dp
                )

            )


            val currentRate =
                exchangeRateViewModel.getRate(expense.currency)



            if (
                currency == expense.currency &&
                currentRate != displayedExchangeRate
            ) {


                Text(

                    text =
                        "Stored rate: %.4f | Current rate: %.4f"
                            .format(
                                displayedExchangeRate,
                                currentRate
                            ),

                    style = MaterialTheme.typography.bodySmall,

                    modifier = Modifier.padding(
                        top = 8.dp
                    )

                )


                Button(

                    onClick = {


                        val newConvertedAmount =
                            expense.amount * currentRate


                        val updatedExpense =
                            expense.copy(

                                exchangeRate = currentRate,

                                convertedAmount = newConvertedAmount

                            )


                        tripViewModel.updateExpense(
                            updatedExpense
                        )


                        displayedExchangeRate = currentRate

                        displayedConvertedAmount =
                            newConvertedAmount


                        showRecalculatedMessage = true


                    },

                    modifier = Modifier.padding(
                        top = 8.dp
                    )

                ) {

                    Text(
                        "Use Current Exchange Rate"
                    )

                }


            }



            if (showRecalculatedMessage) {

                Text(

                    text = "✓ Exchange rate updated",

                    modifier = Modifier.padding(
                        top = 8.dp
                    )

                )

            }


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


                val amountChanged =
                    enteredAmount != expense.amount


                val currencyChanged =
                    currency != expense.currency



                val finalExchangeRate =

                    if (
                        amountChanged ||
                        currencyChanged
                    ) {

                        exchangeRateViewModel
                            .getRate(currency)

                    } else {

                        displayedExchangeRate

                    }



                val finalConvertedAmount =

                    if (
                        amountChanged ||
                        currencyChanged
                    ) {

                        enteredAmount * finalExchangeRate

                    } else {

                        displayedConvertedAmount

                    }



                val updatedExpense =
                    expense.copy(

                        description = description,

                        amount = enteredAmount,

                        currency = currency,

                        category = category,

                        date = expenseDate,

                        exchangeRate = finalExchangeRate,

                        convertedAmount = finalConvertedAmount,

                        numberOfNights =
                            if (
                                category == "Accommodation"
                            ) {

                                numberOfNights.toIntOrNull()

                            } else {

                                null

                            }

                    )


                tripViewModel.updateExpense(
                    updatedExpense
                )


                onExpenseUpdated()

            }

        ) {

            Text(
                "Save Changes"
            )

        }



        Button(

            onClick = {

                showDeleteDialog = true

            }

        ) {

            Text(
                "Delete Expense"
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
                    "Delete Expense?"
                )

            },

            text = {

                Text(
                    "Are you sure you want to delete this expense?"
                )

            },

            confirmButton = {


                Button(

                    onClick = {

                        onDeleteExpense()

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