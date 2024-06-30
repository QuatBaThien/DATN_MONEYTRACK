package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thienhd.noteapp.data.entities.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `Transaction`")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM `Transaction` WHERE transactionID = :id")
    fun getById(id: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(transactions: List<Transaction>)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)
}