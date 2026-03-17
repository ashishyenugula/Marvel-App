package com.marvel.app.domain.model

/**
 * Domain representation of a Marvel character.
 * Decoupled from the API DTOs so the presentation layer
 * doesn't depend on network models directly.
 */
data class MarvelCharacter(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val comics: List<ResourceItem>,
    val series: List<ResourceItem>,
    val stories: List<ResourceItem>,
    val events: List<ResourceItem>,
    val comicsCount: Int,
    val seriesCount: Int,
    val storiesCount: Int,
    val eventsCount: Int
)

data class ResourceItem(
    val name: String,
    val resourceUri: String,
    val type: String? = null,
    // loaded lazily from the resourceURI
    val imageUrl: String? = null
)

data class ResourceDetail(
    val id: Int,
    val title: String,
    val imageUrl: String?
)
