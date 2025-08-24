package com.app.bookfinder.data.model

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
    val coverIds: List<Int>? = null
)

data class Author(
    val author: AuthorDetail
)

data class AuthorDetail(
    val key: String,
    val name: String
)

data class Publisher(
    val name: String
)
