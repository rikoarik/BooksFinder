package com.app.bookfinder.ui.components

enum class SortOption(val displayName: String, val apiValue: String) {
    RELEVANCE("Relevansi", ""),
    NEW("Edisi Terbaru", "new"),
    OLD("Edisi Terlama", "old"),
    RANDOM("Acak", "random"),
    KEY("Berdasarkan Key", "key")
}
