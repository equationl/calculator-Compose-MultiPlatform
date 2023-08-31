package com.equationl.common.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.equationl.common.database.HistoryDatabase

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(HistoryDatabase.Schema, "history.db")
}
