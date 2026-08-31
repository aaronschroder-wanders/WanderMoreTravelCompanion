package com.wandermore.travelcompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.database.TodoEntity
import com.wandermore.travelcompanion.util.formatDate
import java.time.LocalDate

@Composable
fun TodayTomorrowCard(
    title: String,
    date: LocalDate,
    itineraryItems: List<ItineraryEntity>,
    todos: List<TodoEntity>,
    onItineraryClick: (Long) -> Unit,
    onTodoClick: (Long) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // =================================================
            // HEADER
            // =================================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "🗓️",

                    style =
                        MaterialTheme.typography.titleLarge,

                    modifier =
                        Modifier.size(32.dp)
                )

                Spacer(
                    modifier =
                        Modifier.size(8.dp)
                )

                Text(
                    text = title,

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = formatDate(date),

                style =
                    MaterialTheme.typography.bodyMedium,

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            // =================================================
            // NOTHING TO DISPLAY
            // =================================================

            if (
                itineraryItems.isEmpty() &&
                todos.isEmpty()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = "Nothing scheduled",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                return@Column
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // =================================================
            // ITINERARY
            // =================================================

            itineraryItems.forEach { item ->

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItineraryClick(
                                    item.id
                                )
                            }
                            .padding(
                                vertical = 8.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    // -----------------------------------------
                    // ITINERARY TYPE ICON
                    // -----------------------------------------

                    Text(
                        text =
                            todayItinerarySymbol(
                                item.type
                            ),

                        style =
                            MaterialTheme.typography
                                .titleMedium,

                        modifier =
                            Modifier.size(32.dp)
                    )

                    // -----------------------------------------
                    // TIME
                    // -----------------------------------------

                    Text(
                        text =
                            item.time?.toString()
                                ?: "—",

                        style =
                            MaterialTheme.typography.bodyMedium,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    // -----------------------------------------
                    // DETAILS
                    // -----------------------------------------

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                item.title,

                            style =
                                MaterialTheme.typography
                                    .bodyLarge,

                            fontWeight =
                                FontWeight.Medium
                        )

                        if (
                            !item.location
                                .isNullOrBlank()
                        ) {

                            Text(
                                text =
                                    item.location!!,

                                style =
                                    MaterialTheme.typography
                                        .bodySmall,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }

                    // -----------------------------------------
                    // CHEVRON
                    // -----------------------------------------

                    Text(
                        text = "›",

                        style =
                            MaterialTheme.typography
                                .titleLarge,

                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }
            }

            // =================================================
            // TO DOS
            // =================================================

            if (
                itineraryItems.isNotEmpty() &&
                todos.isNotEmpty()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )
            }

            todos.forEach { todo ->

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onTodoClick(
                                    todo.id
                                )
                            }
                            .padding(
                                vertical = 8.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    // -----------------------------------------
                    // TO DO ICON
                    // -----------------------------------------

                    Text(
                        text = "✅",

                        style =
                            MaterialTheme.typography
                                .titleMedium,

                        modifier =
                            Modifier.size(32.dp)
                    )

                    // -----------------------------------------
                    // DETAILS
                    // -----------------------------------------

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                todo.task,

                            style =
                                MaterialTheme.typography
                                    .bodyLarge,

                            fontWeight =
                                FontWeight.Medium
                        )

                        if (
                            todo.assignedTo
                                .isNotBlank()
                        ) {

                            Text(
                                text =
                                    "Assigned to " +
                                            todo.assignedTo,

                                style =
                                    MaterialTheme.typography
                                        .bodySmall,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }

                    // -----------------------------------------
                    // CHEVRON
                    // -----------------------------------------

                    Text(
                        text = "›",

                        style =
                            MaterialTheme.typography
                                .titleLarge,

                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }
    }
}


// =============================================================
// TODAY/TOMORROW ITINERARY SYMBOL
// =============================================================

private fun todayItinerarySymbol(
    type: String
): String {

    return when (
        type.trim().lowercase()
    ) {

        "travel" ->
            "🚆"

        "accommodation" ->
            "🏨"

        "activity" ->
            "🎯"

        "attraction" ->
            "📸"

        "arrival" ->
            "🛬"

        "departure" ->
            "🛫"

        "other" ->
            "📌"

        else ->
            "📅"
    }
}