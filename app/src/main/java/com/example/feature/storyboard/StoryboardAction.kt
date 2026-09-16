package com.example.feature.storyboard

sealed interface StoryboardAction {
    data object Refresh : StoryboardAction
    data object GenerateScript : StoryboardAction
    data class AddScene(val position: Int) : StoryboardAction
    data class DeleteScene(val sceneId: String) : StoryboardAction
    data class UpdateScene(
        val sceneId: String,
        val narration: String,
        val visualPrompt: String,
        val durationSeconds: Int
    ) : StoryboardAction
}
