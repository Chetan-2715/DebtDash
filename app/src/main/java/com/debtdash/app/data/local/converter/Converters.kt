package com.debtdash.app.data.local.converter

import androidx.room.TypeConverter
import com.debtdash.app.data.local.entity.SplitModel
import com.debtdash.app.data.local.entity.TransactionType

/**
 * Room TypeConverters for enum ↔ String serialization.
 */
class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType =
        TransactionType.valueOf(value)

    @TypeConverter
    fun fromSplitModel(model: SplitModel): String = model.name

    @TypeConverter
    fun toSplitModel(value: String): SplitModel =
        SplitModel.valueOf(value)

    @TypeConverter
    fun fromContactType(type: com.debtdash.app.data.local.entity.ContactType): String = type.name

    @TypeConverter
    fun toContactType(value: String): com.debtdash.app.data.local.entity.ContactType =
        com.debtdash.app.data.local.entity.ContactType.valueOf(value)
}
