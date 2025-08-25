package com.app.bookfinder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bookfinder.R
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.ui.components.SearchBar
import com.app.bookfinder.ui.components.FilterSection
import com.app.bookfinder.ui.navigation.Screen
import com.app.bookfinder.ui.viewmodel.BookViewModel
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector

// Data class for tab items
data class TabItem(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: BookViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    
    val tabs = listOf(
        TabItem(
            title = stringResource(R.string.tab_book_list),
            icon = Icons.AutoMirrored.Filled.List
        ),
        TabItem(
            title = stringResource(R.string.tab_favorites),
            icon = Icons.Default.Favorite
        )
    )
    
    // Safe state collection with error handling
    val searchQueryState by viewModel.searchQuery.collectAsStateWithLifecycle()
    val books by viewModel.books.collectAsStateWithLifecycle()
    val favoriteBooks by viewModel.favoriteBooks.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val hasMorePages by viewModel.hasMorePages.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val isLoadingSort by viewModel.isLoadingSort.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        // Load initial data if needed
        try {
            // Safe initialization
        } catch (e: Exception) {
            // Handle initialization error
            println("HomeScreen initialization error: ${e.message}")
        }
    }
    
    // Auto-collapse search when tab changes (no need since search tab is removed)
    LaunchedEffect(selectedTabIndex) {
        // Search functionality is now always available via top bar
        // No need to auto-collapse based on tab changes
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    // Search toggle button in top bar
                    IconButton(
                        onClick = {
                            isSearchExpanded = !isSearchExpanded
                            if (!isSearchExpanded) {
                                searchQuery = ""
                                try {
                                    viewModel.clearSearch()
                                } catch (e: Exception) {
                                    println("Error clearing search: ${e.message}")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (isSearchExpanded) "Close search" else "Open search",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    // Settings button
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Settings.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Collapsible Search Bar - initially collapsed
            AnimatedVisibility(
                visible = isSearchExpanded,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { 
                        searchQuery = it
                        if (it.isNotEmpty()) {
                            try {
                                viewModel.searchBooks(it)
                            } catch (e: Exception) {
                                println("Error searching books: ${e.message}")
                            }
                        } else {
                            try {
                                viewModel.clearSearch()
                            } catch (e: Exception) {
                                println("Error clearing search: ${e.message}")
                            }
                        }
                    },
                    onSearch = { query ->
                        if (query.isNotEmpty()) {
                            try {
                                viewModel.searchBooks(query)
                            } catch (e: Exception) {
                                println("Error searching books: ${e.message}")
                            }
                        }
                    },
                    onClear = {
                        searchQuery = ""
                        try {
                            viewModel.clearSearch()
                        } catch (e: Exception) {
                            println("Error clearing search: ${e.message}")
                        }
                    },
                    isLoading = books is Resource.Loading,
                    isExpanded = isSearchExpanded,
                    onToggleExpanded = { isSearchExpanded = !isSearchExpanded },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Filter Section - show when search is expanded
            AnimatedVisibility(
                visible = isSearchExpanded,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                FilterSection(
                    selectedSort = selectedSort,
                    onSortChange = { sort ->
                        try {
                            viewModel.setSort(sort)
                        } catch (e: Exception) {
                            println("Error setting sort: ${e.message}")
                        }
                    },
                    isLoading = isLoadingSort
                )
            }
            
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }
            
            // Tab Content
            when (selectedTabIndex) {
                0 -> BookListTab(
                    books = books,
                    onBookClick = { bookKey ->
                        navController.navigate(Screen.BookDetail.createRoute(bookKey))
                    },
                    onFavoriteClick = { book ->
                        try {
                            viewModel.toggleFavorite(book)
                        } catch (e: Exception) {
                            println("Error toggling favorite: ${e.message}")
                        }
                    },
                    onRetry = { 
                        try {
                            viewModel.clearSearch()
                        } catch (e: Exception) {
                            println("Error clearing search on retry: ${e.message}")
                        }
                    },
                    onRefresh = { 
                        try {
                            viewModel.clearSearch()
                        } catch (e: Exception) {
                            println("Error clearing search on refresh: ${e.message}")
                        }
                    },
                    isRefreshing = isRefreshing,
                    onLoadMore = {
                        // Load more functionality
                    },
                    hasMorePages = hasMorePages
                )
                1 -> FavoritesTab(
                    books = favoriteBooks,
                    onBookClick = { bookKey ->
                        navController.navigate(Screen.BookDetail.createRoute(bookKey))
                    },
                    onFavoriteClick = { book ->
                        try {
                            viewModel.toggleFavorite(book)
                        } catch (e: Exception) {
                            println("Error toggling favorite: ${e.message}")
                        }
                    },
                    onRefresh = { /* Refresh favorites */ },
                    isRefreshing = isRefreshing
                )
            }
        }
    }
}