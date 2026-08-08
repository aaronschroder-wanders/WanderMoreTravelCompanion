package com.wandermore.travelcompanion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.database.TodoEntity
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.ui.screens.AddExpenseScreen
import com.wandermore.travelcompanion.ui.screens.AddTodoScreen
import com.wandermore.travelcompanion.ui.screens.ArchivedTripsScreen
import com.wandermore.travelcompanion.ui.screens.CategoryBreakdownScreen
import com.wandermore.travelcompanion.ui.screens.CategoryExpensesScreen
import com.wandermore.travelcompanion.ui.screens.CreateTripScreen
import com.wandermore.travelcompanion.ui.screens.EditExpenseScreen
import com.wandermore.travelcompanion.ui.screens.EditTodoScreen
import com.wandermore.travelcompanion.ui.screens.EditTripScreen
import com.wandermore.travelcompanion.ui.screens.ExchangeRateSettingsScreen
import com.wandermore.travelcompanion.ui.screens.HomeScreen
import com.wandermore.travelcompanion.ui.screens.TodoScreen
import com.wandermore.travelcompanion.ui.screens.TripDetailsScreen
import com.wandermore.travelcompanion.ui.screens.TripExpensesScreen
import com.wandermore.travelcompanion.ui.screens.TripHubScreen
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    tripViewModel: TripViewModel,
    exchangeRateViewModel: ExchangeRateViewModel
) {

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            // ---------------------------------------------------------
            // HOME
            // ---------------------------------------------------------

            composable("home") {

                val trips by tripViewModel
                    .getTrips()
                    .collectAsState(
                        initial = emptyList()
                    )

                HomeScreen(
                    trips = trips,
                    tripViewModel = tripViewModel,

                    onCreateTrip = {
                        navController.navigate(
                            "createTrip"
                        )
                    },

                    onArchivedTrips = {
                        navController.navigate(
                            "archivedTrips"
                        )
                    },

                    onSettings = {
                        navController.navigate(
                            "exchangeRates"
                        )
                    },

                    onTripSelected = { trip ->

                        navController.navigate(
                            "tripHub/${trip.id}"
                        )
                    }
                )
            }


            // ---------------------------------------------------------
            // ARCHIVED TRIPS
            // ---------------------------------------------------------

            composable("archivedTrips") {

                val trips by tripViewModel
                    .getTrips()
                    .collectAsState(
                        initial = emptyList()
                    )

                ArchivedTripsScreen(
                    trips = trips,
                    tripViewModel = tripViewModel,

                    onTripSelected = { trip ->

                        navController.navigate(
                            "tripHub/${trip.id}"
                        )
                    },

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }


            // ---------------------------------------------------------
            // CREATE TRIP
            // ---------------------------------------------------------

            composable("createTrip") {

                CreateTripScreen(
                    tripViewModel = tripViewModel,

                    onTripCreated = {
                        navController.popBackStack()
                    }
                )
            }


            // ---------------------------------------------------------
            // EXCHANGE RATE SETTINGS
            // ---------------------------------------------------------

            composable("exchangeRates") {

                ExchangeRateSettingsScreen(
                    exchangeRateViewModel =
                        exchangeRateViewModel,

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }


            // ---------------------------------------------------------
            // TRIP HUB
            // ---------------------------------------------------------

            composable(
                "tripHub/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    TripHubScreen(

                        tripId = tripId,

                        tripViewModel = tripViewModel,

                        onItinerary = {
                            // Coming next
                        },

                        onActivities = {
                            // Coming next
                        },

                        onToDo = {

                            navController.navigate(
                                "todo/$tripId"
                            )
                        },

                        onExpenses = {

                            navController.navigate(
                                "tripExpenses/$tripId"
                            )
                        },

                        onEditTrip = {

                            navController.navigate(
                                "editTrip/$tripId"
                            )
                        },

                        onStartTrip = {

                            tripViewModel.startTrip(
                                tripId
                            )
                        },

                        onArchiveTrip = {

                            tripViewModel.archiveTrip(
                                tripId
                            )
                        },

                        onRestoreTrip = {

                            tripViewModel.restoreTrip(
                                tripId,
                                it
                            )
                        },

                        onDeleteTrip = {

                            tripViewModel.deleteTrip(
                                tripId
                            )

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // TO DO
            // ---------------------------------------------------------

            composable(
                "todo/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    TodoScreen(

                        tripId = tripId,

                        tripViewModel =
                            tripViewModel,

                        onAddTodo = {

                            navController.navigate(
                                "addTodo/$tripId"
                            )
                        },

                        onEditTodo = { todoId ->

                            navController.navigate(
                                "editTodo/$todoId"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // ADD TO DO
            // ---------------------------------------------------------

            composable(
                "addTodo/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    AddTodoScreen(

                        tripId = tripId,

                        tripViewModel =
                            tripViewModel,

                        onTodoAdded = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // EDIT TO DO
            // ---------------------------------------------------------

            composable(
                "editTodo/{todoId}"
            ) { entry ->

                val todoId =
                    entry.arguments
                        ?.getString("todoId")
                        ?.toLongOrNull()

                var todo by remember {
                    mutableStateOf<TodoEntity?>(null)
                }

                LaunchedEffect(todoId) {

                    if (todoId != null) {

                        todo =
                            tripViewModel.getTodoById(
                                todoId
                            )
                    }
                }

                todo?.let {

                    EditTodoScreen(

                        todo = it,

                        tripViewModel =
                            tripViewModel,

                        onTodoUpdated = {

                            navController.popBackStack()
                        },

                        onDeleteTodo = {

                            tripViewModel.deleteTodo(
                                it
                            )

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // TRIP EXPENSES
            // ---------------------------------------------------------

            composable(
                "tripExpenses/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    TripExpensesScreen(

                        tripId = tripId,

                        tripViewModel = tripViewModel,

                        onAddExpense = {

                            navController.navigate(
                                "addExpense/$tripId"
                            )
                        },

                        onEditExpense = { expenseId ->

                            navController.navigate(
                                "editExpense/$expenseId"
                            )
                        },

                        onCategoryBreakdown = {

                            navController.navigate(
                                "categoryBreakdown/$tripId"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // OLD TRIP DETAILS
            // ---------------------------------------------------------
            // Kept temporarily while the Trip Hub replaces it.

            composable(
                "tripDetails/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    TripDetailsScreen(

                        tripId = tripId,

                        tripViewModel = tripViewModel,

                        onEditTrip = {

                            navController.navigate(
                                "editTrip/$tripId"
                            )
                        },

                        onAddExpense = {

                            navController.navigate(
                                "addExpense/$tripId"
                            )
                        },

                        onDeleteTrip = {

                            tripViewModel.deleteTrip(
                                tripId
                            )

                            navController.popBackStack()
                        },

                        onStartTrip = {

                            tripViewModel.startTrip(
                                tripId
                            )
                        },

                        onArchiveTrip = {

                            tripViewModel.archiveTrip(
                                tripId
                            )
                        },

                        onRestoreTrip = {

                            tripViewModel.restoreTrip(
                                tripId,
                                it
                            )
                        },

                        onEditExpense = { expenseId ->

                            navController.navigate(
                                "editExpense/$expenseId"
                            )
                        },

                        onCategoryBreakdown = {

                            navController.navigate(
                                "categoryBreakdown/$tripId"
                            )
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // CATEGORY BREAKDOWN
            // ---------------------------------------------------------

            composable(
                "categoryBreakdown/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                var trip by remember {
                    mutableStateOf<Trip?>(null)
                }

                LaunchedEffect(tripId) {

                    if (tripId != null) {

                        trip =
                            tripViewModel.getTripById(
                                tripId
                            )
                    }
                }

                trip?.let {

                    CategoryBreakdownScreen(

                        tripId = it.id,

                        currency = it.homeCurrency,

                        tripViewModel = tripViewModel,

                        onCategorySelected = { category ->

                            navController.navigate(
                                "categoryExpenses/${it.id}/$category"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // CATEGORY EXPENSES
            // ---------------------------------------------------------

            composable(
                "categoryExpenses/{tripId}/{category}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                val category =
                    entry.arguments
                        ?.getString("category")

                var trip by remember {
                    mutableStateOf<Trip?>(null)
                }

                LaunchedEffect(tripId) {

                    if (tripId != null) {

                        trip =
                            tripViewModel.getTripById(
                                tripId
                            )
                    }
                }

                if (
                    tripId != null &&
                    category != null &&
                    trip != null
                ) {

                    CategoryExpensesScreen(

                        tripId = tripId,

                        category = category,

                        currency =
                            trip!!.homeCurrency,

                        tripViewModel = tripViewModel,

                        onEditExpense = { expenseId ->

                            navController.navigate(
                                "editExpense/$expenseId"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // ADD EXPENSE
            // ---------------------------------------------------------

            composable(
                "addExpense/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                var trip by remember {
                    mutableStateOf<Trip?>(null)
                }

                LaunchedEffect(tripId) {

                    if (tripId != null) {

                        trip =
                            tripViewModel.getTripById(
                                tripId
                            )
                    }
                }

                trip?.let {

                    AddExpenseScreen(

                        trip = it,

                        tripViewModel =
                            tripViewModel,

                        exchangeRateViewModel =
                            exchangeRateViewModel,

                        onExpenseAdded = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // EDIT EXPENSE
            // ---------------------------------------------------------

            composable(
                "editExpense/{expenseId}"
            ) { entry ->

                val expenseId =
                    entry.arguments
                        ?.getString("expenseId")
                        ?.toLongOrNull()

                var expense by remember {
                    mutableStateOf<ExpenseEntity?>(null)
                }

                LaunchedEffect(expenseId) {

                    if (expenseId != null) {

                        expense =
                            tripViewModel.getExpenseById(
                                expenseId
                            )
                    }
                }

                expense?.let {

                    EditExpenseScreen(

                        expense = it,

                        tripViewModel =
                            tripViewModel,

                        exchangeRateViewModel =
                            exchangeRateViewModel,

                        onExpenseUpdated = {

                            navController.popBackStack()
                        },

                        onDeleteExpense = {

                            tripViewModel.deleteExpense(
                                it
                            )

                            navController.popBackStack()
                        }
                    )
                }
            }


            // ---------------------------------------------------------
            // EDIT TRIP
            // ---------------------------------------------------------

            composable(
                "editTrip/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                var trip by remember {
                    mutableStateOf<Trip?>(null)
                }

                LaunchedEffect(tripId) {

                    if (tripId != null) {

                        trip =
                            tripViewModel.getTripById(
                                tripId
                            )
                    }
                }

                trip?.let {

                    EditTripScreen(

                        trip = it,

                        tripViewModel =
                            tripViewModel,

                        onTripUpdated = {

                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}