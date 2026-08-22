package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "activity_destinations",
    primaryKeys = ["activityId", "destinationId"],
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DestinationEntity::class,
            parentColumns = ["id"],
            childColumns = ["destinationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["activityId"]),
        Index(value = ["destinationId"])
    ]
)
data class ActivityDestinationEntity(

    val activityId: Long,

    val destinationId: Long
)