package com.flo.app.data.local.entity

import com.flo.app.data.model.Transaction
import com.flo.app.data.model.TransactionType
import java.time.LocalDate

fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        type = if (type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE,
        category = category,
        note = note,
        date = LocalDate.ofEpochDay(dateEpochDay)
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type.name,
        category = category,
        note = note,
        dateEpochDay = date.toEpochDay()
    )
}