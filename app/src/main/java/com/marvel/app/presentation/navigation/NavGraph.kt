package com.marvel.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.marvel.app.presentation.detail.CharacterDetailScreen
import com.marvel.app.presentation.list.CharacterListScreen

object Routes {
    const val CHARACTER_LIST = "character_list"
    const val CHARACTER_DETAIL = "character_detail/{characterId}/{characterName}/{imageUrl}"

    fun detailRoute(characterId: Int, characterName: String, imageUrl: String): String {
        val encoded = java.net.URLEncoder.encode(imageUrl, "UTF-8")
        val encodedName = java.net.URLEncoder.encode(characterName, "UTF-8")
        return "character_detail/$characterId/$encodedName/$encoded"
    }
}

@Composable
fun MarvelNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.CHARACTER_LIST,
    ) {
        composable(
            route = Routes.CHARACTER_LIST,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(300)
                )
            }
        ) {
            CharacterListScreen(
                onCharacterClick = { id, name, imageUrl ->
                    navController.navigate(Routes.detailRoute(id, name, imageUrl))
                }
            )
        }

        composable(
            route = Routes.CHARACTER_DETAIL,
            arguments = listOf(
                navArgument("characterId") { type = NavType.IntType },
                navArgument("characterName") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(300)
                )
            }
        ) {
            CharacterDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
