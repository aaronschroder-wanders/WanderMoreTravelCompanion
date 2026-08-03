package com.wandermore.travelcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wandermore.travelcompanion.ui.screens.CreateTripScreen
import com.wandermore.travelcompanion.ui.screens.EditTripScreen
import com.wandermore.travelcompanion.ui.screens.HomeScreen
import com.wandermore.travelcompanion.ui.screens.TripDetailsScreen
import com.wandermore.travelcompanion.ui.theme.WanderMoreTravelCompanionTheme
import com.wandermore.travelcompanion.viewmodel.TripViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            WanderMoreTravelCompanionTheme {

                val navController = rememberNavController()

                val tripViewModel: TripViewModel = viewModel()


                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->


                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {


                        composable("home") {

                            HomeScreen(
                                trips = tripViewModel.getTrips(),

                                onCreateTrip = {

                                    navController.navigate("createTrip")

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

                                    navController.navigate("home")

                                }
                            )

                        }


                        composable("tripDetails/{tripId}") { backStackEntry ->


                            val tripId =
                                backStackEntry.arguments
                                    ?.getString("tripId")
                                    ?.toLong()


                            val trip =
                                tripId?.let {
                                    tripViewModel.getTripById(it)
                                }


                            if (trip != null) {

                                TripDetailsScreen(

                                    trip = trip,


                                    onEditTrip = {

                                        navController.navigate(
                                            "editTrip/${trip.id}"
                                        )

                                    },


                                    onDeleteTrip = {

                                        tripViewModel.deleteTrip(
                                            trip.id
                                        )


                                        navController.navigate(
                                            "home"
                                        )

                                    }

                                )

                            }

                        }


                        composable("editTrip/{tripId}") { backStackEntry ->


                            val tripId =
                                backStackEntry.arguments
                                    ?.getString("tripId")
                                    ?.toLong()


                            val trip =
                                tripId?.let {
                                    tripViewModel.getTripById(it)
                                }


                            if (trip != null) {

                                EditTripScreen(

                                    trip = trip,

                                    tripViewModel = tripViewModel,


                                    onTripUpdated = {

                                        navController.navigate(
                                            "tripDetails/${trip.id}"
                                        )

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