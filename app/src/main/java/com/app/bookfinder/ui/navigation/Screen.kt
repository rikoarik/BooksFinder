package com.app.bookfinder.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object BookDetail : Screen("book_detail/{workId}") {
        val arguments = listOf(
            navArgument("workId") {
                type = NavType.StringType
            }
        )
        
        fun createRoute(workId: String): String {
            val cleanWorkId = workId.removePrefix("/")
            return "book_detail/$cleanWorkId"
        }
    }
    object Settings : Screen("settings")
}
