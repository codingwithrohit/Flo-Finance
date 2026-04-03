package com.flo.app.data.model

data class HealthScore(
    val score: Int,
    val budgetScore: Int,
    val streakScore: Int,
    val savingsScore: Int,
    val noSpendScore: Int,
    val label: String
)