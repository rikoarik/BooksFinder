package com.app.bookfinder.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.bookfinder.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.search_placeholder),
    leadingIcon: ImageVector = Icons.Default.Search,
    trailingIcon: ImageVector? = Icons.Default.Clear,
    isLoading: Boolean = false,
    isExpanded: Boolean = false,
    onToggleExpanded: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val hapticFeedback = LocalHapticFeedback.current
    
    // Animated scale for toggle button
    val toggleScale by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.8f,
        animationSpec = tween(200),
        label = "toggle_scale"
    )
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Toggle button to expand/collapse search
        if (onToggleExpanded != null) {
            IconButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onToggleExpanded()
                },
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (isExpanded) "Close search" else "Open search",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Animated search field
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = stringResource(R.string.search_books),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty() && !isLoading) {
                        IconButton(
                            onClick = {
                                onQueryChange("")
                                onClear?.invoke()
                            }
                        ) {
                            Icon(
                                imageVector = trailingIcon ?: Icons.Default.Clear,
                                contentDescription = stringResource(R.string.search_clear),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (query.isNotEmpty() && !isLoading) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSearch(query)
                            focusManager.clearFocus()
                        }
                    }
                ),
                singleLine = true,
                enabled = !isLoading
            )
        }
    }
    
    // Auto-focus when expanded
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        var isExpanded by remember { mutableStateOf(true) }
        var query by remember { mutableStateOf("") }
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { /* Preview */ },
                isExpanded = isExpanded,
                onToggleExpanded = { isExpanded = !isExpanded }
            )
            
            Button(
                onClick = { isExpanded = !isExpanded }
            ) {
                Text(if (isExpanded) "Collapse Search" else "Expand Search")
            }
        }
    }
}
