package com.app.bookfinder

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.SavedStateHandle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.bookfinder.data.local.BookDatabase
import com.app.bookfinder.data.remote.RetrofitClient
import com.app.bookfinder.data.repository.BookRepository
import com.app.bookfinder.data.preferences.UserPreferences
import com.app.bookfinder.ui.navigation.BookFinderNavigation
import com.app.bookfinder.ui.theme.BookFinderTheme
import com.app.bookfinder.ui.viewmodel.BookDetailViewModel
import com.app.bookfinder.ui.viewmodel.BookViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookFinderApp()
        }
    }
}

@Composable
fun BookFinderApp() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Initialize dependencies
    val bookDatabase = BookDatabase.getDatabase(context)
    val bookRepository = BookRepository(
        api = RetrofitClient.openLibraryApi,
        bookDao = bookDatabase.bookDao()
    )
    
    // User preferences
    val userPreferences = remember {
        UserPreferencesRepository(context.dataStore)
    }
    
    val userPreferencesState by userPreferences.userPreferencesFlow.collectAsState(
        initial = UserPreferences()
    )
    
    // ViewModels
    val bookViewModel: BookViewModel = viewModel {
        BookViewModel(bookRepository)
    }
    
    // Theme based on preferences
    val isDarkTheme = userPreferencesState.isDarkMode
    
    BookFinderTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BookFinderNavigation(
                navController = navController,
                bookViewModel = bookViewModel,
                bookRepository = bookRepository,
                userPreferences = userPreferencesState,
                onThemeChange = { isDark ->
                    coroutineScope.launch {
                        userPreferences.updateDarkMode(isDark)
                    }
                }
            )
        }
    }
}

// Simple UserPreferencesRepository implementation
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    private val LANGUAGE = stringPreferencesKey("language")
    
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            isDarkMode = preferences[IS_DARK_MODE] ?: false,
            language = preferences[LANGUAGE] ?: "en"
        )
    }
    
    suspend fun updateDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }
    
    suspend fun updateLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }
}