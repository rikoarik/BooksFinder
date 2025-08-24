package com.app.bookfinder.data.local

import androidx.room.*
import com.app.bookfinder.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    
    @Query("SELECT * FROM books WHERE isFavorite = 1")
    fun getFavoriteBooks(): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR authorNames LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<Book>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)
    
    @Delete
    suspend fun deleteBook(book: Book)
    
    @Query("UPDATE books SET isFavorite = :isFavorite WHERE key = :bookKey")
    suspend fun updateFavoriteStatus(bookKey: String, isFavorite: Boolean)
    
    @Query("SELECT EXISTS(SELECT 1 FROM books WHERE key = :bookKey AND isFavorite = 1)")
    suspend fun isBookFavorite(bookKey: String): Boolean
}
