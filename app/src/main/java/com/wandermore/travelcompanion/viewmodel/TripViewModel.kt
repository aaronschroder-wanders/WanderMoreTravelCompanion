package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.data.repository.TripRepository
import java.time.LocalDate

class TripViewModel : ViewModel() {

    private val repository = TripRepository()


    fun getTrips(): List<Trip> {
        return repository.getTrips()
    }


    fun getTripById(id: Long): Trip? {

        return repository.getTripById(id)

    }


    fun addTrip(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate,
        homeCurrency: String
    ) {

        repository.addTrip(
            name,
            startDate,
            endDate,
            homeCurrency
        )

    }
}
