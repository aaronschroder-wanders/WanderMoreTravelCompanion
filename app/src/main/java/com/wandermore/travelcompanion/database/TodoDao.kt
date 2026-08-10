package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query(
        """
        SELECT * FROM todos
        WHERE tripId = :tripId
        ORDER BY
            CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END,
            dueDate ASC,
            completed ASC
        """
    )
    fun getTodosForTrip(
        tripId: Long
    ): Flow<List<TodoEntity>>

    @Query(
        """
        SELECT * FROM todos
        WHERE id = :todoId
        LIMIT 1
        """
    )
    suspend fun getTodoById(
        todoId: Long
    ): TodoEntity?

    // ---------------------------------------------------------
    // BACKUP
    // ---------------------------------------------------------

    @Query("SELECT * FROM todos")
    suspend fun getAllTodosForBackup(): List<TodoEntity>

    @Insert
    suspend fun insertTodo(
        todo: TodoEntity
    )

    @Update
    suspend fun updateTodo(
        todo: TodoEntity
    )

    @Delete
    suspend fun deleteTodo(
        todo: TodoEntity
    )
}