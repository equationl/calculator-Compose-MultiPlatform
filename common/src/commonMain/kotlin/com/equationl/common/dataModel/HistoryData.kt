package com.equationl.common.dataModel


data class HistoryData(

    val id: Int = 0,

    val showText: String,

    val lastInputText: String,

    val inputText: String,

    val operator: Operator,

    val result: String,

    val createTime: Long = System.currentTimeMillis(),
)
