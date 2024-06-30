package com.thienhd.noteapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thienhd.noteapp.data.entities.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category WHERE type = :type")
    fun getCategoriesByType(type: Int): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<Category>)

    @Update
    fun update(category: Category)

    @Delete
    fun delete(category: Category)
}
