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

    init {

        // Clean up any destination records which are no longer
        // referenced anywhere in the application.
        viewModelScope.launch {

            cleanupOrphanDestinations()
        }
    }

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

                cleanupOrphanDestinations()
            }
        }
    }

    // ---------------------------------------------------------
    // Destination functions
    // ---------------------------------------------------------

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

    suspend fun getDestinationIdsForTrip(
        tripId: Long
    ): List<Long> {

        return tripDestinationDao.getDestinationIdsForTrip(
            tripId
        )
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

        cleanupOrphanDestinations()
    }

    suspend fun removeAllDestinationsFromTrip(
        tripId: Long
    ) {

        tripDestinationDao.deleteDestinationsForTrip(
            tripId
        )

        cleanupOrphanDestinations()
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

                cleanupOldDestination(
                    tripId = activity.tripId,
                    destinationId = destinationId
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

                cleanupOldDestination(
                    tripId = activity.tripId,
                    destinationId = destinationId
                )
            }

            cleanupOrphanDestinations()
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

                itineraryDao.deleteItinerary(
                    existingItinerary
                )
            }

            return
        }

        if (activity.date == null) {

            if (existingItinerary != null) {

                itineraryDao.deleteItinerary(
                    existingItinerary
                )
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

            itineraryDao.updateItinerary(
                itineraryItem
            )

            syncItineraryToDestination(
                itineraryItem
            )
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

                    activityDao.updateActivity(
                        updatedActivity
                    )

                    syncActivityToDestination(
                        updatedActivity
                    )

                    syncActivityToItinerary(
                        updatedActivity
                    )
                }
            }

            for (destinationId in oldDestinationIds) {

                cleanupOldDestination(
                    tripId = itinerary.tripId,
                    destinationId = destinationId
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

                    activityDao.updateActivity(
                        activity.copy(
                            booked = false
                        )
                    )

                    activityDestinationDao
                        .deleteDestinationsForActivity(
                            activity.id
                        )
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

                cleanupOldDestination(
                    tripId = itinerary.tripId,
                    destinationId = destinationId
                )
            }

            cleanupOrphanDestinations()
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
    // Destination cleanup
    // ---------------------------------------------------------

    private suspend fun cleanupOldDestination(
        tripId: Long,
        destinationId: Long
    ) {

        val activities =
            activityDao
                .getActivitiesForTrip(
                    tripId
                )
                .first()

        for (activity in activities) {

            val ids =
                activityDestinationDao
                    .getDestinationIdsForActivity(
                        activity.id
                    )

            if (destinationId in ids) {

                return
            }
        }

        val itineraries =
            itineraryDao
                .getItineraryForTrip(
                    tripId
                )
                .first()

        for (itinerary in itineraries) {

            val ids =
                itineraryDestinationDao
                    .getDestinationIdsForItinerary(
                        itinerary.id
                    )

            if (destinationId in ids) {

                return
            }
        }

        tripDestinationDao.deleteTripDestination(
            tripId,
            destinationId
        )

        cleanupOrphanDestinations()
    }

    // ---------------------------------------------------------
    // Global orphan destination cleanup
    // ---------------------------------------------------------

    private suspend fun cleanupOrphanDestinations() {

        val destinations =
            destinationDao
                .getAllDestinations()
                .first()

        if (destinations.isEmpty()) {

            return
        }

        val trips =
            repository
                .getTrips()
                .first()

        val referencedDestinationIds =
            mutableSetOf<Long>()

        for (trip in trips) {

            referencedDestinationIds.addAll(
                tripDestinationDao
                    .getDestinationIdsForTrip(
                        trip.id
                    )
            )

            val activities =
                activityDao
                    .getActivitiesForTrip(
                        trip.id
                    )
                    .first()

            for (activity in activities) {

                referencedDestinationIds.addAll(
                    activityDestinationDao
                        .getDestinationIdsForActivity(
                            activity.id
                        )
                )
            }

            val itineraries =
                itineraryDao
                    .getItineraryForTrip(
                        trip.id
                    )
                    .first()

            for (itinerary in itineraries) {

                referencedDestinationIds.addAll(
                    itineraryDestinationDao
                        .getDestinationIdsForItinerary(
                            itinerary.id
                        )
                )
            }
        }

        for (destination in destinations) {

            if (
                destination.id !in
                referencedDestinationIds
            ) {

                destinationDao.deleteDestination(
                    destination.id
                )
            }
        }
    }
}