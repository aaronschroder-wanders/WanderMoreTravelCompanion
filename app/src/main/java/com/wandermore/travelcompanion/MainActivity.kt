package com.wandermore.travelcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wandermore.travelcompanion.database.DatabaseProvider
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.ui.screens.AddExpenseScreen
import com.wandermore.travelcompanion.ui.screens.ArchivedTripsScreen
import com.wandermore.travelcompanion.ui.screens.CategoryBreakdownScreen
import com.wandermore.travelcompanion.ui.screens.CategoryExpensesScreen
import com.wandermore.travelcompanion.ui.screens.CreateTripScreen
import com.wandermore.travelcompanion.ui.screens.ExchangeRateSettingsScreen
import com.wandermore.travelcompanion.ui.screens.EditExpenseScreen
import com.wandermore.travelcompanion.ui.screens.EditTripScreen
import com.wandermore.travelcompanion.ui.screens.HomeScreen
import com.wandermore.travelcompanion.ui.screens.TripDetailsScreen
import com.wandermore.travelcompanion.ui.theme.WanderMoreTravelCompanionTheme
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModelFactory
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModelFactory

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        setContent {


            WanderMoreTravelCompanionTheme {


                val navController =
                    rememberNavController()


                val database =
                    DatabaseProvider.getDatabase(
                        applicationContext
                    )


                val tripViewModel: TripViewModel =
                    viewModel(

                        factory = TripViewModelFactory(

                            database.tripDao(),

                            database.expenseDao()

                        )

                    )


                val exchangeRateRepository =
                    remember {

                        ExchangeRateRepository(
                            database.exchangeRateDao()
                        )

                    }


                val exchangeRateViewModel:
                        ExchangeRateViewModel =
                    viewModel(

                        factory =
                            ExchangeRateViewModelFactory(
                                exchangeRateRepository
                            )

                    )


                LaunchedEffect(Unit) {

                    exchangeRateViewModel
                        .initialiseRates()

                }



                Scaffold(

                    modifier =
                        Modifier.fillMaxSize()

                ) { innerPadding ->


                    NavHost(

                        navController = navController,

                        startDestination = "home",

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )

                    ) {


                        composable("home") {


                            val trips by tripViewModel
                                .getTrips()
                                .collectAsState(
                                    initial = emptyList()
                                )


                            HomeScreen(

                                trips = trips,


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
                                        "tripDetails/${trip.id}"
                                    )

                                }

                            )

                        }




                        composable("archivedTrips") {


                            val trips by tripViewModel
                                .getTrips()
                                .collectAsState(
                                    initial = emptyList()
                                )


                            ArchivedTripsScreen(

                                trips = trips,


                                onTripSelected = { trip ->

                                    navController.navigate(
                                        "tripDetails/${trip.id}"
                                    )

                                },


                                onBack = {

                                    navController.popBackStack()

                                }

                            )

                        }




                        composable("createTrip") {


                            CreateTripScreen(

                                tripViewModel = tripViewModel,


                                onTripCreated = {

                                    navController.popBackStack()

                                }

                            )

                        }

                        composable("exchangeRates") {


                            ExchangeRateSettingsScreen(

                                exchangeRateViewModel = exchangeRateViewModel,

                                onBack = {

                                    navController.popBackStack()

                                }

                            )


                        }


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
                                        tripViewModel
                                            .getTripById(
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
                                    tripViewModel
                                        .getTripById(
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

                                currency = trip!!.homeCurrency,

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
                                        tripViewModel
                                            .getTripById(
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
                                        tripViewModel
                                            .getExpenseById(
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

                                        tripViewModel
                                            .deleteExpense(
                                                it
                                            )

                                        navController
                                            .popBackStack()

                                    }

                                )

                            }

                        }




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
                                        tripViewModel
                                            .getTripById(
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

                                        navController
                                            .popBackStack()

                                    }

                                )

                            }

                        }


                    }

                }

            }

        }

    }

}