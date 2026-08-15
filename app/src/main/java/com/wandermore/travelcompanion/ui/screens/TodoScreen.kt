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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.TodoEntity
import com.wandermore.travelcompanion.ui.components.TripSectionHeader
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel

@Composable
fun TodoScreen(
    tripId: Long,
    tripViewModel: TripViewModel,
    onAddTodo: () -> Unit,
    onEditTodo: (Long) -> Unit,
    onBack: () -> Unit
) {

    // ---------------------------------------------------------
    // LOAD TRIP
    // ---------------------------------------------------------

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(
            initial = null
        )

    val currentTrip = tripState ?: return

    // ---------------------------------------------------------
    // LOAD TO DO ITEMS
    // ---------------------------------------------------------

    val todos by tripViewModel
        .getTodosForTrip(currentTrip.id)
        .collectAsState(
            initial = emptyList()
        )

    // ---------------------------------------------------------
    // GROUP BY DUE DATE
    //
    // Items with a due date are grouped by date.
    // Items with no date are kept in a separate group.
    // ---------------------------------------------------------

    val groupedTodos =
        todos
            .groupBy { it.dueDate }
            .toList()
            .sortedWith(
                compareBy(
                    { it.first == null },
                    { it.first }
                )
            )

    // ---------------------------------------------------------
    // SCREEN
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // -----------------------------------------------------
        // SHARED TRIP SECTION HEADER
        // -----------------------------------------------------

        TripSectionHeader(
            title = "To Do",
            tripName = currentTrip.name,
            startDate = currentTrip.startDate,
            endDate = currentTrip.endDate,
            icon = "✓"
        )

        // -----------------------------------------------------
        // TO DO LIST
        // -----------------------------------------------------

        if (todos.isEmpty()) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "No To Do items yet."
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                groupedTodos.forEach {
                        (dueDate, todosForDate) ->

                    // -------------------------------------------------
                    // DATE HEADING
                    // -------------------------------------------------

                    item(
                        key = "date_$dueDate"
                    ) {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                if (dueDate == null) {
                                    "No Due Date"
                                } else {
                                    formatDate(dueDate)
                                },
                            style =
                                MaterialTheme.typography
                                    .titleMedium,
                            color =
                                MaterialTheme.colorScheme
                                    .primary
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )
                    }

                    // -------------------------------------------------
                    // ITEMS FOR THIS DATE
                    //
                    // Incomplete items first, completed items last.
                    // -------------------------------------------------

                    items(
                        items =
                            todosForDate.sortedBy {
                                it.completed
                            },
                        key = {
                            it.id
                        }
                    ) { todo ->

                        TodoRow(
                            todo = todo,
                            onClick = {
                                onEditTodo(
                                    todo.id
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }
        }

        // ---------------------------------------------------------
        // BOTTOM BUTTONS
        // ---------------------------------------------------------

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }

            Button(
                onClick = onAddTodo
            ) {
                Text("Add To Do")
            }
        }
    }
}

// =================================================================
// TO DO ROW
// =================================================================

@Composable
private fun TodoRow(
    todo: TodoEntity,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // -------------------------------------------------
            // TASK
            // -------------------------------------------------

            Text(
                text =
                    if (todo.completed) {
                        "✓ ${todo.task}"
                    } else {
                        "☐ ${todo.task}"
                    },
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // -------------------------------------------------
            // ASSIGNED TO
            // -------------------------------------------------

            Text(
                text =
                    "Assigned to: ${todo.assignedTo}",
                style =
                    MaterialTheme.typography
                        .bodySmall
            )

            // -------------------------------------------------
            // NOTES
            // -------------------------------------------------

            if (!todo.notes.isNullOrBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = todo.notes!!,
                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )
            }
        }
    }
}