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

class BookDetailViewModel(
    private val repository: BookRepository,
    private val workId: String
) : ViewModel() {
    
    private val _book = MutableStateFlow<Resource<Book>>(Resource.Loading)
    val book: StateFlow<Resource<Book>> = _book.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    init {
        println("DEBUG: BookDetailViewModel initialized with workId: $workId")
        loadBookDetail()
    }
    
    fun loadBookDetail() {
        println("DEBUG: Loading book detail for workId: $workId")
        viewModelScope.launch {
            try {
                repository.getWorkDetail(workId).collect { result ->
                    println("DEBUG: Book detail result: $result")
                    _book.value = result
                    if (result is Resource.Success) {
                        checkFavoriteStatus(result.data.key)
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: Error loading book detail: ${e.message}")
                val errorMessage = when (e) {
                    is java.net.SocketTimeoutException -> "Koneksi internet terlalu lambat. Silakan coba lagi."
                    is java.net.UnknownHostException -> "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                    is java.net.ConnectException -> "Server sedang tidak tersedia. Silakan coba beberapa saat lagi."
                    else -> "Gagal memuat detail buku. Silakan coba lagi. Error: ${e.message}"
                }
                _book.value = Resource.Error(errorMessage)
            }
        }
    }
    
    private suspend fun checkFavoriteStatus(bookKey: String) {
        val isFav = repository.isBookFavorite(bookKey)
        _isFavorite.value = isFav
        println("DEBUG: Favorite status for $bookKey: $isFav")
    }
    
    fun toggleFavorite() {
        viewModelScope.launch {
            val currentBook = _book.value
            if (currentBook is Resource.Success) {
                repository.toggleFavorite(currentBook.data)
                _isFavorite.value = !_isFavorite.value
                println("DEBUG: Toggled favorite for ${currentBook.data.title}")
            }
        }
    }
}
