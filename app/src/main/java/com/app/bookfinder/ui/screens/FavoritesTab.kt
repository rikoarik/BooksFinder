package com.app.bookfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.ui.components.*

@Composable
fun FavoritesTab(
    books: List<Book>,
    onBookClick: (String) -> Unit,
    onFavoriteClick: (Book) -> Unit
) {
    if (books.isEmpty()) {
        EmptyState(
            message = "You haven't added any books to favorites yet. Search for books and tap the heart icon to add them!"
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
