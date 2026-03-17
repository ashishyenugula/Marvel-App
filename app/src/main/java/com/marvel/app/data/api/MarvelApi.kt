package com.marvel.app.data.api

import com.marvel.app.data.model.MarvelResponse
import com.marvel.app.data.model.ResourceDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface MarvelApi {

    @GET("characters")
    suspend fun getCharacters(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): MarvelResponse

    @GET("characters/{characterId}")
    suspend fun getCharacterDetail(
        @Query("characterId") characterId: Int
    ): MarvelResponse

    // Fetch resource details (comics, series, events, stories)
    // using the full resourceURI from the character response
    @GET
    suspend fun getResourceDetail(
        @Url url: String,
        @Query("ts") ts: String,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String
    ): ResourceDetailResponse

    companion object {
        const val BASE_URL = "https://gateway.marvel.com/v1/public/"
    }
}
