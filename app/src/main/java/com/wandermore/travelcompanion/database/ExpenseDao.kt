package com.wandermore.travelcompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: ExpenseEntity)


    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY date DESC")
    fun getExpensesForTrip(tripId: Long): Flow<List<ExpenseEntity>>


    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Long): ExpenseEntity?


    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)


    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

}