package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thienhd.noteapp.data.entities.DebtPayment

@Dao
interface DebtPaymentDao {
    @Query("SELECT * FROM DebtPayment")
    fun getAll(): List<DebtPayment>

    @Query("SELECT * FROM DebtPayment WHERE paymentID = :id")
    fun getById(id: Int): DebtPayment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(debtPayment: DebtPayment)

    @Delete
    fun delete(debtPayment: DebtPayment)
}