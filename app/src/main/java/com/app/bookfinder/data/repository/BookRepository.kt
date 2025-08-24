package com.app.bookfinder.data.repository

import com.app.bookfinder.data.local.BookDao
import com.app.bookfinder.data.model.*
import com.app.bookfinder.data.remote.OpenLibraryApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BookRepository(
    private val api: OpenLibraryApi,
    private val bookDao: BookDao
) {
    
    fun searchBooks(query: String): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.searchBooks(query)
            val books = response.docs.map { doc ->
                Book(
                    key = doc.key,
                    title = doc.title,
                    authorNames = doc.authorNames ?: emptyList(),
                    firstPublishedYear = doc.firstPublishedYear,
                    publisher = doc.publisher?.firstOrNull(),
                    isbn = doc.isbns?.firstOrNull(),
                    coverId = doc.coverId,
                    subjects = doc.subject ?: emptyList()
                )
            }
            emit(Resource.Success(books))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun getWorkDetail(workId: String): Flow<Resource<Book>> = flow {
        emit(Resource.Loading)
        try {
            val workDetail = api.getWorkDetail(workId)
            val book = Book(
                key = workDetail.key,
                title = workDetail.title,
                authorNames = workDetail.authors?.map { it.author.name } ?: emptyList(),
                firstPublishedYear = workDetail.firstPublishDate?.substringBefore("-")?.toIntOrNull(),
                publisher = workDetail.publishers?.firstOrNull()?.name,
                isbn = (workDetail.isbn13 ?: workDetail.isbn10)?.firstOrNull(),
                coverId = workDetail.coverIds?.firstOrNull(),
                description = workDetail.description,
                subjects = workDetail.subjects ?: emptyList()
            )
            emit(Resource.Success(book))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun getFavoriteBooks(): Flow<List<Book>> {
        return bookDao.getFavoriteBooks()
    }
    
    suspend fun toggleFavorite(book: Book) {
        val isFavorite = bookDao.isBookFavorite(book.key)
        val updatedBook = book.copy(isFavorite = !isFavorite)
        bookDao.insertBook(updatedBook)
    }
    
    suspend fun addToFavorites(book: Book) {
        val bookWithFavorite = book.copy(isFavorite = true)
        bookDao.insertBook(bookWithFavorite)
    }
    
    suspend fun removeFromFavorites(book: Book) {
        val bookWithoutFavorite = book.copy(isFavorite = false)
        bookDao.insertBook(bookWithoutFavorite)
    }
}
