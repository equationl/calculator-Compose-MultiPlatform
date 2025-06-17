package com.equationl.common.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.equationl.common.dataModel.ScienceHistoryData

@Dao
interface ScienceHistoryDao {
    @Query("select * from science_history order by id DESC")
    suspend fun getAll(): List<ScienceHistoryData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ScienceHistoryData)

    @Update
    suspend fun update(item: ScienceHistoryData)

    @Delete
    suspend fun delete(item: ScienceHistoryData)

    @Query("DELETE FROM science_history")
    suspend fun deleteAll()
}