package com.equationl.common.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.equationl.common.database.HistoryDatabase

// 如果想保存到本地可以参考 https://www.reddit.com/r/Kotlin/comments/10q8xfd/comment/j6qdixf/

actual fun createDriver(): SqlDriver {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    HistoryDatabase.Schema.create(driver)
    return driver
}
