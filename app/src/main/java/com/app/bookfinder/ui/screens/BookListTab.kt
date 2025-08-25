package com.app.bookfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.ui.components.*
import androidx.compose.ui.res.stringResource
import com.app.bookfinder.R
import com.app.bookfinder.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun BookListTab(
    books: Resource<List<Book>>,
    onBookClick: (String) -> Unit,
    onFavoriteClick: (Book) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    onLoadMore: () -> Unit,
    hasMorePages: Boolean = false
) {
    val listState = rememberLazyListState()
    var isLoadingMore by remember { mutableStateOf(false) }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = when (books) {
                is Resource.Success -> books.data.size
                else -> 0
            }

            val shouldLoad = lastVisibleItem != null &&
                    lastVisibleItem.index >= totalItems - 3 &&
                    totalItems > 0 &&
                    hasMorePages &&
                    !isLoadingMore

            shouldLoad
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            isLoadingMore = true
            onLoadMore()
            // Reset loading state after a delay
            delay(300)
            isLoadingMore = false
        }
    }

    when (books) {
        is Resource.Loading -> {
            LoadingState()
        }
        is Resource.Success -> {
            if (books.data.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.state_empty_search),
                    onRefresh = onRefresh,
                    isRefreshing = isRefreshing
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books.data) { book ->
                        BookCard(
                            book = book,
                            onBookClick = {
                                onBookClick(book.key)
                            },
                            onFavoriteClick = onFavoriteClick
                        )
                    }

                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        strokeWidth = 3.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Loading more books...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        is Resource.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorState(
                    message = books.message,
                    onRetry = onRetry,
                    onRefresh = onRefresh,
                    isRefreshing = isRefreshing
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookListTabLoadingPreview() {
    MaterialTheme {
        BookListTab(
            books = Resource.Loading,
            onBookClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {},
            isRefreshing = false,
            onLoadMore = {},
            hasMorePages = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookListTabEmptyPreview() {
    MaterialTheme {
        BookListTab(
            books = Resource.Success(emptyList()),
            onBookClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {},
            isRefreshing = false,
            onLoadMore = {},
            hasMorePages = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookListTabErrorPreview() {
    MaterialTheme {
        BookListTab(
            books = Resource.Error("Failed to load books"),
            onBookClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {},
            isRefreshing = false,
            onLoadMore = {},
            hasMorePages = true
        )
    }
}
