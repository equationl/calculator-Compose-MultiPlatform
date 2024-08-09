package com.equationl.common.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.equationl.common.dataModel.HistoryData
import com.equationl.common.platform.RoomBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [HistoryData::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(HistoryConverters::class)
@ConstructedBy(HistoryDatabaseCtor::class)
abstract class HistoryDb : RoomDatabase() {
    companion object {
        val instance by lazy {
            getRoomDatabase(
                builder = RoomBuilder().builder()
            )
        }

        fun getRoomDatabase(
            builder: Builder<HistoryDb>,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): HistoryDb {
            return builder
                .setQueryCoroutineContext(dispatcher)
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }

    abstract fun history(): HistoryDao
}

expect object HistoryDatabaseCtor : RoomDatabaseConstructor<HistoryDb>