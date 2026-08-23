package com.wandermore.travelcompanion

import androidx.activity.result.contract.ActivityResultContracts
import java.time.LocalDateTime
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.wandermore.travelcompanion.database.ActivityEntity
import com.wandermore.travelcompanion.database.AppDatabase
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.database.TodoEntity
import com.wandermore.travelcompanion.database.TripEstimateEntity
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.ui.screens.ActivitiesScreen
import com.wandermore.travelcompanion.ui.screens.AddActivityScreen
import com.wandermore.travelcompanion.ui.screens.AddExpenseScreen
import com.wandermore.travelcompanion.ui.screens.AddItineraryScreen
import com.wandermore.travelcompanion.ui.screens.AddTodoScreen
import com.wandermore.travelcompanion.ui.screens.AddTripEstimateScreen
import com.wandermore.travelcompanion.ui.screens.ArchivedTripsScreen
import com.wandermore.travelcompanion.ui.screens.CategoryBreakdownScreen
import com.wandermore.travelcompanion.ui.screens.CategoryExpensesScreen
import com.wandermore.travelcompanion.ui.screens.CreateTripScreen
import com.wandermore.travelcompanion.ui.screens.EditActivityScreen
import com.wandermore.travelcompanion.ui.screens.EditExpenseScreen
import com.wandermore.travelcompanion.ui.screens.EditItineraryScreen
import com.wandermore.travelcompanion.ui.screens.EditTodoScreen
import com.wandermore.travelcompanion.ui.screens.EditTripScreen
import com.wandermore.travelcompanion.ui.screens.EditTripEstimateScreen
import com.wandermore.travelcompanion.ui.screens.ExchangeRateSettingsScreen
import com.wandermore.travelcompanion.ui.screens.HomeScreen
import com.wandermore.travelcompanion.ui.screens.ItineraryDetailsScreen
import com.wandermore.travelcompanion.ui.screens.ItineraryScreen
import com.wandermore.travelcompanion.ui.screens.SettingsScreen
import com.wandermore.travelcompanion.ui.screens.TodoScreen
import com.wandermore.travelcompanion.ui.screens.TripDetailsScreen
import com.wandermore.travelcompanion.ui.screens.TripEstimatesScreen
import com.wandermore.travelcompanion.ui.screens.TripExpensesScreen
import com.wandermore.travelcompanion.ui.screens.TripHubScreen
import com.wandermore.travelcompanion.ui.screens.DestinationsScreen
import com.wandermore.travelcompanion.ui.screens.DestinationDetailsScreen
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import androidx.compose.runtime.rememberCoroutineScope
import com.wandermore.travelcompanion.data.repository.BackupRepository
import kotlinx.coroutines.launch
import android.widget.Toast
import com.wandermore.travelcompanion.viewmodel.UserSettingsViewModel
import com.wandermore.travelcompanion.data.repository.UserSettingsRepository


