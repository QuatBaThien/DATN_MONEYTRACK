package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thienhd.noteapp.data.entities.Report

@Dao
interface ReportDao {
    @Query("SELECT * FROM Report")
    fun getAll(): List<Report>

    @Query("SELECT * FROM Report WHERE reportID = :id")
    fun getById(id: Int): Report?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(report: Report)

    @Delete
    fun delete(report: Report)
}