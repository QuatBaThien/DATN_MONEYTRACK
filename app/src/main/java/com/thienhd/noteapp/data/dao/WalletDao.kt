package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thienhd.noteapp.data.entities.Wallet

@Dao
interface WalletDao {
    @Query("SELECT * FROM Wallet")
    fun getAll(): List<Wallet>

    @Query("SELECT * FROM Wallet WHERE walletID = :id")
    fun getById(id: Int): Wallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallet: Wallet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(wallets: List<Wallet>)

    @Update
    fun update(wallet: Wallet)

    @Delete
    fun delete(wallet: Wallet)
}