package com.equationl.common.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.equationl.common.dataModel.MemoryData

@Dao
interface MemoryDao {
    @Query("select * from memory order by id DESC")
    suspend fun getAllMemory(): List<MemoryData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(item: MemoryData)

    @Update
    suspend fun updateMemory(item: MemoryData)

    @Delete
    suspend fun deleteMemory(item: MemoryData)

    @Query("DELETE FROM memory")
    suspend fun deleteAllMemory()
}