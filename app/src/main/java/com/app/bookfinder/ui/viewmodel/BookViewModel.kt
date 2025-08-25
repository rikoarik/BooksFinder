package com.app.bookfinder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.data.repository.BookRepository
import com.app.bookfinder.ui.components.LanguageOption
import com.app.bookfinder.ui.components.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _books = MutableStateFlow<Resource<List<Book>>>(Resource.Loading)
    val books: StateFlow<Resource<List<Book>>> = _books.asStateFlow()

    private val _favoriteBooks = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooks: StateFlow<List<Book>> = _favoriteBooks.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _isLoadingSort = MutableStateFlow(false)
    val isLoadingSort: StateFlow<Boolean> = _isLoadingSort.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(LanguageOption.ALL)
    private val _selectedSort = MutableStateFlow(SortOption.RELEVANCE)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _lastSearchQuery = MutableStateFlow("")

    init {
        loadFavoriteBooks()
        loadInitialBooks()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _lastSearchQuery.value = ""
        loadBooksBySort(_selectedSort.value)
    }

    fun searchBooks(query: String) {
        if (query.isNotBlank()) {
            _searchQuery.value = query
            _lastSearchQuery.value = query
            executeSearch(query, _selectedSort.value)
        }
    }

    private fun executeSearch(query: String, sort: SortOption) {
        _isSearching.value = true
        _books.value = Resource.Loading
        _currentPage.value = 1
        _hasMorePages.value = true

        viewModelScope.launch {
            repository.searchBooks(query, 1, _selectedLanguage.value, sort)
                .collect { resource ->
                    if (resource is Resource.Success) {
                        _allBooks.value = resource.data
                        _books.value = resource
                        _hasMorePages.value = resource.data.isNotEmpty()
                        _currentPage.value = 1
                    } else {
                        _books.value = resource
                        _hasMorePages.value = false
                    }
                    _isSearching.value = false
                }
        }
    }

    fun loadMoreBooks() {
        if (_isLoadingMore.value || !_hasMorePages.value) return

        _isLoadingMore.value = true
        val nextPage = _currentPage.value + 1
        val query = _lastSearchQuery.value.ifBlank {
            when (_selectedSort.value) {
                SortOption.NEW -> "popular"
                SortOption.OLD -> "classic"
                SortOption.RANDOM -> "random"
                SortOption.KEY -> "popular"
                else -> "popular"
            }
        }

        viewModelScope.launch {
            repository.searchBooks(query, nextPage, _selectedLanguage.value, _selectedSort.value)
                .collect { resource ->
                    if (resource is Resource.Success) {
                        val currentBooks = (_books.value as? Resource.Success)?.data ?: emptyList()
                        val updatedBooks = currentBooks + resource.data
                        _books.value = Resource.Success(updatedBooks)
                        _currentPage.value = nextPage
                        _hasMorePages.value = resource.data.isNotEmpty()
                    } else {
                        _hasMorePages.value = false
                    }
                    _isLoadingMore.value = false
                }
        }
    }

    fun setSort(sort: SortOption) {
        _selectedSort.value = sort
        val query = _lastSearchQuery.value
        if (query.isNotEmpty()) executeSearch(query, sort)
        else loadBooksBySort(sort)
    }

    private fun loadBooksBySort(sort: SortOption) {
        _isLoadingSort.value = true
        _books.value = Resource.Loading
        _currentPage.value = 1
        _hasMorePages.value = true

        val defaultQuery = when (sort) {
            SortOption.NEW -> "popular"
            SortOption.OLD -> "classic"
            SortOption.RANDOM -> "random"
            SortOption.KEY -> "popular"
            else -> "popular"
        }
        _lastSearchQuery.value = defaultQuery

        viewModelScope.launch {
            repository.searchBooks(defaultQuery, 1, _selectedLanguage.value, sort)
                .collect { resource ->
                    if (resource is Resource.Success) {
                        _allBooks.value = resource.data
                        _books.value = resource
                        _hasMorePages.value = resource.data.isNotEmpty()
                        _currentPage.value = 1
                    } else {
                        _books.value = resource
                        _hasMorePages.value = false
                    }
                    _isLoadingSort.value = false
                }
        }
    }

    private fun loadInitialBooks() {
        _currentPage.value = 1
        _hasMorePages.value = true
        _lastSearchQuery.value = "popular"

        viewModelScope.launch {
            repository.searchBooks("popular", 1, _selectedLanguage.value, _selectedSort.value)
                .collect { resource ->
                    if (resource is Resource.Success) {
                        _allBooks.value = resource.data
                        _books.value = resource
                        _hasMorePages.value = resource.data.isNotEmpty()
                        _currentPage.value = 1
                    } else {
                        _books.value = resource
                        _hasMorePages.value = false
                    }
                }
        }
    }

    fun loadFavoriteBooks() {
        viewModelScope.launch {
            repository.getFavoriteBooks().collect { favs ->
                _favoriteBooks.value = favs
                refreshCurrentBooks()
            }
        }
    }

    private fun refreshCurrentBooks() {
        val current = _books.value
        if (current is Resource.Success) {
            val updated = current.data.map { book ->
                book.copy(isFavorite = _favoriteBooks.value.any { it.key == book.key })
            }
            _books.value = Resource.Success(updated)
        }
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            repository.toggleFavorite(book)
            loadFavoriteBooks()
        }
    }
}
