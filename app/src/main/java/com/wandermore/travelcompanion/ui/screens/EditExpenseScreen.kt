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
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.viewmodel.TripViewModel


@Composable
fun EditExpenseScreen(
    expense: ExpenseEntity,
    tripViewModel: TripViewModel,
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


    var showDeleteDialog by remember {
        mutableStateOf(false)
    }



    Column(

        modifier = Modifier.fillMaxSize(),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {


        Text(
            text = "Edit Expense"
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


                val updatedExpense = expense.copy(

                    description = description,

                    amount = amount.toDoubleOrNull()
                        ?: 0.0,

                    category = category

                )


                tripViewModel.updateExpense(
                    updatedExpense
                )


                onExpenseUpdated()


            }

        ) {

            Text(
                text = "Save Changes"
            )

        }



        Button(

            onClick = {

                showDeleteDialog = true

            }

        ) {

            Text(
                text = "Delete Expense"
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