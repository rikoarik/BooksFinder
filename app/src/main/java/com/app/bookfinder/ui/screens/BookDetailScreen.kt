package com.app.bookfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import coil.compose.AsyncImage
import com.app.bookfinder.R
import com.app.bookfinder.data.model.Book
import com.app.bookfinder.data.model.Resource
import com.app.bookfinder.ui.components.ErrorState
import com.app.bookfinder.ui.components.LoadingState
import com.app.bookfinder.ui.viewmodel.BookDetailViewModel
import com.app.bookfinder.data.model.Author
import com.app.bookfinder.data.model.AuthorDetail


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel,
    onBackClick: () -> Unit
) {
    val book by viewModel.book.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.book_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    val scale by animateFloatAsState(
                        targetValue = if (isFavorite) 1.2f else 1.0f,
                        animationSpec = tween(durationMillis = 200),
                        label = "favorite_scale"
                    )
                    
                    IconButton(
                        onClick = { 
                            viewModel.toggleFavorite() 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .scale(scale)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    color = if (isFavorite) 
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                    else 
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) stringResource(R.string.action_remove_favorite) else stringResource(R.string.action_add_favorite),
                                tint = MaterialTheme.colorScheme.onPrimary, // Always use onPrimary to match topbar
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (val currentBook = book) {
            is Resource.Loading -> {
                LoadingState(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is Resource.Success -> {
                BookDetailContent(
                    book = currentBook.data,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is Resource.Error -> {
                ErrorState(
                    message = currentBook.message,
                    onRetry = { 
                        viewModel.loadBookDetail() 
                    },
                    modifier = Modifier.padding(paddingValues),
                    onRefresh = {
                        viewModel.loadBookDetail() 
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookDetailContent(
    book: Book,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Book cover and basic info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = book.getCoverImageUrl(),
                contentDescription = stringResource(R.string.book_details),
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Author - only show if available
                if (book.authorNames.isNotEmpty()) {
                    Text(
                        text = book.getAuthorDisplayName(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (book.authors.isNotEmpty()) {
                    // Show author keys if names are not available
                    val authorKeys = book.authors.map { it.author.key.removePrefix("/authors/") }
                    Text(
                        text = "Authors: ${authorKeys.joinToString(", ")}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Year - only show if available
                if (book.firstPublishedYear != null) {
                    Text(
                        text = stringResource(R.string.book_published) + ": ${book.getYearDisplay()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Edition name - only show if available
                if (!book.editionName.isNullOrBlank()) {
                    Text(
                        text = book.editionName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Divider
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        
        // Description - only show if available and not empty
        if (!book.description.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.book_description),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Justify
                )
            }
        }
        
        // Publisher - only show if available and not empty
        if (!book.publisher.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.book_publisher),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.publisher,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // ISBN - only show if available and not empty
        if (!book.isbn.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ISBN",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.isbn,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Format and Pages - only show if available
        if (book.format != null || book.numberOfPages != null) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Physical Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (!book.format.isNullOrBlank()) {
                    Text(
                        text = "Format: ${book.getFormatDisplay()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (book.numberOfPages != null && book.numberOfPages > 0) {
                    Text(
                        text = "Pages: ${book.getPagesDisplay()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Publish Places and Copyright - only show if available
        if (book.publishPlaces.isNotEmpty() || !book.copyrightDate.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Publication Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (book.publishPlaces.isNotEmpty()) {
                    Text(
                        text = "Published in: ${book.getPublishPlacesDisplay()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (!book.copyrightDate.isNullOrBlank()) {
                    Text(
                        text = "Copyright: ${book.copyrightDate}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Classifications - only show if available
        if (book.deweyDecimalClass.isNotEmpty() || book.lcClassifications.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Classifications",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.getClassificationsDisplay(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Table of Contents - only show if available and not empty
        if (book.tableOfContents.isNotEmpty() && book.tableOfContents.any { !it.isNullOrBlank() }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Table of Contents",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                book.tableOfContents.filter { !it.isNullOrBlank() }.forEachIndexed { index, content ->
                    Text(
                        text = "${index + 1}. $content",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Work Titles - only show if available and different from main title
        if (book.workTitles.isNotEmpty() && book.workTitles.any { !it.isNullOrBlank() && it != book.title }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Contains",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                book.workTitles.filter { !it.isNullOrBlank() && it != book.title }.forEach { workTitle ->
                    Text(
                        text = "• $workTitle",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Subjects - only show if available and not empty
        if (book.subjects.isNotEmpty() && book.subjects.any { !it.isNullOrBlank() }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.book_subjects),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    book.subjects.filter { !it.isNullOrBlank() }.take(10).forEach { subject ->
                        AssistChip(
                            onClick = { },
                            label = { Text(subject) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }
        }
        
        // Subject Places - only show if available
        if (book.subjectPlaces.isNotEmpty() && book.subjectPlaces.any { !it.isNullOrBlank() }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Places Mentioned",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.getSubjectPlacesDisplay(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Subject People - only show if available
        if (book.subjectPeople.isNotEmpty() && book.subjectPeople.any { !it.isNullOrBlank() }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "People Mentioned",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.getSubjectPeopleDisplay(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Subject Times - only show if available
        if (book.subjectTimes.isNotEmpty() && book.subjectTimes.any { !it.isNullOrBlank() }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Time Period",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.getSubjectTimesDisplay(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Location - only show if available
        if (!book.location.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Related Location",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.getLocationDisplay(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Revision Info - only show if available
        if (book.revision != null || book.latestRevision != null) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Database Information",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = book.getRevisionInfo(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Show creation and modification dates if available
                if (!book.created.isNullOrBlank()) {
                    Text(
                        text = "Created: ${book.created}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (!book.lastModified.isNullOrBlank()) {
                    Text(
                        text = "Last Modified: ${book.lastModified}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Show what information is available
        val availableInfo = mutableListOf<String>()
        if (!book.description.isNullOrBlank()) availableInfo.add("Description")
        if (!book.publisher.isNullOrBlank()) availableInfo.add("Publisher")
        if (!book.isbn.isNullOrBlank()) availableInfo.add("ISBN")
        if (book.format != null) availableInfo.add("Format")
        if (book.numberOfPages != null) availableInfo.add("Pages")
        if (book.publishPlaces.isNotEmpty()) availableInfo.add("Publication Places")
        if (book.subjects.isNotEmpty()) availableInfo.add("Subjects")
        if (book.subjectPlaces.isNotEmpty()) availableInfo.add("Places Mentioned")
        if (book.subjectPeople.isNotEmpty()) availableInfo.add("People Mentioned")
        if (book.subjectTimes.isNotEmpty()) availableInfo.add("Time Period")
        
        if (availableInfo.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Available Information",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = availableInfo.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Fallback message if no additional information is available
        if (!book.hasAdditionalInfo()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No additional information available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "This book may have limited metadata in our database",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
