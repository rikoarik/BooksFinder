package com.app.bookfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.bookfinder.R
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.ui.components.*

@Composable
fun FavoritesTab(
    books: List<Book>,
    onBookClick: (String) -> Unit,
    onFavoriteClick: (Book) -> Unit,
    onRefresh: () -> Unit = {},
    isRefreshing: Boolean = false
) {
    if (books.isEmpty()) {
        EmptyState(
            message = stringResource(R.string.state_empty_favorites),
            onRefresh = onRefresh,
            isRefreshing = isRefreshing
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(books) { book ->
                BookCard(
                    book = book,
                    onBookClick = { onBookClick(book.key) },
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesTabEmptyPreview() {
    MaterialTheme {
        FavoritesTab(
            books = emptyList(),
            onBookClick = {},
            onFavoriteClick = {},
            onRefresh = {},
            isRefreshing = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesTabWithBooksPreview() {
    MaterialTheme {
        FavoritesTab(
            books = listOf(
                Book(
                    key = "works/OL123456W",
                    title = "Android Programming: The Big Nerd Ranch Guide",
                    authorNames = listOf("Bill Phillips", "Chris Stewart"),
                    firstPublishedYear = 2019,
                    publisher = "Big Nerd Ranch Guides",
                    isbn = "9780134706056",
                    coverId = 123456,
                    subjects = listOf("Android", "Programming", "Mobile Development"),
                    isFavorite = true
                ),
                Book(
                    key = "works/OL789012W",
                    title = "Kotlin in Action",
                    authorNames = listOf("Dmitry Jemerov", "Svetlana Isakova"),
                    firstPublishedYear = 2017,
                    publisher = "Manning Publications",
                    isbn = "9781617293290",
                    coverId = 789012,
                    subjects = listOf("Kotlin", "Programming", "JVM"),
                    isFavorite = true
                )
            ),
            onBookClick = {},
            onFavoriteClick = {},
            onRefresh = {},
            isRefreshing = false
        )
    }
}
