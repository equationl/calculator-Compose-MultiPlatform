package com.equationl.common.platform

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.equationl.common.database.HistoryDb
import com.equationl.common.database.instantiateImpl
import java.io.File


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class RoomBuilder {
    //TODO 运行 ReleaseDistributable 无法链接到数据库，但是单独运行 Release 或 Distributable 却可以……
    // 这应该是 ROOM 的 BUG ，运行官方 sample 有同样的问题
    actual fun builder(): RoomDatabase.Builder<HistoryDb> {
        // 这里获取到的是临时目录，似乎 Desktop 没有一个统一应用数据目录，所以暂时沿用这个目录
        val dbFilePath = File(System.getProperty("java.io.tmpdir"), DATABASE_NAME).absolutePath

        println("save data path = $dbFilePath")

        return Room.databaseBuilder<HistoryDb>(
            name = dbFilePath,
            factory = { HistoryDb::class.instantiateImpl() }
        ).setDriver(BundledSQLiteDriver())
    }
}
