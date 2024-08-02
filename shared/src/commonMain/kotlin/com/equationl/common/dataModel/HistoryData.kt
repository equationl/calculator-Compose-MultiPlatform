package com.equationl.common.dataModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity("history")
data class HistoryData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "show_text")
    val showText: String,
    @ColumnInfo(name = "left_number")
    val lastInputText: String,
    @ColumnInfo(name = "right_number")
    val inputText: String,
    @ColumnInfo(name = "operator")
    val operator: Operator,
    @ColumnInfo(name = "result")
    val result: String,
    @ColumnInfo(name = "create_time")
    val createTime: Long = Clock.System.now().epochSeconds
)
