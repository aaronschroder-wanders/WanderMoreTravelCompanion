package com.wandermore.travelcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.wandermore.travelcompanion.data.repository.ExchangeRateRepository
import com.wandermore.travelcompanion.data.repository.UserSettingsRepository
import com.wandermore.travelcompanion.data.service.CurrencyRateService
import com.wandermore.travelcompanion.database.DatabaseProvider
import com.wandermore.travelcompanion.ui.theme.WanderMoreTravelCompanionTheme
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModel
import com.wandermore.travelcompanion.viewmodel.ExchangeRateViewModelFactory
import com.wandermore.travelcompanion.viewmodel.TripViewModel
import com.wandermore.travelcompanion.viewmodel.TripViewModelFactory
import com.wandermore.travelcompanion.viewmodel.UserSettingsViewModel
import com.wandermore.travelcompanion.viewmodel.UserSettingsViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

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
                        factory =
                            TripViewModelFactory(
                                database.tripDao(),
                                database.expenseDao(),
                                database.todoDao(),
                                database.activityDao(),
                                database.itineraryDao(),
                                database.tripEstimateDao(),
                                database.destinationDao(),
                                database.itineraryDestinationDao(),
                                database.activityDestinationDao(),
                                database.tripDestinationDao()
                            )
                    )

                val currencyRateService =
                    remember {

                        CurrencyRateService()

                    }

                val exchangeRateRepository =
                    remember {

                        ExchangeRateRepository(
                            database.exchangeRateDao(),
                            currencyRateService
                        )

                    }

                val userSettingsRepository =
                    remember {

                        UserSettingsRepository(
                            applicationContext
                        )

                    }

                val userSettingsViewModel:
                        UserSettingsViewModel =
                    viewModel(
                        factory =
                            UserSettingsViewModelFactory(
                                userSettingsRepository
                            )
                    )

                val exchangeRateViewModel:
                        ExchangeRateViewModel =
                    viewModel(
                        factory =
                            ExchangeRateViewModelFactory(
                                exchangeRateRepository,
                                userSettingsRepository
                            )
                    )

                LaunchedEffect(Unit) {

                    exchangeRateViewModel
                        .loadRates()

                }

                AppNavigation(
                    navController = navController,
                    tripViewModel = tripViewModel,
                    exchangeRateViewModel =
                        exchangeRateViewModel,
                    userSettingsViewModel =
                        userSettingsViewModel,
                    userSettingsRepository =
                        userSettingsRepository,
                    database = database
                )
            }
        }
    }
}