package com.app.bookfinder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.bookfinder.ui.screens.BookDetailScreen
import com.app.bookfinder.ui.screens.HomeScreen
import com.app.bookfinder.ui.screens.SettingsScreen
import com.app.bookfinder.ui.viewmodel.BookDetailViewModel
import com.app.bookfinder.ui.viewmodel.BookViewModel
import com.app.bookfinder.data.preferences.UserPreferences

@Composable
fun BookFinderNavigation(
    navController: NavHostController = rememberNavController(),
    bookViewModel: BookViewModel,
    bookDetailViewModel: BookDetailViewModel,
    userPreferences: UserPreferences,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = bookViewModel,
                onBookClick = { workId ->
                    navController.navigate(Screen.BookDetail.createRoute(workId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(
            route = Screen.BookDetail.route,
            arguments = Screen.BookDetail.arguments
        ) {
            BookDetailScreen(
                viewModel = bookDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                userPreferences = userPreferences,
                onThemeChange = onThemeChange,
                onLanguageChange = onLanguageChange,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
