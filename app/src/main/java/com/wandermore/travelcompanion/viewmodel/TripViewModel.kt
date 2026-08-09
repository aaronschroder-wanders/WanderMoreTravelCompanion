package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wandermore.travelcompanion.data.repository.TripRepository
import com.wandermore.travelcompanion.database.ActivityDao
import com.wandermore.travelcompanion.database.ActivityEntity
import com.wandermore.travelcompanion.database.ExpenseDao
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.database.ItineraryDao
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.database.TodoDao
import com.wandermore.travelcompanion.database.TodoEntity
import com.wandermore.travelcompanion.database.TripDao
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.model.TripStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TripViewModel(
    tripDao: TripDao,
    private val expenseDao: ExpenseDao,
    private val todoDao: TodoDao,
    private val activityDao: ActivityDao,
    private val itineraryDao: ItineraryDao
) : ViewModel() {

    private val repository = TripRepository(
        tripDao
    )


    // ---------------------------------------------------------
    // Trip functions
    // ---------------------------------------------------------

    fun getTrips(): Flow<List<Trip>> {

        return repository.getTrips()
    }


    fun getTripByIdFlow(
        id: Long
    ): Flow<Trip?> {

        return repository.getTripByIdFlow(
            id
        )
    }


    suspend fun getTripById(
        id: Long
    ): Trip? {

        return repository.getTripById(
            id
        )
    }


    fun addTrip(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate,
        homeCurrency: String
    ) {

        viewModelScope.launch {

            repository.addTrip(
                name,
                startDate,
                endDate,
                homeCurrency
            )
        }
    }


    fun updateTrip(
        trip: Trip
    ) {

        viewModelScope.launch {

            repository.updateTrip(
                trip
            )
        }
    }


    // ---------------------------------------------------------
    // Trip status functions
    // ---------------------------------------------------------

    fun startTrip(
        tripId: Long
    ) {

        viewModelScope.launch {

            repository.updateTripStatus(
                tripId,
                TripStatus.CURRENT
            )
        }
    }


    fun archiveTrip(
        tripId: Long
    ) {

        viewModelScope.launch {

            repository.updateTripStatus(
                tripId,
                TripStatus.ARCHIVED
            )
        }
    }


    fun restoreTrip(
        tripId: Long,
        status: TripStatus
    ) {

        viewModelScope.launch {

            repository.updateTripStatus(
                tripId,
                status
            )
        }
    }


    fun deleteTrip(
        id: Long
    ) {

        viewModelScope.launch {

            val trip =
                repository.getTripById(
                    id
                )

            if (trip != null) {

                repository.deleteTrip(
                    trip
                )
            }
        }
    }


    // ---------------------------------------------------------
    // Expense functions
    // ---------------------------------------------------------

    fun addExpense(
        expense: ExpenseEntity
    ) {

        viewModelScope.launch {

            expenseDao.insertExpense(
                expense
            )
        }
    }


    fun getExpensesForTrip(
        tripId: Long
    ): Flow<List<ExpenseEntity>> {

        return expenseDao.getExpensesForTrip(
            tripId
        )
    }


    suspend fun getExpenseById(
        expenseId: Long
    ): ExpenseEntity? {

        return expenseDao.getExpenseById(
            expenseId
        )
    }


    fun deleteExpense(
        expense: ExpenseEntity
    ) {

        viewModelScope.launch {

            expenseDao.deleteExpense(
                expense
            )
        }
    }


    fun updateExpense(
        expense: ExpenseEntity
    ) {

        viewModelScope.launch {

            expenseDao.updateExpense(
                expense
            )
        }
    }


    // ---------------------------------------------------------
    // To Do functions
    // ---------------------------------------------------------

    fun addTodo(
        todo: TodoEntity
    ) {

        viewModelScope.launch {

            todoDao.insertTodo(
                todo
            )
        }
    }


    fun getTodosForTrip(
        tripId: Long
    ): Flow<List<TodoEntity>> {

        return todoDao.getTodosForTrip(
            tripId
        )
    }


    suspend fun getTodoById(
        todoId: Long
    ): TodoEntity? {

        return todoDao.getTodoById(
            todoId
        )
    }


    fun updateTodo(
        todo: TodoEntity
    ) {

        viewModelScope.launch {

            todoDao.updateTodo(
                todo
            )
        }
    }


    fun deleteTodo(
        todo: TodoEntity
    ) {

        viewModelScope.launch {

            todoDao.deleteTodo(
                todo
            )
        }
    }


    // ---------------------------------------------------------
    // Activity functions
    // ---------------------------------------------------------

    fun addActivity(
        activity: ActivityEntity
    ) {

        viewModelScope.launch {

            activityDao.insertActivity(
                activity
            )
        }
    }


    fun getActivitiesForTrip(
        tripId: Long
    ): Flow<List<ActivityEntity>> {

        return activityDao.getActivitiesForTrip(
            tripId
        )
    }


    suspend fun getActivityById(
        activityId: Long
    ): ActivityEntity? {

        return activityDao.getActivityById(
            activityId
        )
    }


    fun updateActivity(
        activity: ActivityEntity
    ) {

        viewModelScope.launch {

            activityDao.updateActivity(
                activity
            )
        }
    }


    fun deleteActivity(
        activity: ActivityEntity
    ) {

        viewModelScope.launch {

            activityDao.deleteActivity(
                activity
            )
        }
    }


    // ---------------------------------------------------------
    // Itinerary functions
    // ---------------------------------------------------------

    fun addItinerary(
        itinerary: ItineraryEntity
    ) {

        viewModelScope.launch {

            itineraryDao.insertItinerary(
                itinerary
            )
        }
    }


    fun getItineraryForTrip(
        tripId: Long
    ): Flow<List<ItineraryEntity>> {

        return itineraryDao.getItineraryForTrip(
            tripId
        )
    }


    suspend fun getItineraryById(
        itineraryId: Long
    ): ItineraryEntity? {

        return itineraryDao.getItineraryById(
            itineraryId
        )
    }


    fun updateItinerary(
        itinerary: ItineraryEntity
    ) {

        viewModelScope.launch {

            itineraryDao.updateItinerary(
                itinerary
            )
        }
    }


    fun deleteItinerary(
        itinerary: ItineraryEntity
    ) {

        viewModelScope.launch {

            itineraryDao.deleteItinerary(
                itinerary
            )
        }
    }
}