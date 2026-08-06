package com.wandermore.travelcompanion.data.repository

import com.wandermore.travelcompanion.model.Trip
import com.wandermore.travelcompanion.model.TripStatus
import com.wandermore.travelcompanion.database.TripDao
import com.wandermore.travelcompanion.database.TripEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate


class TripRepository(
    private val tripDao: TripDao
) {


    fun getTrips(): Flow<List<Trip>> {

        return tripDao.getAllTrips()
            .map { trips ->

                trips.map { entity ->

                    Trip(
                        id = entity.id,
                        name = entity.name,
                        startDate = entity.startDate,
                        endDate = entity.endDate,
                        homeCurrency = entity.homeCurrency,
                        status = TripStatus.valueOf(entity.status)
                    )

                }

            }

    }



    fun getTripByIdFlow(
        id: Long
    ): Flow<Trip?> {

        return tripDao.getTripById(id)
            .map { entity ->

                entity?.let {

                    Trip(
                        id = it.id,
                        name = it.name,
                        startDate = it.startDate,
                        endDate = it.endDate,
                        homeCurrency = it.homeCurrency,
                        status = TripStatus.valueOf(it.status)
                    )

                }

            }

    }



    suspend fun getTripById(
        id: Long
    ): Trip? {

        return tripDao.getAllTrips()
            .map { trips ->

                trips.find { entity ->

                    entity.id == id

                }

            }
            .firstOrNull()
            ?.let { entity ->

                Trip(
                    id = entity.id,
                    name = entity.name,
                    startDate = entity.startDate,
                    endDate = entity.endDate,
                    homeCurrency = entity.homeCurrency,
                    status = TripStatus.valueOf(entity.status)
                )

            }

    }



    suspend fun addTrip(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate,
        homeCurrency: String
    ) {

        tripDao.insertTrip(

            TripEntity(
                name = name,
                startDate = startDate,
                endDate = endDate,
                homeCurrency = homeCurrency
            )

        )

    }



    suspend fun updateTrip(
        trip: Trip
    ) {

        tripDao.updateTrip(

            TripEntity(
                id = trip.id,
                name = trip.name,
                startDate = trip.startDate,
                endDate = trip.endDate,
                homeCurrency = trip.homeCurrency,
                status = trip.status.name
            )

        )

    }



    suspend fun updateTripStatus(
        tripId: Long,
        status: TripStatus
    ) {

        tripDao.updateTripStatus(
            tripId = tripId,
            status = status.name
        )

    }



    suspend fun deleteTrip(
        trip: Trip
    ) {

        tripDao.deleteTrip(

            TripEntity(
                id = trip.id,
                name = trip.name,
                startDate = trip.startDate,
                endDate = trip.endDate,
                homeCurrency = trip.homeCurrency,
                status = trip.status.name
            )

        )

    }

}