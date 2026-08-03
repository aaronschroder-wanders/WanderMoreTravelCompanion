package com.wandermore.travelcompanion.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.data.repository.TripRepository
import java.time.LocalDate

class TripViewModel : ViewModel() {

    private val repository = TripRepository()

    private val _trips = mutableStateListOf<Trip>()

    val trips: List<Trip>
        get() = _trips

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

        _trips.clear()
        _trips.addAll(repository.getTrips())
    }
}
