package com.equationl.common.database

import app.cash.sqldelight.ColumnAdapter
import com.equationl.common.dataModel.HistoryData
import com.equationl.common.dataModel.Operator
import com.equationl.common.platform.createDriver

internal class DataBase {

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DataBase()
        }
    }

    private val longOfIntAdapter = object : ColumnAdapter<Int, Long> {
        override fun decode(databaseValue: Long): Int {
            return databaseValue.toInt()
        }

        override fun encode(value: Int): Long {
            return value.toLong()
        }
    }

    private val stringOfOperatorAdapter = object : ColumnAdapter<Operator, String> {
        override fun decode(databaseValue: String): Operator {
            return try {
                Operator.valueOf(databaseValue)
            } catch (e: IllegalArgumentException) {
                Operator.NUll
            }
        }

        override fun encode(value: Operator): String {
            return value.name
        }

    }

    private val database = HistoryDatabase(
        createDriver(),
        HistoryAdapter = History.Adapter(
            idAdapter = longOfIntAdapter,
            operator_Adapter = stringOfOperatorAdapter
        )
    )
    private val dbQuery = database.historyDatabaseQueries

    internal fun delete(historyData: HistoryData?) {
        if (historyData == null) {
            dbQuery.deleteAllHistory()
        }
        else {
            dbQuery.deleteHistory(historyData.id)
        }
    }

    internal fun getAll(): List<HistoryData> {
        return dbQuery.getAllHistory(::mapHistoryList).executeAsList()
    }

    internal fun insert(item: HistoryData) {
        item.run {
            dbQuery.insertHistory(
                History(id, showText, lastInputText, inputText, operator, result, createTime)
            )
        }
    }

    private fun mapHistoryList(
        id: Int,
        show_text: String?,
        left_number: String?,
        right_number: String?,
        operator_: Operator?,
        result: String?,
        create_time: Long?,
    ): HistoryData {
        return HistoryData(
            id = id,
            showText = show_text ?: "",
            lastInputText = left_number ?: "",
            inputText = right_number ?: "",
            operator = operator_ ?: Operator.NUll,
            result = result ?: "",
            createTime = create_time ?: 0
        )
    }


}