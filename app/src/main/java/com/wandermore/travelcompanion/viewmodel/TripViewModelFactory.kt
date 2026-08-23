package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wandermore.travelcompanion.database.ActivityDao
import com.wandermore.travelcompanion.database.ActivityDestinationDao
import com.wandermore.travelcompanion.database.DestinationDao
import com.wandermore.travelcompanion.database.ExpenseDao
import com.wandermore.travelcompanion.database.ItineraryDao
import com.wandermore.travelcompanion.database.ItineraryDestinationDao
import com.wandermore.travelcompanion.database.TodoDao
import com.wandermore.travelcompanion.database.TripDao
import com.wandermore.travelcompanion.database.TripDestinationDao
import com.wandermore.travelcompanion.database.TripEstimateDao

class TripViewModelFactory(
    private val tripDao: TripDao,
    private val expenseDao: ExpenseDao,
    private val todoDao: TodoDao,
    private val activityDao: ActivityDao,
    private val itineraryDao: ItineraryDao,
    private val tripEstimateDao: TripEstimateDao,
    private val destinationDao: DestinationDao,
    private val itineraryDestinationDao: ItineraryDestinationDao,
    private val activityDestinationDao: ActivityDestinationDao,
    private val tripDestinationDao: TripDestinationDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")

            return TripViewModel(
                tripDao,
                expenseDao,
                todoDao,
                activityDao,
                itineraryDao,
                tripEstimateDao,
                destinationDao,
                itineraryDestinationDao,
                activityDestinationDao,
                tripDestinationDao
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}