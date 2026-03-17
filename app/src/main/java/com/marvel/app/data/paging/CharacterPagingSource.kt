package com.marvel.app.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.marvel.app.data.api.MarvelApi
import com.marvel.app.data.model.CharacterDto
import retrofit2.HttpException
import java.io.IOException

class CharacterPagingSource(
    private val api: MarvelApi
) : PagingSource<Int, CharacterDto>() {

    override fun getRefreshKey(state: PagingState<Int, CharacterDto>): Int? {
        return state.anchorPosition?.let { anchor ->
            val closestPage = state.closestPageToPosition(anchor)
            closestPage?.prevKey?.plus(PAGE_SIZE)
                ?: closestPage?.nextKey?.minus(PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterDto> {
        val offset = params.key ?: 0

        return try {
            val response = api.getCharacters(
                offset = offset,
                limit = PAGE_SIZE
            )

            val characters = response.data.results
            val total = response.data.total

            // figure out if there's more pages
            val nextOffset = offset + PAGE_SIZE
            val nextKey = if (nextOffset < total) nextOffset else null
            val prevKey = if (offset > 0) offset - PAGE_SIZE else null

            LoadResult.Page(
                data = characters,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
