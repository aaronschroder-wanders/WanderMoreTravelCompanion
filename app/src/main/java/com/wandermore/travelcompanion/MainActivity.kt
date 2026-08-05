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
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.database.DatabaseProvider
import com.wandermore.travelcompanion.ui.screens.AddExpenseScreen
import com.wandermore.travelcompanion.ui.screens.CreateTripScreen
import com.wandermore.travelcompanion.ui.screens.EditExpenseScreen
import com.wandermore.travelcompanion.ui.screens.EditTripScreen
import com.wandermore.travelcompanion.ui.screens.HomeScreen
import com.wandermore.travelcompanion.ui.screens.TripDetailsScreen
import com.wandermore.travelcompanion.ui.theme.WanderMoreTravelCompanionTheme
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModelFactory


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        setContent {


            WanderMoreTravelCompanionTheme {


                val navController = rememberNavController()


                val database =
                    DatabaseProvider.getDatabase(
                        applicationContext
                    )


                val tripViewModel: TripViewModel = viewModel(
                    factory = TripViewModelFactory(
                        database.tripDao(),
                        database.expenseDao()
                    )
                )


                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->


                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
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


                                onTripSelected = { trip ->

                                    navController.navigate(
                                        "tripDetails/${trip.id}"
                                    )

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



                        composable(
                            "tripDetails/{tripId}"
                        ) {


                            val tripId =
                                it.arguments
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


                                    onEditExpense = { expenseId ->

                                        navController.navigate(
                                            "editExpense/$expenseId"
                                        )

                                    }

                                )

                            }

                        }



                        composable(
                            "addExpense/{tripId}"
                        ) { backStackEntry ->


                            val tripId =
                                backStackEntry.arguments
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

                                    tripViewModel = tripViewModel,

                                    onExpenseAdded = {

                                        navController.popBackStack()

                                    }

                                )

                            }

                        }



                        composable(
                            "editExpense/{expenseId}"
                        ) { backStackEntry ->


                            val expenseId =
                                backStackEntry.arguments
                                    ?.getString("expenseId")
                                    ?.toLongOrNull()


                            var expense by remember {
                                mutableStateOf<com.wandermore.travelcompanion.database.ExpenseEntity?>(null)
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

                                    tripViewModel = tripViewModel,


                                    onExpenseUpdated = {

                                        navController.popBackStack()

                                    },


                                    onDeleteExpense = {

                                        tripViewModel.deleteExpense(it)

                                        navController.popBackStack()

                                    }

                                )

                            }

                        }



                        composable(
                            "editTrip/{tripId}"
                        ) { backStackEntry ->


                            val tripId =
                                backStackEntry.arguments
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

                                    tripViewModel = tripViewModel,

                                    onTripUpdated = {

                                        navController.popBackStack()

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