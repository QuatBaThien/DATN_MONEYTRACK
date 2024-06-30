package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thienhd.noteapp.data.entities.Budget

@Dao
interface BudgetDao {
    @Query("SELECT * FROM Budget")
    fun getAll(): List<Budget>

    @Query("SELECT * FROM Budget WHERE budgetID = :id")
    fun getById(id: Int): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(budget: Budget)

    @Delete
    fun delete(budget: Budget)
}