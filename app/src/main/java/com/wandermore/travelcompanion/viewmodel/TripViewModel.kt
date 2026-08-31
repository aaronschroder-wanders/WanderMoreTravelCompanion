package com.wandermore.travelcompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wandermore.travelcompanion.data.repository.TripRepository
import com.wandermore.travelcompanion.database.ActivityDao
import com.wandermore.travelcompanion.database.ActivityDestinationDao
import com.wandermore.travelcompanion.database.ActivityDestinationEntity
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

    private val repository = TripRepository(tripDao)

    // =========================================================
    // TRIP FUNCTIONS
    // =========================================================

    fun getTrips(): Flow<List<Trip>> {
        return repository.getTrips()
    }

    fun getTripByIdFlow(id: Long): Flow<Trip?> {
        return repository.getTripByIdFlow(id)
    }

    suspend fun getTripById(id: Long): Trip? {
        return repository.getTripById(id)
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

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            repository.updateTrip(trip)
        }
    }

    // =========================================================
    // TRIP STATUS FUNCTIONS
    // =========================================================

    fun startTrip(tripId: Long) {
        viewModelScope.launch {
            repository.updateTripStatus(
                tripId,
                TripStatus.CURRENT
            )
        }
    }

    fun archiveTrip(tripId: Long) {
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

    fun deleteTrip(id: Long) {

        viewModelScope.launch {

            val destinationIds =
                tripDestinationDao
                    .getDestinationIdsForTrip(id)
                    .distinct()

            val trip =
                repository.getTripById(id)

            if (trip != null) {

                repository.deleteTrip(trip)

                for (destinationId in destinationIds) {
                    cleanupOrphanedDestination(destinationId)
                }
            }
        }
    }

    // =========================================================
    // DESTINATION FUNCTIONS
    // =========================================================

    fun getAllActiveDestinations(): Flow<List<DestinationEntity>> {
        return destinationDao.getActiveDestinations()
    }

    fun getAllDestinations(): Flow<List<DestinationEntity>> {
        return destinationDao.getAllDestinations()
    }

    fun getDestinationsForTrip(
        tripId: Long
    ): Flow<List<DestinationEntity>> {
        return tripDestinationDao.getDestinationsForTrip(tripId)
    }

    fun getDestinationByIdFlow(
        destinationId: Long
    ): Flow<DestinationEntity?> = flow {

        emit(
            destinationDao.getDestinationById(destinationId)
        )
    }

    suspend fun getDestinationById(
        destinationId: Long
    ): DestinationEntity? {
        return destinationDao.getDestinationById(destinationId)
    }

    suspend fun getDestinationIdsForTrip(
        tripId: Long
    ): List<Long> {
        return tripDestinationDao.getDestinationIdsForTrip(tripId)
    }

    suspend fun getDestinationByName(
        name: String
    ): DestinationEntity? {
        return destinationDao.getDestinationByName(name)
    }

    // =========================================================
    // ADD GLOBAL DESTINATION
    // =========================================================

    fun addDestination(
        name: String,
        onResult: (Boolean) -> Unit = {}
    ) {

        viewModelScope.launch {

            val trimmedName =
                name.trim()

            if (trimmedName.isBlank()) {

                onResult(false)

                return@launch
            }

            val existing =
                destinationDao.getDestinationByName(
                    trimmedName
                )

            if (existing != null) {

                onResult(false)

                return@launch
            }

            destinationDao.insertDestination(
                DestinationEntity(
                    name = trimmedName
                )
            )

            onResult(true)
        }
    }

    // =========================================================
    // ADD GLOBAL DESTINATION AND RETURN ITS ID
    // =========================================================

    fun addDestinationAndReturnId(
        name: String,
        onResult: (Long?) -> Unit
    ) {

        viewModelScope.launch {

            val trimmedName =
                name.trim()

            if (trimmedName.isBlank()) {

                onResult(null)

                return@launch
            }

            val existing =
                destinationDao.getDestinationByName(
                    trimmedName
                )

            if (existing != null) {

                onResult(existing.id)

                return@launch
            }

            val destinationId =
                destinationDao.insertDestination(
                    DestinationEntity(
                        name = trimmedName
                    )
                )

            onResult(destinationId)
        }
    }

    // =========================================================
    // ADD DESTINATION TO TRIP
    // =========================================================

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

    // =========================================================
    // REMOVE DESTINATION FROM TRIP
    // =========================================================

    suspend fun removeDestinationFromTrip(
        tripId: Long,
        destinationId: Long
    ) {

        tripDestinationDao.deleteTripDestination(
            tripId,
            destinationId
        )

        cleanupOrphanedDestination(destinationId)
    }

    // =========================================================
    // REMOVE ALL DESTINATIONS FROM TRIP
    // =========================================================

    suspend fun removeAllDestinationsFromTrip(
        tripId: Long
    ) {

        val destinationIds =
            tripDestinationDao
                .getDestinationIdsForTrip(tripId)
                .distinct()

        tripDestinationDao.deleteDestinationsForTrip(
            tripId
        )

        for (destinationId in destinationIds) {
            cleanupOrphanedDestination(destinationId)
        }
    }

    // =========================================================
    // ARCHIVE / UNARCHIVE DESTINATION
    // =========================================================

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

    // =========================================================
    // RENAME DESTINATION
    // =========================================================

    fun updateDestination(
        destination: DestinationEntity
    ) {

        viewModelScope.launch {

            destinationDao.updateDestination(
                destination
            )
        }
    }

    // =========================================================
    // DELETE DESTINATION FROM SETTINGS
    // =========================================================

    fun deleteDestinationFromSettings(
        destinationId: Long,
        onDeleted: () -> Unit,
        onBlocked: (String) -> Unit
    ) {

        viewModelScope.launch {

            cleanupStaleTripReferences(
                destinationId
            )

            val activityCount =
                activityDestinationDao
                    .countActivitiesForDestination(
                        destinationId
                    )

            val itineraryCount =
                itineraryDestinationDao
                    .countItinerariesForDestination(
                        destinationId
                    )

            val tripIds =
                tripDestinationDao
                    .getTripIdsForDestination(
                        destinationId
                    )

            if (activityCount > 0) {

                onBlocked(
                    "This destination cannot be deleted because it is still being used by " +
                            activityCount +
                            if (activityCount == 1) {
                                " activity."
                            } else {
                                " activities."
                            }
                )

                return@launch
            }

            if (itineraryCount > 0) {

                onBlocked(
                    "This destination cannot be deleted because it is still being used by " +
                            itineraryCount +
                            if (itineraryCount == 1) {
                                " itinerary item."
                            } else {
                                " itinerary items."
                            }
                )

                return@launch
            }

            if (tripIds.isNotEmpty()) {

                onBlocked(
                    "This destination cannot be deleted because it is still associated with " +
                            tripIds.size +
                            if (tripIds.size == 1) {
                                " trip."
                            } else {
                                " trips."
                            }
                )

                return@launch
            }

            destinationDao.deleteDestination(
                destinationId
            )

            onDeleted()
        }
    }

    // =========================================================
    // ACTIVITY DESTINATIONS
    // =========================================================

    fun getDestinationIdsForActivityFlow(
        activityId: Long
    ): Flow<List<Long>> = flow {

        emit(
            activityDestinationDao
                .getDestinationIdsForActivity(
                    activityId
                )
        )
    }

    suspend fun getDestinationIdsForActivity(
        activityId: Long
    ): List<Long> {
        return activityDestinationDao
            .getDestinationIdsForActivity(
                activityId
            )
    }

    // =========================================================
    // ITINERARY DESTINATIONS
    // =========================================================

    fun getDestinationIdsForItineraryFlow(
        itineraryId: Long
    ): Flow<List<Long>> = flow {

        emit(
            itineraryDestinationDao
                .getDestinationIdsForItinerary(
                    itineraryId
                )
        )
    }

    suspend fun getDestinationIdsForItinerary(
        itineraryId: Long
    ): List<Long> {
        return itineraryDestinationDao
            .getDestinationIdsForItinerary(
                itineraryId
            )
    }

    // =========================================================
    // ACTIVITIES FOR DESTINATION
    // =========================================================

    suspend fun getActivitiesForDestination(
        tripId: Long,
        destinationId: Long
    ): List<ActivityEntity> {

        return activityDestinationDao
            .getActivitiesForDestination(
                tripId,
                destinationId
            )
    }

    // =========================================================
    // ITINERARY FOR DESTINATION
    // =========================================================

    suspend fun getItineraryForDestination(
        tripId: Long,
        destinationId: Long
    ): List<ItineraryEntity> {

        return itineraryDestinationDao
            .getItineraryForDestination(
                tripId,
                destinationId
            )
    }

    // =========================================================
    // EXPENSE FUNCTIONS
    // =========================================================

    fun addExpense(expense: ExpenseEntity) {

        viewModelScope.launch {
            expenseDao.insertExpense(expense)
        }
    }

    fun getExpensesForTrip(
        tripId: Long
    ): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesForTrip(tripId)
    }

    suspend fun getExpenseById(
        expenseId: Long
    ): ExpenseEntity? {
        return expenseDao.getExpenseById(expenseId)
    }

    fun deleteExpense(expense: ExpenseEntity) {

        viewModelScope.launch {
            expenseDao.deleteExpense(expense)
        }
    }

    fun updateExpense(expense: ExpenseEntity) {

        viewModelScope.launch {
            expenseDao.updateExpense(expense)
        }
    }

    // =========================================================
    // TO DO FUNCTIONS
    // =========================================================

    fun addTodo(todo: TodoEntity) {

        viewModelScope.launch {
            todoDao.insertTodo(todo)
        }
    }

    fun getTodosForTrip(
        tripId: Long
    ): Flow<List<TodoEntity>> {
        return todoDao.getTodosForTrip(tripId)
    }

    // =========================================================
    // INCOMPLETE TO DOS FOR SPECIFIC DATE
    // =========================================================

    fun getTodosForDate(
        tripId: Long,
        date: LocalDate
    ): Flow<List<TodoEntity>> {

        return todoDao.getTodosForDate(
            tripId,
            date
        )
    }

    suspend fun getTodoById(
        todoId: Long
    ): TodoEntity? {
        return todoDao.getTodoById(todoId)
    }

    fun updateTodo(todo: TodoEntity) {

        viewModelScope.launch {

            todoDao.updateTodo(
                todo.copy(
                    dueDate =
                        if (todo.completed) {
                            null
                        } else {
                            todo.dueDate
                        }
                )
            )
        }
    }

    fun deleteTodo(todo: TodoEntity) {

        viewModelScope.launch {
            todoDao.deleteTodo(todo)
        }
    }

    // =========================================================
    // ACTIVITY FUNCTIONS
    // =========================================================

    /*
     * destinationIds:
     *
     * null  = use the Activity's Location as the legacy
     *         automatic destination.
     *
     * non-null = use the destinations explicitly selected by
     *            DestinationSelector.
     *
     * This distinction is important because an empty Set means
     * the user deliberately selected no destinations.
     */

    fun addActivity(
        activity: ActivityEntity,
        destinationIds: Set<Long>? = null
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

            if (destinationIds == null) {

                syncActivityToDestination(
                    savedActivity
                )

            } else {

                syncActivityToExplicitDestinations(
                    savedActivity,
                    destinationIds
                )
            }

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
        return activityDao.getActivitiesForTrip(tripId)
    }

    suspend fun getActivityById(
        activityId: Long
    ): ActivityEntity? {
        return activityDao.getActivityById(activityId)
    }

    fun updateActivity(
        activity: ActivityEntity,
        destinationIds: Set<Long>? = null
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

            if (destinationIds == null) {

                syncActivityToDestination(
                    activity
                )

            } else {

                syncActivityToExplicitDestinations(
                    activity,
                    destinationIds
                )
            }

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

    fun deleteActivity(activity: ActivityEntity) {

        viewModelScope.launch {

            val oldActivityDestinationIds =
                activityDestinationDao
                    .getDestinationIdsForActivity(
                        activity.id
                    )

            val existingItinerary =
                itineraryDao.getItineraryByActivityId(
                    activity.id
                )

            val oldItineraryDestinationIds =
                if (existingItinerary != null) {

                    itineraryDestinationDao
                        .getDestinationIdsForItinerary(
                            existingItinerary.id
                        )

                } else {

                    emptyList()
                }

            if (existingItinerary != null) {

                itineraryDestinationDao
                    .deleteDestinationsForItinerary(
                        existingItinerary.id
                    )

                itineraryDao.deleteItinerary(
                    existingItinerary
                )
            }

            activityDestinationDao
                .deleteDestinationsForActivity(
                    activity.id
                )

            activityDao.deleteActivity(
                activity
            )

            for (
            destinationId in
            (
                    oldActivityDestinationIds +
                            oldItineraryDestinationIds
                    ).distinct()
            ) {
                cleanupOrphanedDestination(
                    destinationId
                )
            }
        }
    }

    // =========================================================
    // ACTIVITY → EXPLICIT DESTINATIONS
    // =========================================================

    private suspend fun syncActivityToExplicitDestinations(
        activity: ActivityEntity,
        destinationIds: Set<Long>
    ) {

        val oldDestinationIds =
            activityDestinationDao
                .getDestinationIdsForActivity(
                    activity.id
                )

        activityDestinationDao
            .deleteDestinationsForActivity(
                activity.id
            )

        for (destinationId in destinationIds.distinct()) {

            val destination =
                destinationDao.getDestinationById(
                    destinationId
                )

            if (destination != null) {

                activityDestinationDao
                    .insertActivityDestination(
                        ActivityDestinationEntity(
                            activityId =
                                activity.id,
                            destinationId =
                                destinationId
                        )
                    )

                tripDestinationDao
                    .insertTripDestination(
                        TripDestinationEntity(
                            tripId =
                                activity.tripId,
                            destinationId =
                                destinationId
                        )
                    )
            }
        }

        for (oldDestinationId in oldDestinationIds) {

            if (
                oldDestinationId !in
                destinationIds
            ) {
                cleanupOrphanedDestination(
                    oldDestinationId
                )
            }
        }
    }

    // =========================================================
    // ACTIVITY → DESTINATION
    //
    // LEGACY LOCATION FALLBACK
    // =========================================================

    private suspend fun syncActivityToDestination(
        activity: ActivityEntity
    ) {

        val oldDestinationIds =
            activityDestinationDao
                .getDestinationIdsForActivity(
                    activity.id
                )

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

            for (destinationId in oldDestinationIds) {

                cleanupOrphanedDestination(
                    destinationId
                )
            }

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
                ActivityDestinationEntity(
                    activityId =
                        activity.id,
                    destinationId =
                        destinationId
                )
            )

        tripDestinationDao
            .insertTripDestination(
                TripDestinationEntity(
                    tripId =
                        activity.tripId,
                    destinationId =
                        destinationId
                )
            )

        for (oldDestinationId in oldDestinationIds) {

            if (
                oldDestinationId !=
                destinationId
            ) {
                cleanupOrphanedDestination(
                    oldDestinationId
                )
            }
        }
    }

    // =========================================================
    // ACTIVITY → ITINERARY
    //
    // IMPORTANT:
    //
    // When an Activity has explicit destinations, its linked
    // itinerary item receives those same destinations.
    //
    // We therefore do NOT derive the itinerary destination
    // from Activity.location here.
    // =========================================================

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
                    tripId =
                        activity.tripId,
                    date =
                        activity.date,
                    time =
                        activity.startTime,
                    title =
                        activity.name,
                    type =
                        activity.type,
                    location =
                        activity.location,
                    notes =
                        activity.notes,
                    activityId =
                        activity.id,
                    booked =
                        true
                )

            } else {

                existingItinerary.copy(
                    tripId =
                        activity.tripId,
                    date =
                        activity.date,
                    time =
                        activity.startTime,
                    title =
                        activity.name,
                    type =
                        activity.type,
                    location =
                        activity.location,
                    notes =
                        activity.notes,
                    activityId =
                        activity.id,
                    booked =
                        true
                )
            }

        if (existingItinerary == null) {

            val itineraryId =
                itineraryDao.insertItinerary(
                    itineraryItem
                )

            val savedItinerary =
                itineraryItem.copy(
                    id = itineraryId
                )

            syncItineraryToActivityDestinations(
                savedItinerary,
                activity.id
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

            syncItineraryToActivityDestinations(
                itineraryItem,
                activity.id
            )

            for (destinationId in oldDestinationIds) {

                cleanupOrphanedDestination(
                    destinationId
                )
            }
        }
    }

    // =========================================================
    // ACTIVITY DESTINATIONS → ITINERARY DESTINATIONS
    // =========================================================

    private suspend fun syncItineraryToActivityDestinations(
        itinerary: ItineraryEntity,
        activityId: Long
    ) {

        val oldDestinationIds =
            itineraryDestinationDao
                .getDestinationIdsForItinerary(
                    itinerary.id
                )

        val activityDestinationIds =
            activityDestinationDao
                .getDestinationIdsForActivity(
                    activityId
                )

        itineraryDestinationDao
            .deleteDestinationsForItinerary(
                itinerary.id
            )

        for (destinationId in activityDestinationIds.distinct()) {

            val destination =
                destinationDao.getDestinationById(
                    destinationId
                )

            if (destination != null) {

                itineraryDestinationDao
                    .insertItineraryDestination(
                        ItineraryDestinationEntity(
                            itineraryId =
                                itinerary.id,
                            destinationId =
                                destinationId
                        )
                    )

                tripDestinationDao
                    .insertTripDestination(
                        TripDestinationEntity(
                            tripId =
                                itinerary.tripId,
                            destinationId =
                                destinationId
                        )
                    )
            }
        }

        for (oldDestinationId in oldDestinationIds) {

            if (
                oldDestinationId !in
                activityDestinationIds
            ) {

                cleanupOrphanedDestination(
                    oldDestinationId
                )
            }
        }
    }

    // =========================================================
    // TRIP ESTIMATE FUNCTIONS
    // =========================================================

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

    // =========================================================
    // ITINERARY FUNCTIONS
    // =========================================================

    fun addItinerary(
        itinerary: ItineraryEntity,
        destinationIds: Set<Long> = emptySet()
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

            syncItineraryToExplicitDestinations(
                savedItinerary,
                destinationIds
            )
        }
    }

    // =========================================================
