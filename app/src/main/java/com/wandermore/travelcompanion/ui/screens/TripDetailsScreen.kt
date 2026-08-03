package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.util.formatDate


@Composable
fun TripDetailsScreen(
    trip: Trip,
    onEditTrip: () -> Unit,
    onAddExpense: () -> Unit,
    onDeleteTrip: () -> Unit
) {

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(
            text = "🌏 ${trip.name}"
        )


        Text(
            text = "📅 Start: ${formatDate(trip.startDate)}"
        )


        Text(
            text = "📅 End: ${formatDate(trip.endDate)}"
        )


        Text(
            text = "💰 Home currency: ${trip.homeCurrency}"
        )


        Button(
            onClick = onEditTrip
        ) {

            Text(
                text = "Edit Trip"
            )

        }
        Button(
            onClick = onAddExpense
        ) {

            Text(
                text = "Add Expense"
            )

        }


        Button(
            onClick = {
                showDeleteDialog = true
            }
        ) {

            Text(
                text = "Delete Trip"
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
                    text = "Delete Trip?"
                )

            },


            text = {

                Text(
                    text = "Are you sure you want to delete ${trip.name}?"
                )

            },


            confirmButton = {

                Button(
                    onClick = {

                        onDeleteTrip()

                    }
                ) {

                    Text(
                        text = "Delete"
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
                        text = "Cancel"

                    )

                }

            }

        )

    }

}