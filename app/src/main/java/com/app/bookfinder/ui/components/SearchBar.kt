package com.app.bookfinder.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.bookfinder.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.search_placeholder),
    leadingIcon: ImageVector = Icons.Default.Search,
    trailingIcon: ImageVector? = Icons.Default.Clear
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    
    var isSearchActive by remember { mutableStateOf(false) }
    
    SearchBar(
        query = { query },
        onQueryChange = onQueryChange,
        onSearch = { searchQuery ->
            onSearch(searchQuery)
            focusManager.clearFocus()
            isSearchActive = false
        },
        active = isSearchActive,
        onActiveChange = { isSearchActive = it },
        placeholder = { 
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyLarge
            ) 
        },
        leadingIcon = { 
            Icon(
                leadingIcon, 
                contentDescription = stringResource(R.string.search_books),
                tint = MaterialTheme.colorScheme.primary
            ) 
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onQueryChange("")
                        onSearch("")
                    }
                ) {
                    Icon(
                        trailingIcon, 
                        contentDescription = stringResource(R.string.search_clear),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .focusRequester(focusRequester),
        shape = RoundedCornerShape(16.dp),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            inputFieldColors = SearchBarDefaults.inputFieldColors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )
    ) {
        // Search suggestions can be added here if needed
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
