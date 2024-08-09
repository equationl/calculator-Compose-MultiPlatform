package com.equationl.common.platform

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.equationl.common.database.HistoryDatabaseCtor
import com.equationl.common.database.HistoryDb

// TODO 需要测试
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class RoomBuilder {
    actual fun builder(): RoomDatabase.Builder<HistoryDb> {
        val dbFilePath = NSHomeDirectory() + "/${DATABASE_NAME}"
        return Room.databaseBuilder<HistoryDb>(
            name = dbFilePath,
            factory = HistoryDatabaseCtor::initialize
        ).setDriver(BundledSQLiteDriver())
    }
}