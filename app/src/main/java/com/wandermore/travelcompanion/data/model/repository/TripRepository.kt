package com.wandermore.travelcompanion.data.repository

import com.wandermore.travelcompanion.data.model.Trip
import java.time.LocalDate

class TripRepository {

    private val trips = mutableListOf<Trip>()


    fun getTrips(): List<Trip> {
        return trips
    }


    fun getTripById(id: Long): Trip? {

        return trips.find { trip ->
            trip.id == id
        }

    }


    fun addTrip(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate,
        homeCurrency: String
    ) {

        val trip = Trip(
            id = trips.size.toLong() + 1,
            name = name,
            startDate = startDate,
            endDate = endDate,
            homeCurrency = homeCurrency
        )

        trips.add(trip)
    }


    fun updateTrip(updatedTrip: Trip) {

        val index = trips.indexOfFirst { trip ->
            trip.id == updatedTrip.id
        }


        if (index != -1) {

            trips[index] = updatedTrip

        }

    }


    fun deleteTrip(id: Long) {

        trips.removeIf { trip ->

            trip.id == id

        }

    }
}
