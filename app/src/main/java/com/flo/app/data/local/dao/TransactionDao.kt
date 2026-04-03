package com.flo.app.data.local.dao

import androidx.room.*
import com.flo.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY dateEpochDay DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateEpochDay >= :startEpochDay ORDER BY dateEpochDay DESC")
    fun getTransactionsSince(startEpochDay: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateEpochDay >= :startEpochDay AND dateEpochDay <= :endEpochDay")
    fun getTransactionsBetween(startEpochDay: Long, endEpochDay: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND dateEpochDay >= :startEpochDay")
    fun getTotalIncomeSince(startEpochDay: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND dateEpochDay >= :startEpochDay")
    fun getTotalExpenseSince(startEpochDay: Long): Flow<Double?>

    @Query("SELECT DISTINCT dateEpochDay FROM transactions WHERE dateEpochDay >= :startEpochDay")
    fun getDistinctDaysSince(startEpochDay: Long): Flow<List<Long>>

    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY dateEpochDay DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>
}