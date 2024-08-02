package com.equationl.common.database

import androidx.room.TypeConverter
import com.equationl.common.dataModel.Operator

class HistoryConverters {
    @TypeConverter
    fun fromOperator(operator: Operator): String {
        return operator.name
    }

    @TypeConverter
    fun toOperator(operator: String): Operator {
        return try {
            Operator.valueOf(operator)
        } catch (e: IllegalArgumentException) {
            Operator.NUll
        }
    }
}