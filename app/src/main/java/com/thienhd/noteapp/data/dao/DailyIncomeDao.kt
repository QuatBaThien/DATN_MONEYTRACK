package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thienhd.noteapp.data.entities.DailyIncome
@Dao
interface DailyIncomeDao {
    @Query("SELECT * FROM DailyIncome")
    fun getAll(): List<DailyIncome>

    @Query("SELECT * FROM DailyIncome WHERE dailyIncomeID = :id")
    fun getById(id: Int): DailyIncome?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dailyIncome: DailyIncome)

    @Delete
    fun delete(dailyIncome: DailyIncome)
}