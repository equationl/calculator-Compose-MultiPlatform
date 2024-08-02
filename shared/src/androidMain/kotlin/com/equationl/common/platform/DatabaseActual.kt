package com.equationl.common.platform

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.blankj.utilcode.util.ActivityUtils
import com.equationl.common.database.HistoryDb

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class RoomBuilder {
    actual fun builder(): RoomDatabase.Builder<HistoryDb> {
        val appContext = ActivityUtils.getTopActivity().applicationContext
        val dbFile = appContext.getDatabasePath(DATABASE_NAME)

        return Room.databaseBuilder<HistoryDb>(
            context = appContext,
            name = dbFile.absolutePath
        ).setDriver(BundledSQLiteDriver())
    }
}