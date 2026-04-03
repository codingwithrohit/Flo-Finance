package com.flo.app.data.repository

import com.flo.app.data.local.dao.GoalDao
import com.flo.app.data.local.entity.GoalEntity
import com.flo.app.data.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {
    fun getActiveGoal(): Flow<Goal?> {
        return goalDao.getActiveGoal().map { entity ->
            entity?.let {
                Goal(
                    targetAmount = it.targetAmount,
                    title = it.title,
                    createdDate = LocalDate.ofEpochDay(it.createdEpochDay)
                )
            }
        }
    }

    suspend fun saveGoal(goal: Goal) {
        goalDao.insertGoal(
            GoalEntity(
                id = 1,
                targetAmount = goal.targetAmount,
                title = goal.title,
                createdEpochDay = goal.createdDate.toEpochDay()
            )
        )
    }

    suspend fun deleteGoal() {
        goalDao.deleteGoal()
    }
}