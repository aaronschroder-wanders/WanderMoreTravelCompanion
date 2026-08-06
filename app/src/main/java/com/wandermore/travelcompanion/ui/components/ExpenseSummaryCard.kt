package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ExpenseSummaryCard(
    total: Double,
    currency: String,
    expenseCount: Int
) {


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

    ) {


        Column(

            modifier = Modifier
                .padding(16.dp)

        ) {


            Text(

                text = "Total Spent",

                style = MaterialTheme.typography.titleMedium

            )


            Text(

                text = "${String.format("%.2f", total)} $currency",

                style = MaterialTheme.typography.headlineSmall

            )


            Text(

                text = "$expenseCount expenses",

                style = MaterialTheme.typography.bodyMedium

            )


        }

    }


}