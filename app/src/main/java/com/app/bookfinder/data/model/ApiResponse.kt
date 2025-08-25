package com.app.bookfinder.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<BookDoc>
)

data class BookDoc(
    val key: String,
    val title: String,
    @SerializedName("author_name")
    val authorNames: List<String>? = null,
    @SerializedName("first_publish_year")
    val firstPublishedYear: Int? = null,
    val publisher: List<String>? = null,
    @SerializedName("isbn")
    val isbns: List<String>? = null,
    @SerializedName("cover_i")
    val coverId: Int? = null,
    val subject: List<String>? = null
)

data class WorkDetail(
    val key: String,
    val title: String,
    val description: String?,
    @SerializedName("authors")
    val authors: List<Author>? = null,
    @SerializedName("first_publish_date")
    val firstPublishDate: String? = null,
    val publishers: List<Publisher>? = null,
    @SerializedName("isbn_13")
    val isbn13: List<String>? = null,
    @SerializedName("isbn_10")
    val isbn10: List<String>? = null,
    val subjects: List<String>? = null,
    @SerializedName("covers")
    val coverIds: List<Int>? = null,
    @SerializedName("table_of_contents")
    val tableOfContents: List<String>? = null,
    @SerializedName("edition_name")
    val editionName: String? = null,
    @SerializedName("number_of_pages")
    val numberOfPages: Int? = null,
    val format: String? = null,
    @SerializedName("dewey_decimal_class")
    val deweyDecimalClass: List<String>? = null,
    @SerializedName("lc_classifications")
    val lcClassifications: List<String>? = null,
    @SerializedName("ocaid")
    val ocaid: String? = null,
    @SerializedName("lccn")
    val lccn: List<String>? = null,
    @SerializedName("oclc_numbers")
    val oclcNumbers: List<String>? = null,
    @SerializedName("publish_places")
    val publishPlaces: List<String>? = null,
    @SerializedName("copyright_date")
    val copyrightDate: String? = null,
    @SerializedName("work_titles")
    val workTitles: List<String>? = null,
    @SerializedName("subject_places")
    val subjectPlaces: List<String>? = null,
    @SerializedName("subject_people")
    val subjectPeople: List<String>? = null,
    @SerializedName("subject_times")
    val subjectTimes: List<String>? = null,
    val location: String? = null,
    @SerializedName("latest_revision")
    val latestRevision: Int? = null,
    val revision: Int? = null,
    val created: DateTimeInfo? = null,
    @SerializedName("last_modified")
    val lastModified: DateTimeInfo? = null
)

data class Author(
    val author: AuthorDetail
)

data class AuthorDetail(
    val key: String,
    val name: String? = null
)

data class Publisher(
    val name: String
)

data class DateTimeInfo(
    val type: String,
    val value: String
) {
    fun getFormattedDate(): String {
        return try {
            val instant = java.time.Instant.parse(value)
            val localDateTime = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
            localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"))
        } catch (e: Exception) {
            value
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun getDateOnly(): String {
        return try {
            val instant = java.time.Instant.parse(value)
            val localDate = java.time.LocalDate.ofInstant(instant, java.time.ZoneId.systemDefault())
            localDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        } catch (e: Exception) {
            value.substringBefore("T")
        }
    }
}

data class AuthorDetailResponse(
    val key: String,
    val name: String?,
    val birth_date: String?,
    val death_date: String?,
    val bio: String?,
    val alternate_names: List<String>? = emptyList(),
    val links: List<AuthorLink>? = emptyList(),
    val photos: List<Int>? = emptyList(),
    val type: String?,
    val revision: Int?,
    val latest_revision: Int?,
    val created: DateTimeInfo?,
    val last_modified: DateTimeInfo?
)

data class AuthorLink(
    val title: String,
    val url: String
)

data class SubjectDetailResponse(
    val key: String,
    val name: String?,
    val type: String?,
    val works: List<SubjectWork>? = emptyList(),
    val authors: List<SubjectAuthor>? = emptyList(),
    val description: String?,
    val revision: Int?,
    val latest_revision: Int?,
    val created: DateTimeInfo?,
    val last_modified: DateTimeInfo?
)

data class SubjectWork(
    val key: String,
    val title: String?,
    val author: String?,
    val cover_id: Int?
)

data class SubjectAuthor(
    val key: String,
    val name: String?
)

data class PlaceDetailResponse(
    val key: String,
    val name: String?,
    val type: String?,
    val description: String?,
    val coordinates: List<Double>? = emptyList(),
    val revision: Int?,
    val latest_revision: Int?,
    val created: DateTimeInfo?,
    val last_modified: DateTimeInfo?
)

data class PersonDetailResponse(
    val key: String,
    val name: String?,
    val type: String?,
    val birth_date: String?,
    val death_date: String?,
    val bio: String?,
    val revision: Int?,
    val latest_revision: Int?,
    val created: DateTimeInfo?,
    val last_modified: DateTimeInfo?
)

data class TimeDetailResponse(
    val key: String,
    val name: String?,
    val type: String?,
    val description: String?,
    val revision: Int?,
    val latest_revision: Int?,
    val created: DateTimeInfo?,
    val last_modified: DateTimeInfo?
)

data class PublisherDetailResponse(
    val key: String,
    val name: String?,
    val type: String?,
    val description: String?,
    val revision: Int?,
    val latest_revision: Int?,
    val created: DateTimeInfo?,
    val last_modified: DateTimeInfo?
)

data class EditionDetailResponse(
    val key: String,
    val title: String?,
    val type: String?,
    val authors: List<Author>? = emptyList(),
    val description: String?,
    val revision: Int?,
    val latest_revision: Int?,
    val created: DateTimeInfo?,
    val last_modified: DateTimeInfo?
)

data class ReferencedData(
    val authors: MutableMap<String, AuthorDetailResponse> = mutableMapOf(),
    val subjects: MutableMap<String, SubjectDetailResponse> = mutableMapOf(),
    val places: MutableMap<String, PlaceDetailResponse> = mutableMapOf(),
    val people: MutableMap<String, PersonDetailResponse> = mutableMapOf(),
    val times: MutableMap<String, TimeDetailResponse> = mutableMapOf(),
    val publishers: MutableMap<String, PublisherDetailResponse> = mutableMapOf(),
    val editions: MutableMap<String, EditionDetailResponse> = mutableMapOf()
)

data class AuthorSearchResponse(
    val numFound: Int,
    val start: Int,
    val numFoundExact: Boolean,
    val docs: List<AuthorSearchDoc>
)

data class AuthorSearchDoc(
    val key: String,
    val text: List<String>?,
    val type: String,
    val name: String,
    val alternate_names: List<String>?,
    val birth_date: String?,
    val top_work: String?,
    val work_count: Int?,
    val top_subjects: List<String>?,
    val _version_: Long?
)

data class AuthorWorksResponse(
    val links: List<AuthorWorkLink>?,
    val size: Int?,
    val entries: List<AuthorWorkEntry>?
)

data class AuthorWorkLink(
    val title: String,
    val url: String,
    val type: String
)

data class AuthorWorkEntry(
    val type: String,
    val title: String,
    val key: String,
    val authors: List<AuthorWorkAuthor>?,
    val subjects: List<String>?,
    val first_publish_date: String?,
    val covers: List<Int>?
)

data class AuthorWorkAuthor(
    val author: AuthorDetail,
    val type: String
)
