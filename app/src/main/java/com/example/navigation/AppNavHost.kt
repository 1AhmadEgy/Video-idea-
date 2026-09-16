package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feature.create.CreateProjectRoute
import com.example.feature.projects.ProjectsRoute
import com.example.feature.storyboard.StoryboardRoute
import com.example.feature.home.HomeScreen
import com.example.feature.editor.EditorRoute
import com.example.feature.render.RenderRoute
import com.example.feature.settings.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenProjects = { navController.navigate(Routes.PROJECTS) },
                onCreateProject = { navController.navigate(Routes.CREATE) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        
        composable(Routes.PROJECTS) {
            ProjectsRoute(
                onCreateProject = {
                    navController.navigate(Routes.CREATE)
                },
                onOpenProject = { projectId ->
                    navController.navigate(Routes.storyboard(projectId))
                }
            )
        }
        
        composable(Routes.CREATE) {
            CreateProjectRoute(
                onBack = {
                    navController.popBackStack()
                },
                onProjectCreated = { projectId ->
                    navController.navigate(Routes.storyboard(projectId)) {
                        popUpTo(Routes.PROJECTS)
                    }
                }
            )
        }
        
        composable(
            route = Routes.STORYBOARD,
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.StringType
                }
            )
        ) {
            StoryboardRoute(
                onBack = { navController.popBackStack() },
                onNavigateToEditor = { projectId ->
                    navController.navigate(Routes.editor(projectId))
                }
            )
        }
        
        composable(
            route = Routes.EDITOR,
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val projectId = entry.arguments?.getString("projectId").orEmpty()
            EditorRoute(
                onBack = { navController.popBackStack() },
                onNavigateToRender = { pId ->
                    navController.navigate(Routes.render(pId))
                }
            )
        }
        
        composable(
            route = Routes.RENDER,
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val projectId = entry.arguments?.getString("projectId").orEmpty()
            RenderRoute(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onFinish = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0)
                    }
                }
            )
        }
        
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
