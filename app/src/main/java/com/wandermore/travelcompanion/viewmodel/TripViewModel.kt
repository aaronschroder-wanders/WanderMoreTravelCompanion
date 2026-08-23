package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wandermore.travelcompanion.data.repository.TripRepository
import com.wandermore.travelcompanion.database.ActivityDao
import com.wandermore.travelcompanion.database.ActivityDestinationDao
import com.wandermore.travelcompanion.database.ActivityEntity
import com.wandermore.travelcompanion.database.DestinationDao
import com.wandermore.travelcompanion.database.DestinationEntity
import com.wandermore.travelcompanion.database.ExpenseDao
import com.wandermore.travelcompanion.database.ExpenseEntity
import com.wandermore.travelcompanion.database.ItineraryDao
import com.wandermore.travelcompanion.database.ItineraryDestinationDao
import com.wandermore.travelcompanion.database.ItineraryDestinationEntity
import com.wandermore.travelcompanion.database.ItineraryEntity
import com.wandermore.travelcompanion.database.TodoDao
import com.wandermore.travelcompanion.database.TodoEntity
import com.wandermore.travelcompanion.database.TripDao
import com.wandermore.travelcompanion.database.TripDestinationDao
import com.wandermore.travelcompanion.database.TripDestinationEntity
import com.wandermore.travelcompanion.database.TripEstimateDao
import com.wandermore.travelcompanion.database.TripEstimateEntity
import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.model.TripStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TripViewModel(
    tripDao: TripDao,
    private val expenseDao: ExpenseDao,
    private val todoDao: TodoDao,
    private val activityDao: ActivityDao,
    private val itineraryDao: ItineraryDao,
    private val tripEstimateDao: TripEstimateDao,
    private val destinationDao: DestinationDao,
    private val itineraryDestinationDao: ItineraryDestinationDao,
    private val activityDestinationDao: ActivityDestinationDao,
    private val tripDestinationDao: TripDestinationDao
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

                // Capture the destinations used by this trip
                // BEFORE the trip is deleted.
                val destinationIds =
                    tripDestinationDao
                        .getDestinationIdsForTrip(
                            id
                        )
                        .distinct()

                // Room will cascade-delete the trip's child
                // relationship records.
                repository.deleteTrip(
                    trip
                )

                // Now check each destination. If nothing anywhere
                // still references it, remove the global destination.
                for (destinationId in destinationIds) {

                    cleanupOrphanedDestination(
                        destinationId
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------
    // Destination functions
    //
    // Destinations are GLOBAL and reusable across trips.
    // ---------------------------------------------------------

    fun getAllActiveDestinations(): Flow<List<DestinationEntity>> {

        return destinationDao.getActiveDestinations()
    }

    fun getAllDestinations(): Flow<List<DestinationEntity>> {

        return destinationDao.getAllDestinations()
    }

    fun getDestinationsForTrip(
        tripId: Long
    ): Flow<List<DestinationEntity>> {

        return tripDestinationDao.getDestinationsForTrip(
            tripId
        )
    }

    fun getDestinationByIdFlow(
        destinationId: Long
    ): Flow<DestinationEntity?> = flow {

        emit(
            destinationDao.getDestinationById(
                destinationId
            )
        )
    }

    suspend fun getDestinationById(
        destinationId: Long
    ): DestinationEntity? {

        return destinationDao.getDestinationById(
            destinationId
        )
    }

    suspend fun getDestinationIdsForTrip(
        tripId: Long
    ): List<Long> {

        return tripDestinationDao.getDestinationIdsForTrip(
            tripId
        )
    }

    suspend fun getDestinationByName(
        name: String
    ): DestinationEntity? {

        return destinationDao.getDestinationByName(
            name
        )
    }

    suspend fun addDestination(
        name: String
    ): Long? {

        val trimmedName =
            name.trim()

        if (trimmedName.isBlank()) {

            return null
        }

        val existing =
            destinationDao.getDestinationByName(
                trimmedName
            )

        if (existing != null) {

            return existing.id
        }

        return destinationDao.insertDestination(
            DestinationEntity(
                name = trimmedName
            )
        )
    }

    suspend fun addDestinationToTrip(
        tripId: Long,
        destinationId: Long
    ) {

        tripDestinationDao.insertTripDestination(
            TripDestinationEntity(
                tripId = tripId,
                destinationId = destinationId
            )
        )
    }

    suspend fun removeDestinationFromTrip(
        tripId: Long,
        destinationId: Long
    ) {

        tripDestinationDao.deleteTripDestination(
            tripId,
            destinationId
        )

        cleanupOrphanedDestination(
            destinationId
        )
    }

    suspend fun removeAllDestinationsFromTrip(
        tripId: Long
    ) {

        val destinationIds =
            tripDestinationDao
                .getDestinationIdsForTrip(
                    tripId
                )
                .distinct()

        tripDestinationDao.deleteDestinationsForTrip(
            tripId
        )

        for (destinationId in destinationIds) {

            cleanupOrphanedDestination(
                destinationId
            )
        }
    }

    fun setDestinationActive(
        destinationId: Long,
        active: Boolean
    ) {

        viewModelScope.launch {

            destinationDao.setDestinationActive(
                destinationId,
                active
            )
        }
    }

    fun updateDestination(
        destination: DestinationEntity
    ) {

        viewModelScope.launch {

            destinationDao.updateDestination(
                destination
            )
        }
    }

    suspend fun getActivitiesForDestination(
        tripId: Long,
        destinationId: Long
    ): List<ActivityEntity> {

        val activities =
            activityDao
                .getActivitiesForTrip(
                    tripId
                )
                .first()

        return activities.filter { activity ->

            destinationId in
                    activityDestinationDao
                        .getDestinationIdsForActivity(
                            activity.id
                        )
        }
    }

    suspend fun getItineraryForDestination(
        tripId: Long,
        destinationId: Long
    ): List<ItineraryEntity> {

        val itineraryItems =
            itineraryDao
                .getItineraryForTrip(
                    tripId
                )
                .first()

        return itineraryItems.filter { itinerary ->

            destinationId in
                    itineraryDestinationDao
                        .getDestinationIdsForItinerary(
                            itinerary.id
                        )
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
                todo.copy(
                    dueDate = if (todo.completed) {
                        null
                    } else {
                        todo.dueDate
                    }
                )
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

            val savedActivity =
                activity.copy(
                    id = activityId
                )

            syncActivityToDestination(
                savedActivity
            )

            if (savedActivity.booked) {

                syncActivityToItinerary(
                    savedActivity
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

            val oldDestinationIds =
                activityDestinationDao
                    .getDestinationIdsForActivity(
                        activity.id
                    )

            activityDao.updateActivity(
                activity
            )

            syncActivityToDestination(
                activity
            )

            syncActivityToItinerary(
                activity
            )

            for (destinationId in oldDestinationIds) {

                cleanupOrphanedDestination(
                    destinationId
                )
            }
        }
    }

    fun deleteActivity(
        activity: ActivityEntity
    ) {

        viewModelScope.launch {

            val oldDestinationIds =
                activityDestinationDao
                    .getDestinationIdsForActivity(
                        activity.id
                    )

            itineraryDao.deleteItineraryByActivityId(
                activity.id
            )

            activityDestinationDao
                .deleteDestinationsForActivity(
                    activity.id
                )

            activityDao.deleteActivity(
                activity
            )

            for (destinationId in oldDestinationIds) {

                cleanupOrphanedDestination(
                    destinationId
                )
            }
        }
    }

    // ---------------------------------------------------------
    // Activity → Destination synchronisation
    // ---------------------------------------------------------

    private suspend fun syncActivityToDestination(
        activity: ActivityEntity
    ) {

        activityDestinationDao
            .deleteDestinationsForActivity(
                activity.id
            )

        val location =
            activity.location
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }

        if (location == null) {

            return
        }

        val existingDestination =
            destinationDao.getDestinationByName(
                location
            )

        val destinationId =
            existingDestination?.id
                ?: destinationDao.insertDestination(
                    DestinationEntity(
                        name = location
                    )
                )

        activityDestinationDao
            .insertActivityDestination(
                com.wandermore.travelcompanion.database
                    .ActivityDestinationEntity(
                        activityId = activity.id,
                        destinationId = destinationId
                    )
            )

        tripDestinationDao.insertTripDestination(
            TripDestinationEntity(
                tripId = activity.tripId,
                destinationId = destinationId
            )
        )
    }

    // ---------------------------------------------------------
    // Activity → Itinerary synchronisation
    // ---------------------------------------------------------

    private suspend fun syncActivityToItinerary(
        activity: ActivityEntity
    ) {

        val existingItinerary =
            itineraryDao.getItineraryByActivityId(
                activity.id
            )

        if (!activity.booked) {

            if (existingItinerary != null) {

                val oldDestinationIds =
                    itineraryDestinationDao
                        .getDestinationIdsForItinerary(
                            existingItinerary.id
                        )

                itineraryDestinationDao
                    .deleteDestinationsForItinerary(
                        existingItinerary.id
                    )

                itineraryDao.deleteItinerary(
                    existingItinerary
                )

                for (destinationId in oldDestinationIds) {

                    cleanupOrphanedDestination(
                        destinationId
                    )
                }
            }

            return
        }

        if (activity.date == null) {

            if (existingItinerary != null) {

                val oldDestinationIds =
                    itineraryDestinationDao
                        .getDestinationIdsForItinerary(
                            existingItinerary.id
                        )

                itineraryDestinationDao
                    .deleteDestinationsForItinerary(
                        existingItinerary.id
                    )

                itineraryDao.deleteItinerary(
                    existingItinerary
                )

                for (destinationId in oldDestinationIds) {

                    cleanupOrphanedDestination(
                        destinationId
                    )
                }
            }

            return
        }

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

            val itineraryId =
                itineraryDao.insertItinerary(
                    itineraryItem
                )

            syncItineraryToDestination(
                itineraryItem.copy(
                    id = itineraryId
                )
            )

        } else {

            val oldDestinationIds =
                itineraryDestinationDao
                    .getDestinationIdsForItinerary(
                        existingItinerary.id
                    )

            itineraryDao.updateItinerary(
                itineraryItem
            )

            syncItineraryToDestination(
                itineraryItem
            )

            for (destinationId in oldDestinationIds) {

                cleanupOrphanedDestination(
                    destinationId
                )
            }
        }
    }

    // ---------------------------------------------------------
    // Trip Estimate functions
    // ---------------------------------------------------------

    fun addTripEstimate(
        estimate: TripEstimateEntity,
        onAdded: () -> Unit,
        onDuplicate: () -> Unit
    ) {

        viewModelScope.launch {

            val existingEstimate =
                tripEstimateDao.getEstimate(
                    estimate.tripId,
                    estimate.category
                )

            if (existingEstimate != null) {

                onDuplicate()

                return@launch
            }

            tripEstimateDao.insertEstimate(
                estimate
            )

            onAdded()
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

            val itineraryId =
                itineraryDao.insertItinerary(
                    itinerary
                )

            val savedItinerary =
                itinerary.copy(
                    id = itineraryId
                )

            syncItineraryToDestination(
                savedItinerary
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

            val oldDestinationIds =
                itineraryDestinationDao
                    .getDestinationIdsForItinerary(
                        itinerary.id
                    )

            itineraryDao.updateItinerary(
                itinerary
            )

            syncItineraryToDestination(
                itinerary
            )

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
                            name = itinerary.title,
                            type = itinerary.type,
                            date = itinerary.date,
                            startTime = itinerary.time,
                            location = itinerary.location,
                            notes = itinerary.notes,
                            booked = itinerary.booked
                        )

                    val oldActivityDestinationIds =
                        activityDestinationDao
                            .getDestinationIdsForActivity(
                                updatedActivity.id
                            )

                    activityDao.updateActivity(
                        updatedActivity
                    )

                    syncActivityToDestination(
                        updatedActivity
                    )

                    syncActivityToItinerary(
                        updatedActivity
                    )

                    for (destinationId in oldActivityDestinationIds) {

                        cleanupOrphanedDestination(
                            destinationId
                        )
                    }
                }
            }

            for (destinationId in oldDestinationIds) {

                cleanupOrphanedDestination(
                    destinationId
                )
            }
        }
    }

    fun deleteItinerary(
        itinerary: ItineraryEntity
    ) {

        viewModelScope.launch {

            val oldDestinationIds =
                itineraryDestinationDao
                    .getDestinationIdsForItinerary(
                        itinerary.id
                    )

            val activityId =
                itinerary.activityId

            if (activityId != null) {

                val activity =
                    activityDao.getActivityById(
                        activityId
                    )

                if (activity != null) {

                    val activityDestinationIds =
                        activityDestinationDao
                            .getDestinationIdsForActivity(
                                activity.id
                            )

                    activityDao.updateActivity(
                        activity.copy(
                            booked = false
                        )
                    )

                    activityDestinationDao
                        .deleteDestinationsForActivity(
                            activity.id
                        )

                    for (destinationId in activityDestinationIds) {

                        cleanupOrphanedDestination(
                            destinationId
                        )
                    }
                }
            }

            itineraryDestinationDao
                .deleteDestinationsForItinerary(
                    itinerary.id
                )

            itineraryDao.deleteItinerary(
                itinerary
            )

            for (destinationId in oldDestinationIds) {

                cleanupOrphanedDestination(
                    destinationId
                )
            }
        }
    }

    // ---------------------------------------------------------
    // Itinerary → Destination synchronisation
    // ---------------------------------------------------------

    private suspend fun syncItineraryToDestination(
        itinerary: ItineraryEntity
    ) {

        itineraryDestinationDao
            .deleteDestinationsForItinerary(
                itinerary.id
            )

        val location =
            itinerary.location
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }

        if (location == null) {

            return
        }

        val existingDestination =
            destinationDao.getDestinationByName(
                location
            )

        val destinationId =
            existingDestination?.id
                ?: destinationDao.insertDestination(
                    DestinationEntity(
                        name = location
                    )
                )

        itineraryDestinationDao
            .insertItineraryDestination(
                ItineraryDestinationEntity(
                    itineraryId = itinerary.id,
                    destinationId = destinationId
                )
            )

        tripDestinationDao.insertTripDestination(
            TripDestinationEntity(
                tripId = itinerary.tripId,
                destinationId = destinationId
            )
        )
    }

    // ---------------------------------------------------------
    // GLOBAL ORPHAN DESTINATION CLEANUP
    //
    // A destination is deleted only when it has NO references
    // remaining in:
    //
    // 1. activity_destinations
    // 2. itinerary_destinations
    // 3. trip_destinations
    //
    // This means destinations remain safely reusable while
    // anything in the database still references them.
    // ---------------------------------------------------------

    private suspend fun cleanupOrphanedDestination(
        destinationId: Long
    ) {

        val activityCount =
            activityDestinationDao
                .countActivitiesForDestination(
                    destinationId
                )

        if (activityCount > 0) {
            return
        }

        val itineraryCount =
            itineraryDestinationDao
                .countItinerariesForDestination(
                    destinationId
                )

        if (itineraryCount > 0) {
            return
        }

        val tripIds =
            tripDestinationDao
                .getTripIdsForDestination(
                    destinationId
                )

        if (tripIds.isNotEmpty()) {
            return
        }

        destinationDao.deleteDestination(
            destinationId
        )
    }
}