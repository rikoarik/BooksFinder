package com.app.bookfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.ui.components.*

@Composable
fun SearchTab(
    books: Resource<List<Book>>,
    onBookClick: (String) -> Unit,
    onFavoriteClick: (Book) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    when (books) {
        is Resource.Loading -> LoadingState()
        is Resource.Success -> {
            if (books.data.isEmpty()) {
                EmptyState(
                    message = "No books found. Try searching for something else.",
                    onRefresh = onRefresh,
                    isRefreshing = isRefreshing
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books.data) { book ->
                        BookCard(
                            book = book,
                            onBookClick = { onBookClick(book.key) },
                            onFavoriteClick = onFavoriteClick
                        )
                    }
                }
            }
        }
        is Resource.Error -> {
            ErrorState(
                message = books.message,
                onRetry = onRetry,
                onRefresh = onRefresh,
                isRefreshing = isRefreshing
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchTabLoadingPreview() {
    MaterialTheme {
        SearchTab(
            books = Resource.Loading,
            onBookClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {},
            isRefreshing = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchTabEmptyPreview() {
    MaterialTheme {
        SearchTab(
            books = Resource.Success(emptyList()),
            onBookClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {},
            isRefreshing = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchTabErrorPreview() {
    MaterialTheme {
        SearchTab(
            books = Resource.Error("Failed to load books"),
            onBookClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {},
            isRefreshing = false
        )
    }
}
