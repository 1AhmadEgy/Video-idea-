package com.example.feature.projects.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.Project

@Composable
fun EditProjectDialog(
    project: Project,
    onConfirm: (newTitle: String, newIdea: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(project.title) }
    var idea by remember { mutableStateOf(project.idea) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "تعديل تفاصيل المشروع")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المشروع") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = idea,
                    onValueChange = { idea = it },
                    label = { Text("فكرة المشروع") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), idea.trim())
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(text = "حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "إلغاء")
            }
        }
    )
}
