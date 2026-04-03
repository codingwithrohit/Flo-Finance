package com.flo.app.data.model

import java.time.LocalDate

data class Goal(
    val targetAmount: Double,
    val title: String,
    val createdDate: LocalDate
)