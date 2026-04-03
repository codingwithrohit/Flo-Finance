package com.flo.app.ui.navigation

object NavRoutes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val TRANSACTIONS = "transactions"
    const val ADD_TRANSACTION = "add_transaction"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
    const val GOALS = "goals"
    const val INSIGHTS = "insights"
    const val SETTINGS = "settings"


    fun editTransaction(id: Long) = "edit_transaction/$id"
}