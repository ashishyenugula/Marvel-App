package com.marvel.app.data.model

import com.google.gson.annotations.SerializedName

data class MarvelResponse(
    @SerializedName("data") val data: MarvelData
)

data class MarvelData(
    @SerializedName("offset") val offset: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<CharacterDto>
)

data class CharacterDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("thumbnail") val thumbnail: ThumbnailDto?,
    @SerializedName("comics") val comics: ResourceListDto?,
    @SerializedName("series") val series: ResourceListDto?,
    @SerializedName("stories") val stories: ResourceListDto?,
    @SerializedName("events") val events: ResourceListDto?
)

data class ThumbnailDto(
    @SerializedName("path") val path: String,
    @SerializedName("extension") val extension: String
) {
    // Marvel requires https and the full image path
    fun toUrl(): String {
        val securePath = path.replace("http://", "https://")
        return "$securePath.$extension"
    }
}

data class ResourceListDto(
    @SerializedName("available") val available: Int,
    @SerializedName("items") val items: List<ResourceItemDto>?
)

data class ResourceItemDto(
    @SerializedName("resourceURI") val resourceURI: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?
)

// Response when fetching individual resource (comic, series, etc.)
data class ResourceDetailResponse(
    @SerializedName("data") val data: ResourceDetailData
)

data class ResourceDetailData(
    @SerializedName("results") val results: List<ResourceDetailDto>
)

data class ResourceDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("thumbnail") val thumbnail: ThumbnailDto?
)
