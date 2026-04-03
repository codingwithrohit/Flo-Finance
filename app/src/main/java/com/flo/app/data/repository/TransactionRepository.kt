package com.flo.app.data.repository

import com.flo.app.data.local.dao.TransactionDao
import com.flo.app.data.local.entity.toTransaction
import com.flo.app.data.local.entity.toEntity
import com.flo.app.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
            .map { entities -> entities.map { it.toTransaction() } }
    }

    fun getTransactionsSince(date: LocalDate): Flow<List<Transaction>> {
        return transactionDao.getTransactionsSince(date.toEpochDay())
            .map { entities -> entities.map { it.toTransaction() } }
    }

    fun getTransactionsBetween(start: LocalDate, end: LocalDate): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetween(start.toEpochDay(), end.toEpochDay())
            .map { entities -> entities.map { it.toTransaction() } }
    }

    fun getTotalIncomeSince(date: LocalDate): Flow<Double> {
        return transactionDao.getTotalIncomeSince(date.toEpochDay())
            .map { it ?: 0.0 }
    }

    fun getTotalExpenseSince(date: LocalDate): Flow<Double> {
        return transactionDao.getTotalExpenseSince(date.toEpochDay())
            .map { it ?: 0.0 }
    }

    fun getDistinctDaysSince(date: LocalDate): Flow<List<Long>> {
        return transactionDao.getDistinctDaysSince(date.toEpochDay())
    }

    fun searchTransactions(query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(query)
            .map { entities -> entities.map { it.toTransaction() } }
    }

    fun getTransactionsByType(type: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type)
            .map { entities -> entities.map { it.toTransaction() } }
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }
}