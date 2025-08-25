package com.app.bookfinder.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object BookDetail : Screen("book_detail/works/{workId}") {
        val arguments = listOf(
            navArgument("workId") {
                type = NavType.StringType
            }
        )
        
        fun createRoute(workId: String): String {
            val cleanWorkId = when {
                workId.startsWith("works/") -> workId.substringAfter("works/")
                workId.startsWith("/works/") -> workId.substringAfter("/works/")
                else -> workId
            }
            return "book_detail/works/$cleanWorkId"
        }
    }
    object Settings : Screen("settings")
}
