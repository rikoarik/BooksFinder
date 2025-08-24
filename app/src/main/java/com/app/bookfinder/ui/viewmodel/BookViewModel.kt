package com.app.bookfinder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("android")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _books = MutableStateFlow<Resource<List<Book>>>(Resource.Loading)
    val books: StateFlow<Resource<List<Book>>> = _books.asStateFlow()
    
    private val _favoriteBooks = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooks: StateFlow<List<Book>> = _favoriteBooks.asStateFlow()
    
    init {
        searchBooks("android")
        loadFavoriteBooks()
    }
    
    fun searchBooks(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            repository.searchBooks(query).collect { result ->
                _books.value = result
            }
        }
    }
    
    fun loadFavoriteBooks() {
        viewModelScope.launch {
            repository.getFavoriteBooks().collect { books ->
                _favoriteBooks.value = books
            }
        }
    }
    
    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            repository.toggleFavorite(book)
            loadFavoriteBooks()
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _books.value = Resource.Loading
    }
}
