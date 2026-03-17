package com.marvel.app.data.model

import com.marvel.app.domain.model.MarvelCharacter
import com.marvel.app.domain.model.ResourceDetail
import com.marvel.app.domain.model.ResourceItem

// Mapping DTOs to domain models

fun CharacterDto.toDomain(): MarvelCharacter {
    return MarvelCharacter(
        id = id,
        name = name,
        description = description.orEmpty(),
        imageUrl = thumbnail?.toUrl() ?: "",
        comics = comics?.items?.map { it.toDomain() } ?: emptyList(),
        series = series?.items?.map { it.toDomain() } ?: emptyList(),
        stories = stories?.items?.map { it.toDomain() } ?: emptyList(),
        events = events?.items?.map { it.toDomain() } ?: emptyList(),
        comicsCount = comics?.available ?: 0,
        seriesCount = series?.available ?: 0,
        storiesCount = stories?.available ?: 0,
        eventsCount = events?.available ?: 0
    )
}

fun ResourceItemDto.toDomain(): ResourceItem {
    return ResourceItem(
        name = name.orEmpty(),
        resourceUri = (resourceURI ?: "").replace("http://", "https://"),
        type = type
    )
}

fun ResourceDetailDto.toDetail(): ResourceDetail {
    return ResourceDetail(
        id = id,
        title = title ?: "Unknown",
        imageUrl = thumbnail?.toUrl()
    )
}
