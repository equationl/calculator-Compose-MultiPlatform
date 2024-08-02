package com.equationl.common.platform

import androidx.room.RoomDatabase
import com.equationl.common.database.HistoryDb

const val DATABASE_NAME = "history.db"

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class RoomBuilder() {
    fun builder(): RoomDatabase.Builder<HistoryDb>
}