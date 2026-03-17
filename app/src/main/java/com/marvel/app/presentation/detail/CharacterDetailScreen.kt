package com.marvel.app.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.marvel.app.domain.model.ResourceItem
import com.marvel.app.presentation.list.ErrorContent
import com.marvel.app.presentation.theme.DarkCard
import com.marvel.app.presentation.theme.MarvelRed

@Composable
fun CharacterDetailScreen(
    viewModel: CharacterDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        when {
            state.isLoading && state.character == null -> {
                DetailLoadingContent(
                    characterName = state.characterName,
                    imageUrl = state.characterImageUrl,
                    onBackClick = onBackClick
                )
            }

            state.errorMessage != null && state.character == null -> {
                ErrorContent(
                    message = state.errorMessage ?: "Unknown error",
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                val character = state.character ?: return@Column

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // hero image with gradient and back button
                    HeroImageSection(
                        imageUrl = character.imageUrl,
                        characterName = character.name,
                        onBackClick = onBackClick
                    )

                    // character info
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = character.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        if (character.description.isNotBlank()) {
                            Text(
                                text = character.description,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp),
                                lineHeight = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // resource sections — only show if data exists
                        if (character.comicsCount > 0) {
                            ResourceSection(
                                title = "Comics",
                                count = character.comicsCount,
                                items = character.comics,
                                isExpanded = state.expandedSections.contains("Comics"),
                                onToggle = { viewModel.toggleSection("Comics") },
                                resourceImages = state.resourceImages,
                                loadingResources = state.loadingResources,
                                onLoadImage = { viewModel.loadResourceImage(it) }
                            )
                        }

                        if (character.seriesCount > 0) {
                            ResourceSection(
                                title = "Series",
                                count = character.seriesCount,
                                items = character.series,
                                isExpanded = state.expandedSections.contains("Series"),
                                onToggle = { viewModel.toggleSection("Series") },
                                resourceImages = state.resourceImages,
                                loadingResources = state.loadingResources,
                                onLoadImage = { viewModel.loadResourceImage(it) }
                            )
                        }

                        if (character.storiesCount > 0) {
                            ResourceSection(
                                title = "Stories",
                                count = character.storiesCount,
                                items = character.stories,
                                isExpanded = state.expandedSections.contains("Stories"),
                                onToggle = { viewModel.toggleSection("Stories") },
                                resourceImages = state.resourceImages,
                                loadingResources = state.loadingResources,
                                onLoadImage = { viewModel.loadResourceImage(it) }
                            )
                        }

                        if (character.eventsCount > 0) {
                            ResourceSection(
                                title = "Events",
                                count = character.eventsCount,
                                items = character.events,
                                isExpanded = state.expandedSections.contains("Events"),
                                onToggle = { viewModel.toggleSection("Events") },
                                resourceImages = state.resourceImages,
                                loadingResources = state.loadingResources,
                                onLoadImage = { viewModel.loadResourceImage(it) }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLoadingContent(
    characterName: String,
    imageUrl: String,
    onBackClick: () -> Unit
) {
    Column {
        if (imageUrl.isNotBlank()) {
            HeroImageSection(
                imageUrl = imageUrl,
                characterName = characterName,
                onBackClick = onBackClick
            )
        }

        if (characterName.isNotBlank()) {
            Text(
                text = characterName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MarvelRed)
        }
    }
}

@Composable
private fun HeroImageSection(
    imageUrl: String,
    characterName: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = characterName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // top gradient for back button visibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )

        // bottom gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        // back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ResourceSection(
    title: String,
    count: Int,
    items: List<ResourceItem>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    resourceImages: Map<String, String>,
    loadingResources: Set<String>,
    onLoadImage: (String) -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "chevron_rotation"
    )

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        // section header - clickable to expand
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "($count)",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(rotationAngle)
                )
            }
        }

        // expandable content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
        ) {
            if (items.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items) { item ->
                        ResourceCard(
                            item = item,
                            imageUrl = resourceImages[item.resourceUri],
                            isLoading = loadingResources.contains(item.resourceUri),
                            onAppear = { onLoadImage(item.resourceUri) }
                        )
                    }
                }
            } else {
                Text(
                    text = "No details available",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ResourceCard(
    item: ResourceItem,
    imageUrl: String?,
    isLoading: Boolean,
    onAppear: () -> Unit
) {
    // trigger lazy loading when card appears
    LaunchedEffect(item.resourceUri) {
        if (item.resourceUri.isNotBlank()) {
            onAppear()
        }
    }

    Card(
        modifier = Modifier
            .width(140.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .background(DarkCard),
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageUrl != null -> {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                CircularProgressIndicator(
                                    color = MarvelRed,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        )
                    }

                    isLoading -> {
                        CircularProgressIndicator(
                            color = MarvelRed,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    else -> {
                        // placeholder when no image
                        Text(
                            text = item.name.take(2).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}
