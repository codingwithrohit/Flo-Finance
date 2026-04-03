package com.flo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String,        // "INCOME" or "EXPENSE"
    val category: String,
    val note: String,
    val dateEpochDay: Long   // LocalDate.toEpochDay() — we store as Long, convert in UI
)