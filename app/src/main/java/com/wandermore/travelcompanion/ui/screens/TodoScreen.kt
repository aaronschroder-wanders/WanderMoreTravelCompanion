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

    val tripState by tripViewModel
        .getTripByIdFlow(tripId)
        .collectAsState(
            initial = null
        )

    val currentTrip = tripState ?: return

    val todos by tripViewModel
        .getTodosForTrip(currentTrip.id)
        .collectAsState(
            initial = emptyList()
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "To Do",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = currentTrip.name,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

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
                modifier = Modifier.weight(1f)
            ) {

                items(
                    items = todos,
                    key = { it.id }
                ) { todo ->

                    TodoRow(
                        todo = todo,
                        onClick = {
                            onEditTodo(
                                todo.id
                            )
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
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


@Composable
private fun TodoRow(
    todo: TodoEntity,
    onClick: () -> Unit
) {

    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                Text(
                    text = if (todo.completed) {
                        "✓ ${todo.task}"
                    } else {
                        "☐ ${todo.task}"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Assigned to: ${todo.assignedTo}",
                    style = MaterialTheme.typography.bodySmall
                )

                if (todo.dueDate != null) {

                    Text(
                        text = "Due: ${formatDate(todo.dueDate)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (!todo.notes.isNullOrBlank()) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = todo.notes!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}