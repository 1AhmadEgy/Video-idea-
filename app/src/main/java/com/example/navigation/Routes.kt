package com.example.navigation

object Routes {
    const val HOME = "home"
    const val PROJECTS = "projects"
    const val CREATE = "create"
    const val STORYBOARD = "storyboard/{projectId}"
    const val SCENE = "scene/{sceneId}"
    const val EDITOR = "editor/{projectId}"
    const val RENDER = "render/{projectId}"
    const val SETTINGS = "settings"

    fun storyboard(projectId: String): String = "storyboard/$projectId"
    fun scene(sceneId: String): String = "scene/$sceneId"
    fun editor(projectId: String): String = "editor/$projectId"
    fun render(projectId: String): String = "render/$projectId"
}
