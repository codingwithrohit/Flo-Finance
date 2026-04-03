package com.flo.app.data.local.dao

import androidx.room.*
import com.flo.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE id = 1")
    fun getActiveGoal(): Flow<GoalEntity?>

    @Query("DELETE FROM goals WHERE id = 1")
    suspend fun deleteGoal()
}