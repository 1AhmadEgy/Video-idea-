package com.example.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.database.entity.SceneEntity

@Composable
fun EditorRoute(
    onBack: () -> Unit,
    onNavigateToRender: (String) -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    EditorScreen(
        state = state,
        onBack = onBack,
        onVoiceSelected = viewModel::selectVoice,
        onStyleSelected = viewModel::selectStyle,
        onUpdateIdea = viewModel::updateProjectIdea,
        onGenerateWithAi = viewModel::generateScriptWithAi,
        onAddScene = viewModel::addScene,
        onUpdateScene = viewModel::updateScene,
        onDeleteScene = viewModel::deleteScene,
        onStartRender = {
            viewModel.saveSettingsAndContinue {
                onNavigateToRender(viewModel.projectId)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    state: EditorUiState,
    onBack: () -> Unit,
    onVoiceSelected: (String) -> Unit,
    onStyleSelected: (String) -> Unit,
    onUpdateIdea: (String) -> Unit,
    onGenerateWithAi: () -> Unit,
    onAddScene: () -> Unit,
    onUpdateScene: (SceneEntity) -> Unit,
    onDeleteScene: (SceneEntity) -> Unit,
    onStartRender: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.project?.title ?: "المحرر النهائي") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.scenes.isNotEmpty() && !state.isGenerating) {
                ExtendedFloatingActionButton(
                    onClick = onStartRender,
                    icon = { Icon(Icons.Default.PlayArrow, "رندر") },
                    text = { Text("بدء الرندر") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = state.project?.idea ?: "",
                            onValueChange = onUpdateIdea,
                            label = { Text("فكرة الفيديو") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Button(
                            onClick = onGenerateWithAi,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isGenerating
                        ) {
                            if (state.isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري التوليد...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("توليد السيناريو بالذكاء الاصطناعي")
                            }
                        }
                        if (state.error != null) {
                            Text(text = state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("المعلق الصوتي", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
                                SelectionCard("صوت نسائي (عربي)", state.selectedVoice == "ar-female-1") { onVoiceSelected("ar-female-1") }
                                Spacer(modifier = Modifier.height(8.dp))
                                SelectionCard("صوت رجالي (عربي)", state.selectedVoice == "ar-male-1") { onVoiceSelected("ar-male-1") }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("النمط البصري", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
                                SelectionCard("واقعي سينمائي", state.selectedImageStyle == "cinematic") { onStyleSelected("cinematic") }
                                Spacer(modifier = Modifier.height(8.dp))
                                SelectionCard("رسوم متحركة 3D", state.selectedImageStyle == "3d-animation") { onStyleSelected("3d-animation") }
                            }
                        }
                    }

                    item {
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "المشاهد",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            OutlinedButton(onClick = onAddScene) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إضافة مشهد")
                            }
                        }
                    }

                    if (state.scenes.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("لا يوجد مشاهد مضافة", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(state.scenes, key = { it.id }) { scene ->
                            SceneEditorCard(
                                scene = scene,
                                onChange = onUpdateScene,
                                onDelete = onDeleteScene
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
