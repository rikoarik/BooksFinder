package com.app.bookfinder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val key: String,
    val title: String,
    val authorNames: List<String> = emptyList(),
    val firstPublishedYear: Int? = null,
    val publisher: String? = null,
    val isbn: String? = null,
    val coverId: Int? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val subjects: List<String> = emptyList(),
    val isFavorite: Boolean = false
) {
    fun getAuthorDisplayName(): String {
        return if (authorNames.isNotEmpty()) {
            authorNames.joinToString(", ")
        } else {
            "Unknown Author"
        }
    }
    
    fun getCoverImageUrl(): String {
        return coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" } ?: ""
    }
    
    fun getYearDisplay(): String {
        return firstPublishedYear?.toString() ?: "Unknown Year"
    }
}
