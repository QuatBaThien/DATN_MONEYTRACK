package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thienhd.noteapp.data.entities.Debt

@Dao
interface DebtDao {
    @Query("SELECT * FROM Debt")
    fun getAll(): List<Debt>

    @Query("SELECT * FROM Debt WHERE debtID = :id")
    fun getById(id: Int): Debt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(debt: Debt)

    @Delete
    fun delete(debt: Debt)
}