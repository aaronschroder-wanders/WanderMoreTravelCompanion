package com.wandermore.travelcompanion.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wandermore.travelcompanion.database.TodoEntity
import com.wandermore.travelcompanion.util.formatDate
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todo: TodoEntity,
    tripViewModel: TripViewModel,
    onTodoUpdated: () -> Unit,
    onDeleteTodo: () -> Unit
) {

    var task by remember {
        mutableStateOf(todo.task)
    }

    var dueDate by remember {
        mutableStateOf(todo.dueDate)
    }

    var assignedTo by remember {
        mutableStateOf(todo.assignedTo)
    }

    var completed by remember {
        mutableStateOf(todo.completed)
    }

    var notes by remember {
        mutableStateOf(todo.notes ?: "")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var assignedToExpanded by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState()
            )
            .imePadding()
            .padding(16.dp)
    ) {

        Text(
            text = "Edit To Do"
        )

        OutlinedTextField(
            value = task,
            onValueChange = {
                task = it
            },
            label = {
                Text("Task")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = {
                showDatePicker = true
            }
        ) {

            Text(
                text = dueDate?.let {
                    formatDate(it)
                } ?: "No due date"
            )
        }

        if (dueDate != null) {

            OutlinedButton(
                onClick = {
                    dueDate = null
                }
            ) {

                Text("Clear Date")
            }
        }

        Text(
            text = "Assigned To"
        )

        OutlinedButton(
            onClick = {
                assignedToExpanded = true
            }
        ) {

            Text(assignedTo)
        }

        DropdownMenu(
            expanded = assignedToExpanded,
            onDismissRequest = {
                assignedToExpanded = false
            }
        ) {

            listOf(
                "Me",
                "Sarah",
                "Both"
            ).forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(option)
                    },
                    onClick = {

                        assignedTo = option
                        assignedToExpanded = false
                    }
                )
            }
        }

        Text(
            text = "Completed"
        )

        Checkbox(
            checked = completed,
            onCheckedChange = {
                completed = it
            }
        )

        OutlinedTextField(
            value = notes,
            onValueChange = {
                notes = it
            },
            label = {
                Text("Notes")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                errorMessage = ""

                if (task.isBlank()) {

                    errorMessage =
                        "Please enter a task"

                } else {

                    val updatedTodo =
                        todo.copy(

                            task = task.trim(),

                            dueDate = dueDate,

                            assignedTo = assignedTo,

                            completed = completed,

                            notes =
                                notes
                                    .trim()
                                    .ifBlank {
                                        null
                                    }
                        )

                    tripViewModel.updateTodo(
                        updatedTodo
                    )

                    onTodoUpdated()
                }
            }
        ) {

            Text(
                text = "Save Changes"
            )
        }

        TextButton(
            onClick = {
                showDeleteConfirmation = true
            }
        ) {

            Text(
                text = "Delete To Do"
            )
        }

        if (errorMessage.isNotBlank()) {

            Text(
                text = errorMessage
            )
        }
    }

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState()

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        dueDate =
                            datePickerState
                                .selectedDateMillis
                                ?.let {

                                    Instant
                                        .ofEpochMilli(it)
                                        .atZone(
                                            ZoneId.systemDefault()
                                        )
                                        .toLocalDate()
                                }

                        showDatePicker = false
                    }
                ) {

                    Text("OK")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showDeleteConfirmation) {

        androidx.compose.material3.AlertDialog(

            onDismissRequest = {
                showDeleteConfirmation = false
            },

            title = {
                Text("Delete To Do?")
            },

            text = {
                Text(
                    "Are you sure you want to delete this To Do item?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showDeleteConfirmation = false
                        onDeleteTodo()
                    }
                ) {

                    Text("Delete")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}