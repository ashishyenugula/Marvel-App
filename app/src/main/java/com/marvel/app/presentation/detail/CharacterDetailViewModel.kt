package com.marvel.app.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel.app.domain.model.MarvelCharacter
import com.marvel.app.domain.model.ResourceDetail
import com.marvel.app.domain.model.ResourceItem
import com.marvel.app.domain.usecase.GetCharacterDetailUseCase
import com.marvel.app.domain.usecase.GetResourceDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

data class DetailUiState(
    val character: MarvelCharacter? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val characterName: String = "",
    val characterImageUrl: String = "",
    // maps from resourceUri to loaded thumbnail url
    val resourceImages: Map<String, String> = emptyMap(),
    val loadingResources: Set<String> = emptySet(),
    // track which sections are expanded
    val expandedSections: Set<String> = emptySet()
)

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCharacterDetail: GetCharacterDetailUseCase,
    private val getResourceDetail: GetResourceDetailUseCase
) : ViewModel() {

    private val characterId: Int = savedStateHandle.get<Int>("characterId") ?: 0

    private val _uiState = MutableStateFlow(
        DetailUiState(
            characterName = URLDecoder.decode(
                savedStateHandle.get<String>("characterName") ?: "", "UTF-8"
            ),
            characterImageUrl = URLDecoder.decode(
                savedStateHandle.get<String>("imageUrl") ?: "", "UTF-8"
            )
        )
    )
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadCharacterDetail()
    }

    private fun loadCharacterDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getCharacterDetail(characterId).fold(
                onSuccess = { character ->
                    _uiState.update {
                        it.copy(
                            character = character,
                            isLoading = false,
                            characterName = character.name,
                            characterImageUrl = character.imageUrl
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load character"
                        )
                    }
                }
            )
        }
    }

    fun toggleSection(sectionName: String) {
        _uiState.update { state ->
            val newExpanded = state.expandedSections.toMutableSet()
            if (newExpanded.contains(sectionName)) {
                newExpanded.remove(sectionName)
            } else {
                newExpanded.add(sectionName)
            }
            state.copy(expandedSections = newExpanded)
        }
    }

    fun loadResourceImage(resourceUri: String) {
        // don't re-fetch if already loaded or currently loading
        if (_uiState.value.resourceImages.containsKey(resourceUri)) return
        if (_uiState.value.loadingResources.contains(resourceUri)) return

        viewModelScope.launch {
            _uiState.update { it.copy(loadingResources = it.loadingResources + resourceUri) }

            getResourceDetail(resourceUri).fold(
                onSuccess = { detail ->
                    val url = detail.imageUrl
                    if (url != null) {
                        _uiState.update { state ->
                            state.copy(
                                resourceImages = state.resourceImages + (resourceUri to url),
                                loadingResources = state.loadingResources - resourceUri
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(loadingResources = it.loadingResources - resourceUri)
                        }
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(loadingResources = it.loadingResources - resourceUri)
                    }
                }
            )
        }
    }

    fun retry() {
        loadCharacterDetail()
    }
}
