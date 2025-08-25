package com.app.bookfinder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.app.bookfinder.ui.screens.BookDetailScreen
import com.app.bookfinder.ui.screens.HomeScreen
import com.app.bookfinder.ui.screens.SettingsScreen
import com.app.bookfinder.ui.viewmodel.BookDetailViewModel
import com.app.bookfinder.ui.viewmodel.BookDetailViewModelFactory
import com.app.bookfinder.ui.viewmodel.BookViewModel
import com.app.bookfinder.data.preferences.UserPreferences
import com.app.bookfinder.data.repository.BookRepository
import androidx.lifecycle.SavedStateHandle

@Composable
fun BookFinderNavigation(
    navController: NavHostController = rememberNavController(),
    bookViewModel: BookViewModel,
    bookRepository: BookRepository,
    userPreferences: UserPreferences,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = bookViewModel,
                onBookClick = { workId ->
                    println("DEBUG: Book clicked with workId: $workId")
                    val route = Screen.BookDetail.createRoute(workId)
                    println("DEBUG: Navigating to route: $route")
                    navController.navigate(route)
                },
                onSettingsClick = {
                    println("DEBUG: Settings clicked")
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(
            route = Screen.BookDetail.route,
            arguments = Screen.BookDetail.arguments
        ) { backStackEntry ->
            val workId = backStackEntry.arguments?.getString("workId")
            println("DEBUG: BookDetail route entered with workId: $workId")
            
            if (workId != null) {
                val bookDetailViewModel: BookDetailViewModel = viewModel(
                    factory = BookDetailViewModelFactory(
                        repository = bookRepository,
                        workId = workId
                    )
                )
                BookDetailScreen(
                    viewModel = bookDetailViewModel,
                    onBackClick = {
                        println("DEBUG: Back button clicked")
                        navController.popBackStack()
                    }
                )
            } else {
                println("DEBUG: workId is null, showing error")
                // Handle case when workId is null
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: Book ID not found")
                }
            }
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                userPreferences = userPreferences,
                onThemeChange = onThemeChange,
                onBackClick = {
                    println("DEBUG: Settings back clicked")
                    navController.popBackStack()
                }
            )
        }
    }
}
