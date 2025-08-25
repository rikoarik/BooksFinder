package com.app.bookfinder.data.repository

import com.app.bookfinder.data.local.BookDao
import com.app.bookfinder.data.model.*
import com.app.bookfinder.data.remote.OpenLibraryApi
import com.app.bookfinder.ui.components.LanguageOption
import com.app.bookfinder.ui.components.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.awaitAll

class BookRepository(
    private val api: OpenLibraryApi,
    private val bookDao: BookDao
) {
    // Simple in-memory cache to avoid repeated API calls
    private val authorCache = mutableMapOf<String, AuthorDetailResponse>()
    private val subjectCache = mutableMapOf<String, SubjectDetailResponse>()
    private val placeCache = mutableMapOf<String, PlaceDetailResponse>()
    private val personCache = mutableMapOf<String, PersonDetailResponse>()
    private val timeCache = mutableMapOf<String, TimeDetailResponse>()
    private val publisherCache = mutableMapOf<String, PublisherDetailResponse>()
    
    // Cache TTL: 5 minutes
    private val cacheExpiry = 5 * 60 * 1000L
    private val cacheTimestamps = mutableMapOf<String, Long>()
    
    private fun isCacheValid(key: String): Boolean {
        val timestamp = cacheTimestamps[key] ?: return false
        return System.currentTimeMillis() - timestamp < cacheExpiry
    }
    
    private fun updateCache(key: String) {
        cacheTimestamps[key] = System.currentTimeMillis()
    }
    
    // Helper function to extract key from URL
    private fun extractKeyFromUrl(url: String): String {
        return url.removePrefix("/").removeSuffix(".json")
    }
    
    // Helper function to fetch all referenced data
    private suspend fun fetchReferencedData(workDetail: WorkDetail): ReferencedData {
        val referencedData = ReferencedData()
        var completedItems = 0
        val totalItems = (workDetail.authors?.size ?: 0) + 
                        (workDetail.subjects?.size ?: 0) + 
                        (workDetail.subjectPlaces?.size ?: 0) + 
                        (workDetail.subjectPeople?.size ?: 0) + 
                        (workDetail.subjectTimes?.size ?: 0) + 
                        (workDetail.publishers?.size ?: 0)
        
        if (totalItems == 0) return referencedData
        
        // Use coroutineScope to properly handle async operations
        return kotlinx.coroutines.coroutineScope {
            val deferredCalls = mutableListOf<kotlinx.coroutines.Deferred<Unit>>()
            
            // OPTIMIZATION: Fetch all authors in one batch request instead of individual calls
            if (workDetail.authors?.isNotEmpty() == true) {
                val deferred = async {
                    try {
                        // Build key filter for batch request: "key:(/authors/OL23919A OR /authors/OL1394244A)"
                        val authorKeys = workDetail.authors.map { author ->
                            val authorKey = extractKeyFromUrl(author.author.key)
                            "/authors/$authorKey"
                        }
                        
                        if (authorKeys.isNotEmpty()) {
                            val keyFilter = "key:(${authorKeys.joinToString(" OR ")})"
                            
                            val authorBatch = api.getAuthorsBatch(keyFilter, authorKeys.size)
                            
                            // Map batch response to individual authors
                            authorBatch.docs.forEach { authorDoc ->
                                val authorKey = authorDoc.key.substringAfter("/authors/")
                                
                                val authorDetail = AuthorDetailResponse(
                                    key = authorKey,
                                    name = authorDoc.name,
                                    birth_date = authorDoc.birth_date,
                                    death_date = null,
                                    bio = null,
                                    alternate_names = authorDoc.alternate_names,
                                    links = emptyList(),
                                    photos = emptyList(),
                                    type = null,
                                    revision = null,
                                    latest_revision = null,
                                    created = null,
                                    last_modified = null
                                )
                                
                                // Update cache
                                authorCache[authorKey] = authorDetail
                                updateCache(authorKey)
                                referencedData.authors[authorKey] = authorDetail
                            }
                        }
                        completedItems += workDetail.authors.size
                    } catch (e: Exception) {
                        // Fallback to individual calls if batch fails
                        workDetail.authors.forEach { author ->
                            val authorKey = extractKeyFromUrl(author.author.key)
                            try {
                                // Check cache first
                                if (authorCache.containsKey(authorKey) && isCacheValid(authorKey)) {
                                    referencedData.authors[authorKey] = authorCache[authorKey]!!
                                    return@forEach
                                }
                                
                                val authorDetail = api.getAuthorDetail(authorKey)
                                authorCache[authorKey] = authorDetail
                                updateCache(authorKey)
                                referencedData.authors[authorKey] = authorDetail
                            } catch (e: Exception) {
                                // Skip if author fetch fails
                            }
                        }
                        completedItems += workDetail.authors.size
                    }
                }
                deferredCalls.add(deferred)
            }
            
            // Fetch subject details in parallel
            workDetail.subjects?.forEach { subject ->
                val deferred = async {
                    try {
                        val subjectKey = extractKeyFromUrl(subject)
                        
                        // Check cache first
                        if (subjectCache.containsKey(subjectKey) && isCacheValid(subjectKey)) {
                            referencedData.subjects[subjectKey] = subjectCache[subjectKey]!!
                            completedItems++
                            return@async
                        }
                        
                        val subjectDetail = api.getSubjectDetail(subjectKey)
                        subjectCache[subjectKey] = subjectDetail
                        updateCache(subjectKey)
                        referencedData.subjects[subjectKey] = subjectDetail
                        completedItems++
                    } catch (e: Exception) {
                        completedItems++
                    }
                }
                deferredCalls.add(deferred)
            }
            
            // Fetch place details in parallel
            workDetail.subjectPlaces?.forEach { place ->
                val deferred = async {
                    try {
                        val placeKey = extractKeyFromUrl(place)
                        
                        // Check cache first
                        if (placeCache.containsKey(placeKey) && isCacheValid(placeKey)) {
                            referencedData.places[placeKey] = placeCache[placeKey]!!
                            completedItems++
                            return@async
                        }
                        
                        val placeDetail = api.getPlaceDetail(placeKey)
                        placeCache[placeKey] = placeDetail
                        updateCache(placeKey)
                        referencedData.places[placeKey] = placeDetail
                        completedItems++
                    } catch (e: Exception) {
                        completedItems++
                    }
                }
                deferredCalls.add(deferred)
            }
            
            // Fetch person details in parallel
            workDetail.subjectPeople?.forEach { person ->
                val deferred = async {
                    try {
                        val personKey = extractKeyFromUrl(person)
                        
                        // Check cache first
                        if (personCache.containsKey(personKey) && isCacheValid(personKey)) {
                            referencedData.people[personKey] = personCache[personKey]!!
                            completedItems++
                            return@async
                        }
                        
                        val personDetail = api.getPersonDetail(personKey)
                        personCache[personKey] = personDetail
                        updateCache(personKey)
                        referencedData.people[personKey] = personDetail
                        completedItems++
                    } catch (e: Exception) {
                        completedItems++
                    }
                }
                deferredCalls.add(deferred)
            }
            
            // Fetch time details in parallel
            workDetail.subjectTimes?.forEach { time ->
                val deferred = async {
                    try {
                        val timeKey = extractKeyFromUrl(time)
                        
                        // Check cache first
                        if (timeCache.containsKey(timeKey) && isCacheValid(timeKey)) {
                            referencedData.times[timeKey] = timeCache[timeKey]!!
                            completedItems++
                            return@async
                        }
                        
                        val timeDetail = api.getTimeDetail(timeKey)
                        timeCache[timeKey] = timeDetail
                        updateCache(timeKey)
                        referencedData.times[timeKey] = timeDetail
                        completedItems++
                    } catch (e: Exception) {
                        completedItems++
                    }
                }
                deferredCalls.add(deferred)
            }
            
            // Fetch publisher details in parallel
            workDetail.publishers?.forEach { publisher ->
                val deferred = async {
                    try {
                        val publisherKey = publisher.name // Publisher doesn't have key, use name as identifier
                        
                        // Check cache first
                        if (publisherCache.containsKey(publisherKey) && isCacheValid(publisherKey)) {
                            referencedData.publishers[publisherKey] = publisherCache[publisherKey]!!
                            completedItems++
                            return@async
                        }
                        
                        val publisherDetail = api.getPublisherDetail(publisherKey)
                        publisherCache[publisherKey] = publisherDetail
                        updateCache(publisherKey)
                        referencedData.publishers[publisherKey] = publisherDetail
                        completedItems++
                    } catch (e: Exception) {
                        completedItems++
                    }
                }
                deferredCalls.add(deferred)
            }
            
            // Wait for all parallel calls to complete
            deferredCalls.awaitAll()
            
            referencedData
        }
    }

    fun searchBooks(
        query: String,
        page: Int = 1,
        language: LanguageOption = LanguageOption.ALL,
        sort: SortOption = SortOption.RELEVANCE
    ): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading)
        try {
            // Calculate offset based on page number (OpenLibrary API uses offset)
            val offset = (page - 1) * 20
            
            val response = api.searchBooks(
                query = query,
                offset = offset, // Use offset instead of page
                limit = 20, // Fixed page size for manual loading
                language = if (language.code.isNotEmpty()) language.code else null,
                sort = sort.apiValue
            )
            
            val books = response.docs.map { doc ->
                val bookKey = doc.key
                val isFavorite = bookDao.isBookFavorite(bookKey)
                Book(
                    key = bookKey,
                    title = doc.title,
                    authorNames = doc.authorNames ?: emptyList(),
                    firstPublishedYear = doc.firstPublishedYear,
                    publisher = doc.publisher?.firstOrNull(),
                    isbn = doc.isbns?.firstOrNull(),
                    coverId = doc.coverId,
                    subjects = doc.subject ?: emptyList(),
                    isFavorite = isFavorite
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
            // Clean workId by removing "works/" prefix if present
            val cleanWorkId = workId.removePrefix("works/")
            
            val workDetail = api.getWorkDetail(cleanWorkId)
            
            // Fetch all referenced data automatically
            val referencedData = fetchReferencedData(workDetail)
            
            // Build author names from referenced data with better fallback logic
            val authorNames = workDetail.authors?.mapNotNull { author ->
                val authorKey = extractKeyFromUrl(author.author.key)
                val authorDetail = referencedData.authors[authorKey]
                
                // Try multiple fallback strategies for author names
                val authorName = when {
                    // First priority: fetched author detail name
                    authorDetail?.name != null -> authorDetail.name
                    // Second priority: original author name from work detail
                    author.author.name != null -> author.author.name
                    // Third priority: try to extract from author key (remove prefix and format nicely)
                    else -> {
                        val cleanKey = authorKey.removePrefix("authors/")
                        // Try to make it more readable by removing OL prefix if present
                        if (cleanKey.startsWith("OL")) {
                            "Author $cleanKey"
                        } else {
                            cleanKey
                        }
                    }
                }
                
                authorName
            } ?: emptyList()
            
            val bookKey = workDetail.key
            val isFavorite = bookDao.isBookFavorite(bookKey)
            val book = Book(
                key = bookKey,
                title = workDetail.title,
                authorNames = authorNames,
                firstPublishedYear = workDetail.firstPublishDate?.substringBefore("-")?.toIntOrNull(),
                publisher = workDetail.publishers?.firstOrNull()?.let { publisher ->
                    val publisherKey = publisher.name // Publisher doesn't have key, use name as identifier
                    referencedData.publishers[publisherKey]?.name ?: publisher.name
                },
                isbn = (workDetail.isbn13 ?: workDetail.isbn10)?.firstOrNull(),
                coverId = workDetail.coverIds?.firstOrNull(),
                description = workDetail.description,
                subjects = workDetail.subjects?.mapNotNull { subject ->
                    val subjectKey = extractKeyFromUrl(subject)
                    referencedData.subjects[subjectKey]?.name ?: subject
                } ?: emptyList(),
                isFavorite = isFavorite,
                // Additional fields
                tableOfContents = workDetail.tableOfContents ?: emptyList(),
                editionName = workDetail.editionName,
                numberOfPages = workDetail.numberOfPages,
                format = workDetail.format,
                deweyDecimalClass = workDetail.deweyDecimalClass ?: emptyList(),
                lcClassifications = workDetail.lcClassifications ?: emptyList(),
                ocaid = workDetail.ocaid,
                lccn = workDetail.lccn ?: emptyList(),
                oclcNumbers = workDetail.oclcNumbers ?: emptyList(),
                publishPlaces = workDetail.publishPlaces ?: emptyList(),
                copyrightDate = workDetail.copyrightDate,
                workTitles = workDetail.workTitles ?: emptyList(),
                // New fields from API response with enhanced data
                subjectPlaces = workDetail.subjectPlaces?.mapNotNull { place ->
                    val placeKey = extractKeyFromUrl(place)
                    referencedData.places[placeKey]?.name ?: place
                } ?: emptyList(),
                subjectPeople = workDetail.subjectPeople?.mapNotNull { person ->
                    val personKey = extractKeyFromUrl(person)
                    referencedData.people[personKey]?.name ?: person
                } ?: emptyList(),
                subjectTimes = workDetail.subjectTimes?.mapNotNull { time ->
                    val timeKey = extractKeyFromUrl(time)
                    referencedData.times[timeKey]?.name ?: time
                } ?: emptyList(),
                location = workDetail.location,
                latestRevision = workDetail.latestRevision,
                revision = workDetail.revision,
                created = workDetail.created?.value,
                lastModified = workDetail.lastModified?.value,
                // Store original author information
                authors = workDetail.authors ?: emptyList()
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

    suspend fun isBookFavorite(bookKey: String): Boolean {
        return bookDao.isBookFavorite(bookKey)
    }
}
