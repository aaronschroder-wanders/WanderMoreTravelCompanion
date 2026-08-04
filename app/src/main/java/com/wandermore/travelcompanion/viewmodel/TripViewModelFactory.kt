package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wandermore.travelcompanion.database.ExpenseDao
import com.wandermore.travelcompanion.database.TripDao


class TripViewModelFactory(
    private val tripDao: TripDao,
    private val expenseDao: ExpenseDao
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {


        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {


            @Suppress("UNCHECKED_CAST")
            return TripViewModel(
                tripDao,
                expenseDao
            ) as T


        }


        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}