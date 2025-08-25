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
    val isFavorite: Boolean = false,
    val tableOfContents: List<String> = emptyList(),
    val editionName: String? = null,
    val numberOfPages: Int? = null,
    val format: String? = null,
    val deweyDecimalClass: List<String> = emptyList(),
    val lcClassifications: List<String> = emptyList(),
    val ocaid: String? = null,
    val lccn: List<String> = emptyList(),
    val oclcNumbers: List<String> = emptyList(),
    val publishPlaces: List<String> = emptyList(),
    val copyrightDate: String? = null,
    val workTitles: List<String> = emptyList(),
    val subjectPlaces: List<String> = emptyList(),
    val subjectPeople: List<String> = emptyList(),
    val subjectTimes: List<String> = emptyList(),
    val location: String? = null,
    val latestRevision: Int? = null,
    val revision: Int? = null,
    val created: String? = null,
    val lastModified: String? = null,
    val authors: List<Author> = emptyList()
) {
    fun getAuthorDisplayName(): String {
        return if (authorNames.isNotEmpty()) {
            authorNames.filter { !it.isNullOrBlank() }.joinToString(", ")
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
    
    fun getFormatDisplay(): String {
        return format?.takeIf { it.isNotBlank() } ?: "Unknown Format"
    }
    
    fun getPagesDisplay(): String {
        return numberOfPages?.takeIf { it > 0 }?.let { "$it pages" } ?: "Unknown pages"
    }
    
    fun getPublishPlacesDisplay(): String {
        return if (publishPlaces.isNotEmpty()) {
            publishPlaces.filter { !it.isNullOrBlank() }.joinToString(", ")
        } else {
            "Unknown location"
        }
    }
    
    fun getClassificationsDisplay(): String {
        val classifications = mutableListOf<String>()
        
        if (deweyDecimalClass.isNotEmpty()) {
            val dewey = deweyDecimalClass.filter { !it.isNullOrBlank() }
            if (dewey.isNotEmpty()) {
                classifications.add("Dewey: ${dewey.joinToString(", ")}")
            }
        }
        
        if (lcClassifications.isNotEmpty()) {
            val lc = lcClassifications.filter { !it.isNullOrBlank() }
            if (lc.isNotEmpty()) {
                classifications.add("LC: ${lc.joinToString(", ")}")
            }
        }
        
        return if (classifications.isNotEmpty()) {
            classifications.joinToString(" | ")
        } else {
            "No classifications available"
        }
    }
    
    fun getSubjectPlacesDisplay(): String {
        return if (subjectPlaces.isNotEmpty()) {
            subjectPlaces.filter { !it.isNullOrBlank() }.joinToString(", ")
        } else {
            "No specific places mentioned"
        }
    }
    
    fun getSubjectPeopleDisplay(): String {
        return if (subjectPeople.isNotEmpty()) {
            subjectPeople.filter { !it.isNullOrBlank() }.joinToString(", ")
        } else {
            "No specific people mentioned"
        }
    }
    
    fun getSubjectTimesDisplay(): String {
        return if (subjectTimes.isNotEmpty()) {
            subjectTimes.filter { !it.isNullOrBlank() }.joinToString(", ")
        } else {
            "No specific time period mentioned"
        }
    }
    
    fun getLocationDisplay(): String {
        return location?.takeIf { it.isNotBlank() } ?: "No location specified"
    }
    
    fun getRevisionInfo(): String {
        return if (revision != null && latestRevision != null) {
            "Revision $revision of $latestRevision"
        } else if (revision != null) {
            "Revision $revision"
        } else {
            "No revision info"
        }
    }
    
    fun hasAdditionalInfo(): Boolean {
        return !description.isNullOrBlank() ||
               !publisher.isNullOrBlank() ||
               !isbn.isNullOrBlank() ||
               format != null ||
               numberOfPages != null ||
               publishPlaces.isNotEmpty() ||
               !copyrightDate.isNullOrBlank() ||
               deweyDecimalClass.isNotEmpty() ||
               lcClassifications.isNotEmpty() ||
               tableOfContents.isNotEmpty() ||
               workTitles.isNotEmpty() ||
               subjects.isNotEmpty() ||
               subjectPlaces.isNotEmpty() ||
               subjectPeople.isNotEmpty() ||
               subjectTimes.isNotEmpty() ||
               !location.isNullOrBlank()
    }
}
