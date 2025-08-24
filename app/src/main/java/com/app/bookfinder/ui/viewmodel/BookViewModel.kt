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

    private val _lastSearchQuery = MutableStateFlow("")
    private val _currentPage = MutableStateFlow(1)
    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(LanguageOption.ALL)

    private val _selectedSort = MutableStateFlow(SortOption.RELEVANCE)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())

    private val _isLoadingInitial = MutableStateFlow(true)
    val isLoadingInitial: StateFlow<Boolean> = _isLoadingInitial.asStateFlow()

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
            try {
                repository.searchBooks(query, 1, _selectedLanguage.value, sort)
                    .collect { resource ->
                        if (resource is Resource.Success) {
                            _allBooks.value = resource.data
                            _books.value = resource
                            _hasMorePages.value = resource.data.size >= 20
                        } else {
                            _books.value = resource
                            _hasMorePages.value = false
                        }
                    }
            } catch (e: Exception) {
                _books.value = Resource.Error(e.message ?: "Search failed")
                _hasMorePages.value = false
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun loadMoreBooks() {
        if (!_hasMorePages.value) {
            println("DEBUG: loadMoreBooks called but hasMorePages is false")
            return
        }
        
        println("DEBUG: loadMoreBooks called - currentPage: ${_currentPage.value}, hasMorePages: ${_hasMorePages.value}")
        
        viewModelScope.launch {
            try {
                val query = _lastSearchQuery.value
                val nextPage = _currentPage.value + 1
                
                println("DEBUG: Loading page $nextPage, query: '$query'")
                
                if (query.isNotEmpty()) {
                    // Load more for search results
                    _currentPage.value = nextPage
                    repository.searchBooks(query, nextPage, _selectedLanguage.value, _selectedSort.value)
                        .collect { resource ->
                            if (resource is Resource.Success) {
                                val updated = _allBooks.value + resource.data
                                _allBooks.value = updated
                                _books.value = Resource.Success(updated)
                                _hasMorePages.value = resource.data.size >= 20
                                println("DEBUG: Search load more success - new total: ${updated.size}, hasMorePages: ${_hasMorePages.value}")
                            } else {
                                _hasMorePages.value = false
                                println("DEBUG: Search load more failed")
                            }
                        }
                } else {
                    // Load more for initial books or books by sort
                    val sortQuery = when (_selectedSort.value) {
                        SortOption.NEW -> "popular"
                        SortOption.OLD -> "classic"
                        SortOption.RANDOM -> "random"
                        SortOption.KEY -> "popular"
                        else -> "popular"
                    }
                    
                    _currentPage.value = nextPage
                    repository.searchBooks(sortQuery, nextPage, _selectedLanguage.value, _selectedSort.value)
                        .collect { resource ->
                            if (resource is Resource.Success) {
                                val updated = _allBooks.value + resource.data
                                _allBooks.value = updated
                                _books.value = Resource.Success(updated)
                                _hasMorePages.value = resource.data.size >= 20
                                println("DEBUG: Sort load more success - new total: ${updated.size}, hasMorePages: ${_hasMorePages.value}")
                            } else {
                                _hasMorePages.value = false
                                println("DEBUG: Sort load more failed")
                            }
                        }
                }
            } catch (e: Exception) {
                _hasMorePages.value = false
                println("DEBUG: Load more error: ${e.message}")
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

        viewModelScope.launch {
            try {
                val query = when (sort) {
                    SortOption.NEW -> "popular"
                    SortOption.OLD -> "classic"
                    SortOption.RANDOM -> "random"
                    SortOption.KEY -> "popular"
                    else -> "popular"
                }

                repository.searchBooks(query, 1, _selectedLanguage.value, sort)
                    .collect { resource ->
                        if (resource is Resource.Success) {
                            _allBooks.value = resource.data
                            _books.value = resource
                            _hasMorePages.value = resource.data.size >= 20
                        } else {
                            _books.value = resource
                            _hasMorePages.value = false
                        }
                    }
            } catch (e: Exception) {
                _books.value = Resource.Error(e.message ?: "Failed to load books")
                _hasMorePages.value = false
            } finally {
                _isLoadingSort.value = false
            }
        }
    }

    fun retrySearch() {
        val query = _lastSearchQuery.value
        if (query.isNotEmpty()) executeSearch(query, _selectedSort.value)
        else loadBooksBySort(_selectedSort.value)
    }

    fun refreshData() {
        _isRefreshing.value = true
        viewModelScope.launch {
            try {
                val query = _lastSearchQuery.value
                if (query.isNotEmpty()) {
                    _currentPage.value = 1
                    _hasMorePages.value = true
                    executeSearch(query, _selectedSort.value)
                } else {
                    loadBooksBySort(_selectedSort.value)
                }
                loadFavoriteBooks()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun loadInitialBooks() {
        viewModelScope.launch {
            _isLoadingInitial.value = true
            try {
                repository.searchBooks("popular", 1, _selectedLanguage.value, _selectedSort.value)
                    .collect { resource ->
                        if (resource is Resource.Success) {
                            _allBooks.value = resource.data
                            _books.value = if (resource.data.isNotEmpty()) resource else Resource.Success(emptyList())
                            _hasMorePages.value = resource.data.size >= 20
                        } else {
                            _books.value = resource
                            _hasMorePages.value = false
                        }
                    }
            } catch (_: Exception) {
                _books.value = Resource.Success(emptyList())
                _hasMorePages.value = false
            } finally {
                _isLoadingInitial.value = false
            }
        }
    }

    fun loadFavoriteBooks() {
        viewModelScope.launch {
            try {
                repository.getFavoriteBooks().collect { favs ->
                    _favoriteBooks.value = favs
                    refreshCurrentBooks()
                }
            } catch (_: Exception) {
                _favoriteBooks.value = emptyList()
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
            try {
                repository.toggleFavorite(book)
                loadFavoriteBooks()
            } catch (_: Exception) {}
        }
    }
}
