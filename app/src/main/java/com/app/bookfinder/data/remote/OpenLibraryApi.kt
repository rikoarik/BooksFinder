package com.app.bookfinder.data.remote

import com.app.bookfinder.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {
    
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("fields") fields: String = "key,title,author_name,first_publish_year,publisher,isbn,cover_i,subject",
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1,
        @Query("language") language: String? = null,
        @Query("sort") sort: String? = null
    ): SearchResponse
    
    @GET("works/{workId}.json")
    suspend fun getWorkDetail(@Path("workId") workId: String): WorkDetail
    
    // Author search API - more efficient than individual calls
    @GET("search/authors.json")
    suspend fun searchAuthors(
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): AuthorSearchResponse
    
    // Batch author request using search API with key filter
    @GET("search/authors.json")
    suspend fun getAuthorsBatch(
        @Query("q") keyFilter: String, // e.g., "key:(/authors/OL23919A OR /authors/OL1394244A)"
        @Query("limit") limit: Int = 100
    ): AuthorSearchResponse
    
    // Individual author detail
    @GET("authors/{authorId}.json")
    suspend fun getAuthorDetail(@Path("authorId") authorId: String): AuthorDetailResponse
    
    // Works by an author
    @GET("authors/{authorId}/works.json")
    suspend fun getAuthorWorks(
        @Path("authorId") authorId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): AuthorWorksResponse
    
    @GET("subjects/{subjectId}.json")
    suspend fun getSubjectDetail(@Path("subjectId") subjectId: String): SubjectDetailResponse
    
    @GET("places/{placeId}.json")
    suspend fun getPlaceDetail(@Path("placeId") placeId: String): PlaceDetailResponse
    
    @GET("people/{personId}.json")
    suspend fun getPersonDetail(@Path("personId") personId: String): PersonDetailResponse
    
    @GET("times/{timeId}.json")
    suspend fun getTimeDetail(@Path("timeId") timeId: String): TimeDetailResponse
    
    @GET("publishers/{publisherId}.json")
    suspend fun getPublisherDetail(@Path("publisherId") publisherId: String): PublisherDetailResponse
    
    @GET("editions/{editionId}.json")
    suspend fun getEditionDetail(@Path("editionId") editionId: String): EditionDetailResponse
}
