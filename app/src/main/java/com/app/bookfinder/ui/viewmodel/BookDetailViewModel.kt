package com.app.bookfinder.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val workId: String = checkNotNull(savedStateHandle["workId"])
    
    private val _book = MutableStateFlow<Resource<Book>>(Resource.Loading)
    val book: StateFlow<Resource<Book>> = _book.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    init {
        loadBookDetail()
    }
    
    private fun loadBookDetail() {
        viewModelScope.launch {
            repository.getWorkDetail(workId).collect { result ->
                _book.value = result
                if (result is Resource.Success) {
                    checkFavoriteStatus(result.data.key)
                }
            }
        }
    }
    
    private suspend fun checkFavoriteStatus(bookKey: String) {
        val isFav = repository.isBookFavorite(bookKey)
        _isFavorite.value = isFav
    }
    
    fun toggleFavorite() {
        viewModelScope.launch {
            val currentBook = _book.value
            if (currentBook is Resource.Success) {
                repository.toggleFavorite(currentBook.data)
                _isFavorite.value = !_isFavorite.value
            }
        }
    }
}
