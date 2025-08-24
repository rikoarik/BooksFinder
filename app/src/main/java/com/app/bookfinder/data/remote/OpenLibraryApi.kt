package com.app.bookfinder.data.remote

import com.app.bookfinder.data.model.SearchResponse
import com.app.bookfinder.data.model.WorkDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {
    
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("fields") fields: String = "key,title,author_name,first_publish_year,publisher,isbn,cover_i,subject",
        @Query("limit") limit: Int = 20
    ): SearchResponse
    
    @GET("works/{workId}.json")
    suspend fun getWorkDetail(
        @Path("workId") workId: String
    ): WorkDetail
}
