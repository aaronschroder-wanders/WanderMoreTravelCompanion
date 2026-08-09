package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TripSectionHeader(
    title: String,
    tripName: String,
    startDate: LocalDate,
    endDate: LocalDate,
    icon: String? = null
) {

    val dateFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {

        // -----------------------------------------------------
        // SECTION TITLE
        // -----------------------------------------------------

        Text(
            text = if (!icon.isNullOrBlank()) {
                "$icon  $title"
            } else {
                title
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        // -----------------------------------------------------
        // TRIP NAME
        // -----------------------------------------------------

        Text(
            text = tripName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        // -----------------------------------------------------
        // TRIP DATES
        // -----------------------------------------------------

        Text(
            text = buildString {

                append(
                    startDate.format(dateFormatter)
                )

                append(" – ")

                append(
                    endDate.format(dateFormatter)
                )
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}