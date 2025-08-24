package com.app.bookfinder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.bookfinder.data.repository.BookRepository

class BookDetailViewModelFactory(
    private val repository: BookRepository,
    private val workId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookDetailViewModel(repository, workId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
