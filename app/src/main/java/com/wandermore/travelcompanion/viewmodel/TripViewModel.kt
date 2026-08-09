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
import com.wandermore.travelcompanion.database.TripEstimateDao
import com.wandermore.travelcompanion.database.TripEstimateEntity
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
    private val itineraryDao: ItineraryDao,
    private val tripEstimateDao: TripEstimateDao
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

            val activityId =
                activityDao.insertActivity(
                    activity
                )

            if (activity.booked) {

                syncActivityToItinerary(
                    activity.copy(
                        id = activityId
                    )
                )
            }
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

            syncActivityToItinerary(
                activity
            )
        }
    }

    fun deleteActivity(
        activity: ActivityEntity
    ) {

        viewModelScope.launch {

            itineraryDao.deleteItineraryByActivityId(
                activity.id
            )

            activityDao.deleteActivity(
                activity
            )
        }
    }

    // ---------------------------------------------------------
    // ACTIVITY → ITINERARY SYNCHRONISATION
    // ---------------------------------------------------------

    private suspend fun syncActivityToItinerary(
        activity: ActivityEntity
    ) {

        val existingItinerary =
            itineraryDao.getItineraryByActivityId(
                activity.id
            )

        // -----------------------------------------------------
        // ACTIVITY IS NOT BOOKED
        // -----------------------------------------------------

        if (!activity.booked) {

            if (existingItinerary != null) {

                itineraryDao.deleteItinerary(
                    existingItinerary
                )
            }

            return
        }

        // -----------------------------------------------------
        // ACTIVITY IS BOOKED BUT HAS NO DATE
        // -----------------------------------------------------

        if (activity.date == null) {

            if (existingItinerary != null) {

                itineraryDao.deleteItinerary(
                    existingItinerary
                )
            }

            return
        }

        // -----------------------------------------------------
        // CREATE / UPDATE ITINERARY ITEM
        // -----------------------------------------------------

        val itineraryItem =
            if (existingItinerary == null) {

                ItineraryEntity(
                    tripId = activity.tripId,
                    date = activity.date,
                    time = activity.startTime,
                    title = activity.name,
                    type = activity.type,
                    location = activity.location,
                    notes = activity.notes,
                    activityId = activity.id,
                    booked = true
                )

            } else {

                existingItinerary.copy(
                    tripId = activity.tripId,
                    date = activity.date,
                    time = activity.startTime,
                    title = activity.name,
                    type = activity.type,
                    location = activity.location,
                    notes = activity.notes,
                    activityId = activity.id,
                    booked = true
                )
            }

        if (existingItinerary == null) {

            itineraryDao.insertItinerary(
                itineraryItem
            )

        } else {

            itineraryDao.updateItinerary(
                itineraryItem
            )
        }
    }

    // ---------------------------------------------------------
    // Trip Estimate functions
    // ---------------------------------------------------------

    fun addTripEstimate(
        estimate: TripEstimateEntity
    ) {

        viewModelScope.launch {

            tripEstimateDao.insertEstimate(
                estimate
            )
        }
    }


    fun getTripEstimatesForTrip(
        tripId: Long
    ): Flow<List<TripEstimateEntity>> {

        return tripEstimateDao.getEstimatesForTrip(
            tripId
        )
    }


    suspend fun getTripEstimate(
        tripId: Long,
        category: String
    ): TripEstimateEntity? {

        return tripEstimateDao.getEstimate(
            tripId,
            category
        )
    }

    suspend fun getTripEstimateById(
        estimateId: Long
    ): TripEstimateEntity? {

        return tripEstimateDao.getEstimateById(
            estimateId
        )
    }

    fun updateTripEstimate(
        estimate: TripEstimateEntity
    ) {

        viewModelScope.launch {

            tripEstimateDao.updateEstimate(
                estimate
            )
        }
    }


    fun deleteTripEstimate(
        estimate: TripEstimateEntity
    ) {

        viewModelScope.launch {

            tripEstimateDao.deleteEstimate(
                estimate
            )
        }
    }


    fun deleteTripEstimatesForTrip(
        tripId: Long
    ) {

        viewModelScope.launch {

            tripEstimateDao.deleteEstimatesForTrip(
                tripId
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

            // -------------------------------------------------
            // SAVE THE ITINERARY ITEM
            // -------------------------------------------------

            itineraryDao.updateItinerary(
                itinerary
            )

            // -------------------------------------------------
            // SYNC BACK TO LINKED ACTIVITY
            //
            // Only itinerary items created from an Activity
            // have an activityId.
            // -------------------------------------------------

            val activityId =
                itinerary.activityId

            if (activityId != null) {

                val existingActivity =
                    activityDao.getActivityById(
                        activityId
                    )

                if (existingActivity != null) {

                    val updatedActivity =
                        existingActivity.copy(

                            // Itinerary title → Activity name
                            name =
                                itinerary.title,

                            // Keep the activity type
                            // in step with the itinerary.
                            type =
                                itinerary.type,

                            // Itinerary date → Activity date
                            date =
                                itinerary.date,

                            // Itinerary time → Activity
                            // start time.
                            startTime =
                                itinerary.time,

                            // Itinerary location →
                            // Activity location.
                            location =
                                itinerary.location,

                            // Itinerary notes →
                            // Activity notes.
                            notes =
                                itinerary.notes,

                            // Itinerary booked →
                            // Activity booked.
                            booked =
                                itinerary.booked
                        )

                    activityDao.updateActivity(
                        updatedActivity
                    )

                    // -------------------------------------------------
                    // IMPORTANT
                    //
                    // If the Activity has just been marked as
                    // unbooked, the normal Activity → Itinerary
                    // synchronisation will remove this itinerary
                    // item. That is intentional.
                    // -------------------------------------------------

                    syncActivityToItinerary(
                        updatedActivity
                    )
                }
            }
        }
    }

    fun deleteItinerary(
        itinerary: ItineraryEntity
    ) {

        viewModelScope.launch {

            // -------------------------------------------------
            // If this itinerary item came from an Activity,
            // deleting it should also remove the Activity's
            // booked itinerary relationship.
            //
            // We do NOT delete the Activity itself.
            // -------------------------------------------------

            val activityId =
                itinerary.activityId

            if (activityId != null) {

                val activity =
                    activityDao.getActivityById(
                        activityId
                    )

                if (activity != null) {

                    activityDao.updateActivity(
                        activity.copy(
                            booked = false
                        )
                    )
                }
            }

            itineraryDao.deleteItinerary(
                itinerary
            )
        }
    }
}