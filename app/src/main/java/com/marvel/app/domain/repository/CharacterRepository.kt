package com.marvel.app.domain.repository

import androidx.paging.PagingData
import com.marvel.app.data.model.CharacterDto
import com.marvel.app.domain.model.MarvelCharacter
import com.marvel.app.domain.model.ResourceDetail
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharactersPaged(): Flow<PagingData<CharacterDto>>

    suspend fun getCharacterById(characterId: Int): Result<MarvelCharacter>

    suspend fun getResourceDetail(resourceUri: String): Result<ResourceDetail>
}
