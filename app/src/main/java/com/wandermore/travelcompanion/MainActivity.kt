package com.wandermore.travelcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.wandermore.travelcompanion.database.DatabaseProvider
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository
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

                AppNavigation(
                    navController = navController,
                    tripViewModel = tripViewModel,
                    exchangeRateViewModel = exchangeRateViewModel
                )
            }
        }
    }
}