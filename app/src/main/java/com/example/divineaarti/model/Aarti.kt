package com.example.divineaarti.model

data class Aarti(
    val id: Int,
    val title: String,
    val deity: String,
    val duration: String,
    val category: AartiCategory,
    val imageUrl: String,
    val audioUrl: String,
    val lyrics: List<String>,
    var isFavorite: Boolean = false
)

enum class AartiCategory(val displayName: String) {
    ALL("All Aartis"),
    MORNING("Morning"),
    EVENING("Evening"),
    FESTIVAL("Festival"),
    POPULAR("Popular")
}