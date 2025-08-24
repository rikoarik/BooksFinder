package com.app.bookfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.bookfinder.R
import com.app.bookfinder.data.local.BookDatabase
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.data.model.SearchResponse
import com.app.bookfinder.data.model.WorkDetail
import com.app.bookfinder.data.remote.OpenLibraryApi
import com.app.bookfinder.data.repository.BookRepository
import com.app.bookfinder.ui.components.FilterSection
import com.app.bookfinder.ui.components.SearchBar
import com.app.bookfinder.ui.theme.BookFinderTheme
import com.app.bookfinder.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BookViewModel,
    onBookClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val books by viewModel.books.collectAsStateWithLifecycle()
    val favoriteBooks by viewModel.favoriteBooks.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val hasMorePages by viewModel.hasMorePages.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val isLoadingSort by viewModel.isLoadingSort.collectAsStateWithLifecycle()
    val isLoadingInitial by viewModel.isLoadingInitial.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoadingInitial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
                    Text(
                        text = "Loading books...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                                )
                            )
                        )
                )

                Column(modifier = Modifier.fillMaxSize()) {
                    // Top app bar
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            )
                        },
                        actions = {
                            IconButton(onClick = onSettingsClick) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = stringResource(R.string.settings)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Search bar
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.updateSearchQuery(it) },
                            onSearch = { query ->
                                if (query.isNotEmpty()) viewModel.searchBooks(query)
                            },
                            onClear = { viewModel.clearSearch() }
                        )
                    }

                    // Filter section
                    FilterSection(
                        selectedSort = selectedSort,
                        onSortChange = { viewModel.setSort(it) },
                        isLoading = isSearching || isLoadingSort
                    )

                    when {
                        // Searching state
                        isSearching && searchQuery.isNotEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        strokeWidth = 4.dp
                                    )
                                    Text(
                                        text = "Searching for books...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Loading sort state
                        isLoadingSort -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        strokeWidth = 4.dp
                                    )
                                    Text(
                                        text = "Loading books...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Search results
                        searchQuery.isNotEmpty() -> {
                            BookListTab(
                                books = books,
                                onBookClick = { workId ->
                                    val cleanId = workId.removePrefix("/")
                                    onBookClick(cleanId)
                                },
                                onFavoriteClick = { viewModel.toggleFavorite(it) },
                                onRetry = { viewModel.retrySearch() },
                                onRefresh = { viewModel.refreshData() },
                                isRefreshing = isRefreshing,
                                onLoadMore = { viewModel.loadMoreBooks() },
                                hasMorePages = hasMorePages
                            )
                        }

                        else -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                TabRow(
                                    selectedTabIndex = selectedTabIndex,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    indicator = { tabPositions ->
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                            height = 3.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                ) {
                                    Tab(
                                        selected = selectedTabIndex == 0,
                                        onClick = { selectedTabIndex = 0 },
                                        text = { Text(stringResource(R.string.nav_search)) },
                                        icon = { Icon(Icons.Default.Search, null) }
                                    )
                                    Tab(
                                        selected = selectedTabIndex == 1,
                                        onClick = { selectedTabIndex = 1 },
                                        text = { Text(stringResource(R.string.nav_favorites)) },
                                        icon = { Icon(Icons.Default.Favorite, null) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            when (selectedTabIndex) {
                                0 -> BookListTab(
                                    books = books,
                                    onBookClick = { workId ->
                                        println("DEBUG: HomeScreen - Tab 0 Book clicked: $workId")
                                        onBookClick(workId)
                                    },
                                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                                    onRetry = { viewModel.retrySearch() },
                                    onRefresh = { viewModel.refreshData() },
                                    isRefreshing = isRefreshing,
                                    onLoadMore = { viewModel.loadMoreBooks() },
                                    hasMorePages = hasMorePages
                                )
                                1 -> FavoritesTab(
                                    books = favoriteBooks,
                                    onBookClick = { workId ->
                                        println("DEBUG: HomeScreen - Tab 1 Book clicked: $workId")
                                        onBookClick(workId)
                                    },
                                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                                    onRefresh = { viewModel.refreshData() },
                                    isRefreshing = isRefreshing
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val bookDao = BookDatabase.getDatabase(context).bookDao()
    val bookRepository = BookRepository(OpenLibraryApiFake(), bookDao)
    val viewModel: BookViewModel = viewModel {
        BookViewModel(bookRepository)
    }
    BookFinderTheme {
        HomeScreen(
            viewModel = viewModel,
            onBookClick = {},
            onSettingsClick = {}
        )
    }
}

class OpenLibraryApiFake : OpenLibraryApi {
    override suspend fun searchBooks(
        query: String,
        fields: String,
        limit: Int,
        page: Int,
        language: String?,
        sort: String?
    ): SearchResponse {
        return SearchResponse(docs = emptyList(), numFound = 0, start = 0)
    }


    override suspend fun getWorkDetail(workId: String): WorkDetail {
        return WorkDetail(
            key = "key",
            title = "title",
            authors = emptyList(),
            firstPublishDate = "2023",
            publishers = emptyList(),
            isbn10 = emptyList(),
            isbn13 = emptyList(),
            coverIds = emptyList(),
            description = "description",
            subjects = emptyList()
        )
    }
}