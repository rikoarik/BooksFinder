package com.app.bookfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.ui.components.*

@Composable
fun SearchTab(
    books: Resource<List<Book>>,
    onBookClick: (String) -> Unit,
    onFavoriteClick: (Book) -> Unit,
    onRetry: () -> Unit
) {
    when (books) {
        is Resource.Loading -> {
            LoadingState()
        }
        is Resource.Success -> {
            if (books.data.isEmpty()) {
                EmptyState(
                    message = "No books found. Try searching for something else."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
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
                onRetry = onRetry
            )
        }
    }
}
