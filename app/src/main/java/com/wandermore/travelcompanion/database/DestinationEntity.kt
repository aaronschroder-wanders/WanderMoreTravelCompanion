package com.wandermore.travelcompanion.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "destinations",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class DestinationEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val active: Boolean = true
)