package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thienhd.noteapp.data.entities.DailyExpense

@Dao
interface DailyExpenseDao {
    @Query("SELECT * FROM DailyExpense")
    fun getAll(): List<DailyExpense>

    @Query("SELECT * FROM DailyExpense WHERE dailyExpenseID = :id")
    fun getById(id: Int): DailyExpense?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dailyExpense: DailyExpense)

    @Delete
    fun delete(dailyExpense: DailyExpense)
}