// ITINERARY → EXPLICIT DESTINATIONS
// =========================================================
//
// An empty Set is intentional.
// It means the user has selected no destinations.
//
// This is separate from the legacy Location fallback below,
// which is retained only for older/internal workflows.
//

    private suspend fun syncItineraryToExplicitDestinations(
        itinerary: ItineraryEntity,
        destinationIds: Set<Long>
    ) {

        val oldDestinationIds =
            itineraryDestinationDao
                .getDestinationIdsForItinerary(
                    itinerary.id
                )

        itineraryDestinationDao
            .deleteDestinationsForItinerary(
                itinerary.id
            )

        for (destinationId in destinationIds.distinct()) {

            val destination =
                destinationDao.getDestinationById(
                    destinationId
                )

            if (destination != null) {

                itineraryDestinationDao
                    .insertItineraryDestination(
                        ItineraryDestinationEntity(
                            itineraryId =
                                itinerary.id,
                            destinationId =
                                destinationId
                        )
                    )

                tripDestinationDao
                    .insertTripDestination(
                        TripDestinationEntity(
                            tripId =
                                itinerary.tripId,
                            destinationId =
                                destinationId
                        )
                    )
            }
        }

        for (oldDestinationId in oldDestinationIds) {

            if (
                oldDestinationId !in
                destinationIds
            ) {

                cleanupOrphanedDestination(
                    oldDestinationId
                )
            }
        }
    }

    fun getItineraryForTrip(
        tripId: Long
    ): Flow<List<ItineraryEntity>> {
        return itineraryDao.getItineraryForTrip(
            tripId
        )
    }

    // =========================================================
    // ITINERARY FOR SPECIFIC DATE
    // =========================================================

    fun getItineraryForDate(
        tripId: Long,
        date: LocalDate
    ): Flow<List<ItineraryEntity>> {

        return itineraryDao.getItineraryForDate(
            tripId,
            date
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
        itinerary: ItineraryEntity,
        destinationIds: Set<Long>
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

            // =====================================================
            // ITINERARY DESTINATIONS
            //
            // destinationIds is always explicit here.
            //
            // An empty Set means the user deliberately selected
            // no destinations, so we must remove all existing
            // itinerary destination links.
            // =====================================================

            itineraryDestinationDao
                .deleteDestinationsForItinerary(
                    itinerary.id
                )

            for (destinationId in destinationIds.distinct()) {

                val destination =
                    destinationDao.getDestinationById(
                        destinationId
                    )

                if (destination != null) {

                    itineraryDestinationDao
                        .insertItineraryDestination(
                            ItineraryDestinationEntity(
                                itineraryId =
                                    itinerary.id,
                                destinationId =
                                    destinationId
                            )
                        )

                    tripDestinationDao
                        .insertTripDestination(
                            TripDestinationEntity(
                                tripId =
                                    itinerary.tripId,
                                destinationId =
                                    destinationId
                            )
                        )
                }
            }

            // =====================================================
            // LINKED ACTIVITY
            //
            // If this itinerary item was created from an Activity,
            // keep the Activity's normal fields synchronised.
            //
            // IMPORTANT:
            // We do NOT overwrite the itinerary's explicitly
            // selected destinations here.
            // =====================================================

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
                            name =
                                itinerary.title,

                            type =
                                itinerary.type,

                            date =
                                itinerary.date,

                            startTime =
                                itinerary.time,

                            location =
                                itinerary.location,

                            notes =
                                itinerary.notes,

                            booked =
                                itinerary.booked
                        )

                    activityDao.updateActivity(
                        updatedActivity
                    )

                    // =================================================
                    // Keep the Activity destinations aligned with the
                    // destinations explicitly selected on the
                    // itinerary.
                    // =================================================

                    syncActivityToExplicitDestinations(
                        updatedActivity,
                        destinationIds
                    )

                    // The linked itinerary already exists and has
                    // just been given its explicit destinations above.
                    // Do not call syncActivityToItinerary() here,
                    // because that would rebuild the itinerary
                    // destinations from the Activity and potentially
                    // undo the user's selection.
                }
            }

            // =====================================================
            // CLEAN UP DESTINATIONS THAT ARE NO LONGER USED
            // =====================================================

            for (destinationId in oldDestinationIds) {

                if (
                    destinationId !in destinationIds
                ) {
                    cleanupOrphanedDestination(
                        destinationId
                    )
                }
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

                    for (
                    destinationId
                    in activityDestinationIds
                    ) {

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

    // =========================================================
    // ITINERARY → DESTINATION
    //
    // LEGACY LOCATION FALLBACK
    //
    // Used for manually-created standalone itinerary items.
    // =========================================================

    private suspend fun syncItineraryToDestination(
        itinerary: ItineraryEntity
    ) {

        val oldDestinationIds =
            itineraryDestinationDao
                .getDestinationIdsForItinerary(
                    itinerary.id
                )

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

            for (destinationId in oldDestinationIds) {

                cleanupOrphanedDestination(
                    destinationId
                )
            }

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
                    itineraryId =
                        itinerary.id,
                    destinationId =
                        destinationId
                )
            )

        tripDestinationDao
            .insertTripDestination(
                TripDestinationEntity(
                    tripId =
                        itinerary.tripId,
                    destinationId =
                        destinationId
                )
            )

        for (oldDestinationId in oldDestinationIds) {

            if (
                oldDestinationId !=
                destinationId
            ) {

                cleanupOrphanedDestination(
                    oldDestinationId
                )
            }
        }
    }

    // =========================================================
    // REMOVE STALE TRIP REFERENCES
    // =========================================================

    private suspend fun cleanupStaleTripReferences(
        destinationId: Long
    ) {

        val tripIds =
            tripDestinationDao
                .getTripIdsForDestination(
                    destinationId
                )

        if (tripIds.isEmpty()) {
            return
        }

        for (tripId in tripIds) {

            val activities =
                activityDao
                    .getActivitiesForTrip(
                        tripId
                    )
                    .first()

            val activityUsesDestination =
                activities.any { activity ->

                    destinationId in
                            activityDestinationDao
                                .getDestinationIdsForActivity(
                                    activity.id
                                )
                }

            val itineraryItems =
                itineraryDao
                    .getItineraryForTrip(
                        tripId
                    )
                    .first()

            val itineraryUsesDestination =
                itineraryItems.any { itinerary ->

                    destinationId in
                            itineraryDestinationDao
                                .getDestinationIdsForItinerary(
                                    itinerary.id
                                )
                }

            if (
                !activityUsesDestination &&
                !itineraryUsesDestination
            ) {

                tripDestinationDao
                    .deleteTripDestination(
                        tripId,
                        destinationId
                    )
            }
        }
    }

    // =========================================================
    // GLOBAL ORPHAN DESTINATION CLEANUP
    // =========================================================

    private suspend fun cleanupOrphanedDestination(
        destinationId: Long
    ) {

        cleanupStaleTripReferences(
            destinationId
        )

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