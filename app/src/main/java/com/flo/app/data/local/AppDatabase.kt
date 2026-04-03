package com.flo.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flo.app.data.local.dao.GoalDao
import com.flo.app.data.local.dao.TransactionDao
import com.flo.app.data.local.entity.GoalEntity
import com.flo.app.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, GoalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
}