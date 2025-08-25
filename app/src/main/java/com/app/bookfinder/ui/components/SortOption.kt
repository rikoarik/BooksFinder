package com.app.bookfinder.ui.components

enum class SortOption(val displayName: String, val apiValue: String) {
    RELEVANCE("Relevance", ""),
    NEW("Newest Edition", "new"),
    OLD("Oldest Edition", "old"),
    RANDOM("Random", "random"),
    KEY("Based on Key", "key")

}