@Composable
fun AppNavigation(
    navController: NavHostController,
    tripViewModel: TripViewModel,
    exchangeRateViewModel: ExchangeRateViewModel,
    userSettingsViewModel: UserSettingsViewModel,
    userSettingsRepository: UserSettingsRepository,
    database: AppDatabase
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val backupRepository =
        remember(
            database,
            userSettingsRepository
        ) {
            BackupRepository(
                database = database,
                userSettingsRepository =
                    userSettingsRepository
            )
        }

    // =========================================================
    // BACKUP FILE CREATION
    // =========================================================

    val backupLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.CreateDocument(
                    "application/json"
                )
        ) { uri: Uri? ->

            if (uri != null) {

                coroutineScope.launch {

                    val backupJson =
                        backupRepository.createBackup()

                    context.contentResolver
                        .openOutputStream(uri)
                        ?.bufferedWriter()
                        ?.use { writer ->

                            writer.write(
                                backupJson
                            )
                        }

                    Toast.makeText(
                        context,
                        "Backup Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    // =========================================================
    // RESTORE FILE PICKER
    // =========================================================

    val restoreLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri != null) {

                coroutineScope.launch {

                    val backupJson =
                        context.contentResolver
                            .openInputStream(uri)
                            ?.bufferedReader()
                            ?.use { it.readText() }

                    if (backupJson != null) {

                        backupRepository.restoreBackup(
                            backupJson
                        )

                        Toast.makeText(
                            context,
                            "Restore Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            // =========================================================
            // HOME
            // =========================================================

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
                            "settings"
                        )
                    },

                    onTripSelected = { trip ->

                        navController.navigate(
                            "tripHub/${trip.id}"
                        )
                    }
                )
            }


            // =========================================================
            // ARCHIVED TRIPS
            // =========================================================

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


            // =========================================================
            // CREATE TRIP
            // =========================================================

            composable("createTrip") {

                CreateTripScreen(
                    tripViewModel = tripViewModel,
                    userSettingsViewModel = userSettingsViewModel,

                    onTripCreated = {
                        navController.popBackStack()
                    }
                )
            }


            // =========================================================
            // SETTINGS
            // =========================================================

            composable("settings") {

                SettingsScreen(

                    userSettingsViewModel =
                        userSettingsViewModel,

                    onExchangeRates = {

                        navController.navigate(
                            "exchangeRates"
                        )
                    },

                    onDestinations = {

                        navController.navigate(
                            "destinations"
                        )
                    },

                    onBackup = {

                        val filename =
                            "WanderMore_Backup_${
                                LocalDateTime.now()
                                    .toString()
                                    .replace(":", "-")
                                    .substringBefore(".")
                            }.json"

                        backupLauncher.launch(
                            filename
                        )

                    },

                    onRestore = {

                        restoreLauncher.launch(
                            arrayOf("application/json")
                        )

                    },

                    onBack = {

                        navController.popBackStack()

                    }

                )
            }


            // =========================================================
            // EXCHANGE RATE SETTINGS
            // =========================================================

            composable("exchangeRates") {

                ExchangeRateSettingsScreen(
                    exchangeRateViewModel =
                        exchangeRateViewModel,

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }


            // =========================================================
            // TRIP HUB
            // =========================================================

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

                        tripViewModel =
                            tripViewModel,

                        onItinerary = {

                            navController.navigate(
                                "itinerary/$tripId"
                            )
                        },

                        onActivities = {

                            navController.navigate(
                                "activities/$tripId"
                            )
                        },

                        onToDo = {

                            navController.navigate(
                                "todo/$tripId"
                            )
                        },

                        onDestinations = {

                            navController.navigate(
                                "destinations/$tripId"
                            )
                        },

                        onExpenses = {

                            navController.navigate(
                                "tripExpenses/$tripId"
                            )
                        },

                        onEstimates = {

                            navController.navigate(
                                "tripEstimates/$tripId"
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

                        onRestoreTrip = { status ->

                            tripViewModel.restoreTrip(
                                tripId,
                                status
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


            // =========================================================
            // TRIP ESTIMATES
            // =========================================================

            composable(
                "tripEstimates/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    TripEstimatesScreen(

                        tripId = tripId,

                        tripViewModel =
                            tripViewModel,

                        onAddEstimate = {

                            navController.navigate(
                                "addTripEstimate/$tripId"
                            )
                        },

                        onEditEstimate = { estimateId ->

                            navController.navigate(
                                "editTripEstimate/$estimateId"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // ADD TRIP ESTIMATE
            // =========================================================

            composable(
                "addTripEstimate/{tripId}"
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

                trip?.let { item ->

                    AddTripEstimateScreen(

                        trip = item,

                        tripViewModel =
                            tripViewModel,

                        exchangeRateViewModel =
                            exchangeRateViewModel,

                        onEstimateAdded = {

                            navController.popBackStack()
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // EDIT TRIP ESTIMATE
            // =========================================================

            composable(
                "editTripEstimate/{estimateId}"
            ) { entry ->

                val estimateId =
                    entry.arguments
                        ?.getString("estimateId")
                        ?.toLongOrNull()

                var estimate by remember {
                    mutableStateOf<TripEstimateEntity?>(null)
                }

                var showDeleteEstimateDialog by remember {
                    mutableStateOf(false)
                }

                LaunchedEffect(estimateId) {

                    if (estimateId != null) {

                        estimate =
                            tripViewModel.getTripEstimateById(
                                estimateId
                            )
                    }
                }

                // =====================================================
                // DELETE ESTIMATE CONFIRMATION
                // =====================================================

                if (showDeleteEstimateDialog && estimate != null) {

                    AlertDialog(
                        onDismissRequest = {
                            showDeleteEstimateDialog = false
                        },

                        title = {
                            Text("Delete estimate?")
                        },

                        text = {
                            Text(
                                "Are you sure you want to delete this estimate? " +
                                        "This cannot be undone."
                            )
                        },

                        confirmButton = {

                            Button(
                                onClick = {

                                    tripViewModel.deleteTripEstimate(
                                        estimate!!
                                    )

                                    showDeleteEstimateDialog = false

                                    navController.popBackStack()
                                }
                            ) {
                                Text("Delete")
                            }
                        },

                        dismissButton = {

                            TextButton(
                                onClick = {
                                    showDeleteEstimateDialog = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                estimate?.let { item ->

                    EditTripEstimateScreen(

                        estimate = item,

                        tripViewModel =
                            tripViewModel,

                        exchangeRateViewModel =
                            exchangeRateViewModel,

                        onEstimateUpdated = {

                            navController.popBackStack()
                        },

                        onDeleteEstimate = {

                            showDeleteEstimateDialog = true
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // ITINERARY LIST
            // =========================================================

            composable(
                "itinerary/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    ItineraryScreen(

                        tripId = tripId,

                        tripViewModel =
                            tripViewModel,

                        onAddItinerary = {

                            navController.navigate(
                                "addItinerary/$tripId"
                            )
                        },

                        onItinerarySelected = { itineraryId ->

                            navController.navigate(
                                "itineraryDetails/$itineraryId"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // ADD ITINERARY
            // =========================================================

            composable(
                "addItinerary/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    AddItineraryScreen(

                        tripId = tripId,

                        tripViewModel =
                            tripViewModel,

                        onItineraryAdded = {

                            navController.popBackStack()
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // ITINERARY DETAILS
            // =========================================================

            composable(
                "itineraryDetails/{itineraryId}"
            ) { entry ->

                val itineraryId =
                    entry.arguments
                        ?.getString("itineraryId")
                        ?.toLongOrNull()

                var itinerary by remember {
                    mutableStateOf<ItineraryEntity?>(null)
                }

                LaunchedEffect(itineraryId) {

                    if (itineraryId != null) {

                        itinerary =
                            tripViewModel.getItineraryById(
                                itineraryId
                            )
                    }
                }

                itinerary?.let { item ->

                    ItineraryDetailsScreen(

                        itinerary = item,

                        onEdit = {

                            navController.navigate(
                                "editItinerary/${item.id}"
                            )
                        },

                        onDelete = {

                            tripViewModel.deleteItinerary(
                                item
                            )

                            navController.popBackStack()
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // EDIT ITINERARY
            // =========================================================

            composable(
                "editItinerary/{itineraryId}"
            ) { entry ->

                val itineraryId =
                    entry.arguments
                        ?.getString("itineraryId")
                        ?.toLongOrNull()

                if (itineraryId != null) {

                    EditItineraryScreen(

                        itineraryId = itineraryId,

                        tripViewModel =
                            tripViewModel,

                        onItineraryUpdated = {

                            navController.popBackStack()
                        },

                        onDeleteItinerary = { item ->

                            tripViewModel.deleteItinerary(
                                item
                            )

                            navController.popBackStack()
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // ACTIVITIES & ATTRACTIONS
            // =========================================================

            composable(
                "activities/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    ActivitiesScreen(

                        tripId = tripId,

                        tripViewModel =
                            tripViewModel,

                        onAddActivity = {

                            navController.navigate(
                                "addActivity/$tripId"
                            )
                        },

                        onEditActivity = { activityId ->

                            navController.navigate(
                                "editActivity/$activityId"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // ADD ACTIVITY
            // =========================================================

            composable(
                "addActivity/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    AddActivityScreen(

                        tripId = tripId,

                        tripViewModel =
                            tripViewModel,

                        exchangeRateViewModel =
                            exchangeRateViewModel,

                        onActivityAdded = {

                            navController.popBackStack()
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // EDIT ACTIVITY
            // =========================================================

            composable(
                "editActivity/{activityId}"
            ) { entry ->

                val activityId =
                    entry.arguments
                        ?.getString("activityId")
                        ?.toLongOrNull()

                var activity by remember {
                    mutableStateOf<ActivityEntity?>(null)
                }

                LaunchedEffect(activityId) {

                    if (activityId != null) {

                        activity =
                            tripViewModel.getActivityById(
                                activityId
                            )
                    }
                }

                activity?.let { item ->

                    EditActivityScreen(

                        activity = item,

                        tripViewModel =
                            tripViewModel,

                        exchangeRateViewModel =
                            exchangeRateViewModel,

                        onActivityUpdated = {

                            navController.popBackStack()
                        },

                        onDeleteActivity = {

                            tripViewModel.deleteActivity(
                                item
                            )

                            navController.popBackStack()
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // TO DO
            // =========================================================

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


            // =========================================================
            // ADD TO DO
            // =========================================================

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


            // =========================================================
            // EDIT TO DO
            // =========================================================

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

                todo?.let { item ->

                    EditTodoScreen(

                        todo = item,

                        tripViewModel =
                            tripViewModel,

                        onTodoUpdated = {

                            navController.popBackStack()
                        },

                        onDeleteTodo = {

                            tripViewModel.deleteTodo(
                                item
                            )

                            navController.popBackStack()
                        }
                    )
                }
            }

// =========================================================
// DESTINATIONS
// =========================================================

            composable(
                "destinations/{tripId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                if (tripId != null) {

                    DestinationsScreen(

                        tripId = tripId,

                        tripViewModel =
                            tripViewModel,

                        onDestinationClick = { destinationId ->

                            navController.navigate(
                                "destinationDetails/$tripId/$destinationId"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


// =========================================================
// DESTINATION DETAILS
// =========================================================

            composable(
                "destinationDetails/{tripId}/{destinationId}"
            ) { entry ->

                val tripId =
                    entry.arguments
                        ?.getString("tripId")
                        ?.toLongOrNull()

                val destinationId =
                    entry.arguments
                        ?.getString("destinationId")
                        ?.toLongOrNull()

                if (
                    tripId != null &&
                    destinationId != null
                ) {

                    DestinationDetailsScreen(

                        tripId = tripId,

                        destinationId =
                            destinationId,

                        tripViewModel =
                            tripViewModel,

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }

            // =========================================================
            // TRIP EXPENSES
            // =========================================================

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

                        tripViewModel =
                            tripViewModel,

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


            // =========================================================
            // OLD TRIP DETAILS
            // =========================================================
            // Kept temporarily while Trip Hub replaces it.

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

                        tripViewModel =
                            tripViewModel,

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

                        onRestoreTrip = { status ->

                            tripViewModel.restoreTrip(
                                tripId,
                                status
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


            // =========================================================
            // CATEGORY BREAKDOWN
            // =========================================================

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

                trip?.let { item ->

                    CategoryBreakdownScreen(

                        tripId = item.id,

                        currency =
                            item.homeCurrency,

                        tripViewModel =
                            tripViewModel,

                        onCategorySelected = { category ->

                            navController.navigate(
                                "categoryExpenses/${item.id}/$category"
                            )
                        },

                        onBack = {

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // CATEGORY EXPENSES
            // =========================================================

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

                        tripViewModel =
                            tripViewModel,

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


            // =========================================================
            // ADD EXPENSE
            // =========================================================

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

                trip?.let { item ->

                    AddExpenseScreen(

                        trip = item,

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


            // =========================================================
            // EDIT EXPENSE
            // =========================================================

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

                expense?.let { item ->

                    EditExpenseScreen(

                        expense = item,

                        tripViewModel =
                            tripViewModel,

                        exchangeRateViewModel =
                            exchangeRateViewModel,

                        onExpenseUpdated = {

                            navController.popBackStack()
                        },

                        onDeleteExpense = {

                            tripViewModel.deleteExpense(
                                item
                            )

                            navController.popBackStack()
                        }
                    )
                }
            }


            // =========================================================
            // EDIT TRIP
            // =========================================================

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

                trip?.let { item ->

                    EditTripScreen(

                        trip = item,

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