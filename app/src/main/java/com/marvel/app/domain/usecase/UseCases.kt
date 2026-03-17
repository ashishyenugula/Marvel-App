package com.marvel.app.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.marvel.app.data.model.toDomain
import com.marvel.app.domain.model.MarvelCharacter
import com.marvel.app.domain.model.ResourceDetail
import com.marvel.app.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(): Flow<PagingData<MarvelCharacter>> {
        return repository.getCharactersPaged().map { pagingData ->
            pagingData.map { dto -> dto.toDomain() }
        }
    }
}

class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(characterId: Int): Result<MarvelCharacter> {
        return repository.getCharacterById(characterId)
    }
}

class GetResourceDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(resourceUri: String): Result<ResourceDetail> {
        return repository.getResourceDetail(resourceUri)
    }
}
