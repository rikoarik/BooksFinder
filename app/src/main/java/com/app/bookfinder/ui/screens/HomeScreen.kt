package com.app.bookfinder.ui.screens

import android.annotation.SuppressLint
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bookfinder.R
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.ui.components.SearchBar
import com.app.bookfinder.ui.components.FilterSection
import com.app.bookfinder.ui.navigation.Screen
import com.app.bookfinder.ui.viewmodel.BookViewModel
import androidx.navigation.NavController
import com.app.bookfinder.data.model.TabItem


@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: BookViewModel
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
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

    val books by viewModel.books.collectAsStateWithLifecycle()
    val favoriteBooks by viewModel.favoriteBooks.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val hasMorePages by viewModel.hasMorePages.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val isLoadingSort by viewModel.isLoadingSort.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()

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
                    IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (isSearchExpanded) "Close search" else "Open search",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
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
                Column {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { query ->
                            if (query.isNotEmpty()) viewModel.searchBooks(query)
                        },
                        onClear = {
                            searchQuery = ""
                            viewModel.clearSearch()
                        },
                        isLoading = books is Resource.Loading,
                        isExpanded = isSearchExpanded,
                        onToggleExpanded = { isSearchExpanded = !isSearchExpanded },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    FilterSection(
                        selectedSort = selectedSort,
                        onSortChange = { sort -> viewModel.setSort(sort) },
                        isLoading = isLoadingSort
                    )
                }
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = tab.title, style = MaterialTheme.typography.labelMedium) },
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

            when (selectedTabIndex) {
                0 -> BookListTab(
                    books = books,
                    onBookClick = { bookKey -> navController.navigate(Screen.BookDetail.createRoute(bookKey)) },
                    onFavoriteClick = { book -> viewModel.toggleFavorite(book) },
                    onRetry = { viewModel.clearSearch() },
                    onRefresh = { viewModel.clearSearch() },
                    isRefreshing = isRefreshing,
                    onLoadMore = { viewModel.loadMoreBooks() },
                    hasMorePages = hasMorePages,
                    isLoadingMore = isLoadingMore
                )
                1 -> FavoritesTab(
                    books = favoriteBooks,
                    onBookClick = { bookKey -> navController.navigate(Screen.BookDetail.createRoute(bookKey)) },
                    onFavoriteClick = { book -> viewModel.toggleFavorite(book) },
                    onRefresh = { },
                    isRefreshing = isRefreshing
                )
            }
        }
    }
}
