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
                navController = navController,
                viewModel = bookViewModel
            )
        }
        
        composable(
            route = Screen.BookDetail.route,
            arguments = Screen.BookDetail.arguments
        ) { backStackEntry ->
            val workId = backStackEntry.arguments?.getString("workId")
            
            if (workId != null) {
                // workId now contains just the ID part (e.g., "OL45361W"), so we need to add "works/" prefix
                val fullWorkId = "works/$workId"
                
                val bookDetailViewModel: BookDetailViewModel = viewModel(
                    factory = BookDetailViewModelFactory(
                        repository = bookRepository,
                        workId = fullWorkId
                    )
                )
                BookDetailScreen(
                    viewModel = bookDetailViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            } else {
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
                    navController.popBackStack()
                }
            )
        }
    }
}
