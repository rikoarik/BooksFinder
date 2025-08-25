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
            // Extract just the workId part (e.g., "OL45361W" from "works/OL45361W")
            // Handle multiple cases: "works/OL45361W", "/works/OL45361W", "OL45361W"
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
