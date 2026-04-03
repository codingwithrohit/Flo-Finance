package com.flo.app.data.model

data class Insight(
    val title: String,
    val description: String,
    val amount: Double,
    val type: InsightType,
    val categoryBreakdown: List<CategorySpending> = emptyList()
)

enum class InsightType {
    TOP_CATEGORY,
    MONTHLY_COMPARISON,
    CATEGORY_BREAKDOWN
}