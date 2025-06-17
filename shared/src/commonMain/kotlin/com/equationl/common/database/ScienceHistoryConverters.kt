package com.equationl.common.database

import androidx.room.TypeConverter
import com.equationl.common.dataModel.ScienceOperator

class ScienceHistoryConverters {
    @TypeConverter
    fun fromOperator(operator: ScienceOperator): String {
        return operator.name
    }

    @TypeConverter
    fun toOperator(operator: String): ScienceOperator {
        return try {
            ScienceOperator.valueOf(operator)
        } catch (e: IllegalArgumentException) {
            ScienceOperator.NUll
        }
    }
}