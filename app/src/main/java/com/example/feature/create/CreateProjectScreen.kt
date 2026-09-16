package com.example.feature.create

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.domain.model.IdeaCategory
import com.example.feature.create.components.AspectRatioOptions
import com.example.feature.create.components.DeleteIdeaDialog
import com.example.feature.create.components.DurationOptions
import com.example.feature.create.components.ExportBackupDialog
import com.example.feature.create.components.IdeaDetailBottomSheet
import com.example.feature.create.components.IdeaTagSelector
import com.example.feature.create.components.SavedIdeasSection
import com.example.feature.create.components.TemplateOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    state: CreateProjectUiState,
    onAction: (CreateProjectAction) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // SAF File Saver for JSON Backup
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null && state.exportedJsonContent != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(state.exportedJsonContent.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "تم حفظ ملف النسخة الاحتياطية بنجاح على جهازك", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "تعذر حفظ الملف: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            onAction(CreateProjectAction.ClearExportedJson)
        }
    }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onAction(CreateProjectAction.ClearSnackbarMessage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إنشاء فيديو") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الرجوع")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "اكتب فكرة الفيديو",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = state.idea,
                onValueChange = { onAction(CreateProjectAction.IdeaChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("الفكرة") },
                placeholder = { Text("مثال: 5 عادات صباحية لتغيير حياتك وزيادة الإنتاجية") },
                minLines = 4,
                enabled = !state.isSubmitting
            )

            // Category Tag Selector for the current idea
            IdeaTagSelector(
                selectedTag = state.tag,
                enabled = !state.isSubmitting,
                onTagSelected = { onAction(CreateProjectAction.TagChanged(it)) }
            )

            // Button to save current idea explicitly
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onAction(CreateProjectAction.SaveCurrentIdea) },
                    enabled = state.idea.isNotBlank() && !state.isSubmitting
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 4.dp)
                    )
                    Text("حفظ كفكرة")
                }
            }

            // Saved Ideas Section with Tag Filtering & Export
            if (state.savedIdeas.isNotEmpty()) {
                SavedIdeasSection(
                    ideas = state.savedIdeas,
                    filterOnlyFavorites = state.filterOnlyFavorites,
                    selectedFilterTag = state.selectedFilterTag,
                    onToggleFavoritesFilter = { onAction(CreateProjectAction.SetFavoritesFilter(it)) },
                    onSelectTagFilter = { onAction(CreateProjectAction.SetTagFilter(it)) },
                    onOpenIdeaDetails = { onAction(CreateProjectAction.OpenIdeaDetails(it)) },
                    onSelectIdea = { onAction(CreateProjectAction.SelectSavedIdea(it)) },
                    onToggleFavorite = {
                        onAction(CreateProjectAction.ToggleFavorite(it.id, !it.isFavorite))
                    },
                    onShareIdea = { idea ->
                        val formattedText = buildString {
                            appendLine("🎬 فكرة فيديو بالذكاء الاصطناعي:")
                            appendLine("\"${idea.text}\"")
                            appendLine("🏷️ التصنيف: ${IdeaCategory.getDisplayName(idea.tag)}")
                            appendLine()
                            append("✨ تم إنشاؤها وتنسيقها عبر فكرة فيديو (Fikra Video)")
                        }

                        // Copy to clipboard
                        clipboardManager.setText(AnnotatedString(formattedText))

                        // Launch Android Share Sheet
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, formattedText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "مشاركة فكرة الفيديو")
                        context.startActivity(shareIntent)
                    },
                    onDeleteIdea = { onAction(CreateProjectAction.RequestDeleteIdea(it)) },
                    onExportBackupJson = { onAction(CreateProjectAction.ExportBackupJson) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            Text(text = "القالب", style = MaterialTheme.typography.titleMedium)
            TemplateOptions(
                selected = state.template,
                enabled = !state.isSubmitting,
                onSelected = { onAction(CreateProjectAction.TemplateChanged(it)) }
            )

            Text(text = "المدة", style = MaterialTheme.typography.titleMedium)
            DurationOptions(
                selected = state.durationSeconds,
                enabled = !state.isSubmitting,
                onSelected = { onAction(CreateProjectAction.DurationChanged(it)) }
            )

            Text(text = "مقاس الفيديو", style = MaterialTheme.typography.titleMedium)
            AspectRatioOptions(
                selected = state.aspectRatio,
                enabled = !state.isSubmitting,
                onSelected = { onAction(CreateProjectAction.AspectRatioChanged(it)) }
            )

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = { onAction(CreateProjectAction.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("إنشاء المشروع")
                }
            }
        }
    }

    // Bottom Sheet: Displays full generated script and details when tapped
    state.selectedIdeaForDetails?.let { idea ->
        IdeaDetailBottomSheet(
            idea = idea,
            onDismiss = { onAction(CreateProjectAction.CloseIdeaDetails) },
            onUseIdea = { onAction(CreateProjectAction.SelectSavedIdea(idea)) },
            onToggleFavorite = { onAction(CreateProjectAction.ToggleFavorite(idea.id, !idea.isFavorite)) },
            onUpdateTag = { newTag -> onAction(CreateProjectAction.UpdateIdeaTag(idea.id, newTag)) },
            onShare = {
                val formatted = buildString {
                    appendLine("🎬 فكرة فيديو: ${idea.text}")
                    appendLine("🏷️ التصنيف: ${IdeaCategory.getDisplayName(idea.tag)}")
                    if (!idea.script.isNullOrBlank()) {
                        appendLine()
                        appendLine("📜 السيناريو الكامل:")
                        appendLine(idea.script)
                    }
                    appendLine()
                    append("✨ تطبيق فكرة فيديو (Fikra Video)")
                }
                clipboardManager.setText(AnnotatedString(formatted))
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, formatted)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "مشاركة الفكرة والسيناريو"))
            },
            onDelete = { onAction(CreateProjectAction.RequestDeleteIdea(idea)) }
        )
    }

    // Dialog: Export Backup JSON
    state.exportedJsonContent?.let { json ->
        ExportBackupDialog(
            jsonContent = json,
            onSaveToDevice = {
                createDocumentLauncher.launch("fikra_ideas_backup_${System.currentTimeMillis()}.json")
            },
            onShareBackup = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, json)
                    putExtra(Intent.EXTRA_TITLE, "نسخة احتياطية للأفكار (JSON)")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "مشاركة ملف النسخة الاحتياطية"))
                onAction(CreateProjectAction.ClearExportedJson)
            },
            onCopyJson = {
                clipboardManager.setText(AnnotatedString(json))
                Toast.makeText(context, "تم نسخ محتوى JSON إلى الحافظة بنجاح", Toast.LENGTH_SHORT).show()
                onAction(CreateProjectAction.ClearExportedJson)
            },
            onDismiss = { onAction(CreateProjectAction.ClearExportedJson) }
        )
    }

    // Confirmation Dialog before deleting an idea
    state.ideaToDelete?.let { idea ->
        DeleteIdeaDialog(
            idea = idea,
            onConfirm = { onAction(CreateProjectAction.ConfirmDeleteIdea) },
            onDismiss = { onAction(CreateProjectAction.DismissDeleteDialog) }
        )
    }
}
