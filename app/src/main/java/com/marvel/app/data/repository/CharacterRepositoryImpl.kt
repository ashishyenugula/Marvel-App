package com.marvel.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.marvel.app.BuildConfig
import com.marvel.app.data.api.MarvelApi
import com.marvel.app.data.model.CharacterDto
import com.marvel.app.data.model.toDomain
import com.marvel.app.data.model.toDetail
import com.marvel.app.data.paging.CharacterPagingSource
import com.marvel.app.domain.model.MarvelCharacter
import com.marvel.app.domain.model.ResourceDetail
import com.marvel.app.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val api: MarvelApi
) : CharacterRepository {

    override fun getCharactersPaged(): Flow<PagingData<CharacterDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = CharacterPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { CharacterPagingSource(api) }
        ).flow
    }

    override suspend fun getCharacterById(characterId: Int): Result<MarvelCharacter> {
        return try {
            val response = api.getCharacterDetail(characterId)
            val character = response.data.results.firstOrNull()
            if (character != null) {
                Result.success(character.toDomain())
            } else {
                Result.failure(Exception("Character not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getResourceDetail(resourceUri: String): Result<ResourceDetail> {
        return try {
            val ts = System.currentTimeMillis().toString()
            val hash = generateHash(ts)

            val response = api.getResourceDetail(
                url = resourceUri,
                ts = ts,
                apiKey = BuildConfig.MARVEL_PUBLIC_KEY,
                hash = hash
            )

            val detail = response.data.results.firstOrNull()
            if (detail != null) {
                Result.success(detail.toDetail())
            } else {
                Result.failure(Exception("Resource not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateHash(timestamp: String): String {
        val input = timestamp + BuildConfig.MARVEL_PRIVATE_KEY + BuildConfig.MARVEL_PUBLIC_KEY
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
