package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import com.wandermore.travelcompanion.data.model.Expense
import com.wandermore.travelcompanion.data.model.Trip
import com.wandermore.travelcompanion.data.repository.TripRepository
import java.time.LocalDate

class TripViewModel : ViewModel() {

    private val repository = TripRepository()


    private val expenses = mutableListOf<Expense>()


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


    fun updateTrip(trip: Trip) {

        repository.updateTrip(trip)

    }


    fun deleteTrip(id: Long) {

        repository.deleteTrip(id)

        expenses.removeAll {
            it.tripId == id
        }

    }


    // -------------------------
    // Expense functions
    // -------------------------


    fun addExpense(expense: Expense) {

        expenses.add(expense)

    }


    fun getExpensesForTrip(
        tripId: Long
    ): List<Expense> {

        return expenses.filter {
            it.tripId == tripId
        }

    }


    fun deleteExpense(
        expenseId: Int
    ) {

        expenses.removeAll {
            it.id == expenseId
        }

    }

}
