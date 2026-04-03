package com.flo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: Int = 1,          // only ever one active goal, id is always 1
    val targetAmount: Double,
    val title: String,
    val createdEpochDay: Long
)