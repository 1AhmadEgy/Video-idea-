package com.example.feature.storyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feature.storyboard.components.SceneCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryboardScreen(
    state: StoryboardUiState,
    onAction: (StoryboardAction) -> Unit,
    onBack: () -> Unit,
    onNavigateToEditor: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.project?.title ?: "لوحة القصة") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "العودة"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.scenes.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = onNavigateToEditor,
                        icon = { Icon(Icons.Default.PlayArrow, "متابعة") },
                        text = { Text("متابعة للمحرر") }
                    )
                }

                if (state.scenes.isEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = { 
                            if (!state.isGenerating) {
                                onAction(StoryboardAction.GenerateScript) 
                            }
                        },
                        icon = { 
                            if (state.isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                Icon(Icons.Default.AutoAwesome, "توليد") 
                            }
                        },
                        text = { 
                            Text(if (state.isGenerating) "جارٍ التوليد..." else "توليد المشاهد بالذكاء الاصطناعي") 
                        }
                    )
                } else {
                    ExtendedFloatingActionButton(
                        onClick = { onAction(StoryboardAction.AddScene(state.scenes.size)) },
                        icon = { Icon(Icons.Default.Add, "إضافة") },
                        text = { Text("إضافة مشهد") }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = state.project?.idea ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                state.error?.let { error ->
                    item {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                items(
                    items = state.scenes,
                    key = { it.id }
                ) { scene ->
                    SceneCard(
                        scene = scene,
                        onUpdate = { narration, prompt, duration ->
                            onAction(
                                StoryboardAction.UpdateScene(
                                    scene.id,
                                    narration,
                                    prompt,
                                    duration
                                )
                            )
                        },
                        onDelete = {
                            onAction(StoryboardAction.DeleteScene(scene.id))
                        }
                    )
                }
                
                // Add padding at the bottom so the FABs don't hide the last item
                item {
                    Box(modifier = Modifier.padding(120.dp))
                }
            }
        }
    }
}
