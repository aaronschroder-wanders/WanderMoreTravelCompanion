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
import com.wandermore.travelcompanion.ui.screens.HomeScreen
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
                                trips = tripViewModel.trips,

                                onCreateTrip = {

                                    navController.navigate("createTrip")

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

                    }
                }
            }
        }
    }
}