package com.example.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.core.database.entity.SceneEntity

@Composable
fun SceneEditorCard(
    scene: SceneEntity,
    onChange: (SceneEntity) -> Unit,
    onDelete: (SceneEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "المشهد ${scene.position + 1}")

            OutlinedTextField(
                value = scene.visualPrompt,
                onValueChange = { onChange(scene.copy(visualPrompt = it)) },
                label = { Text("Prompt الصورة") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = scene.narration,
                onValueChange = { onChange(scene.copy(narration = it)) },
                label = { Text("التعليق الصوتي") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = scene.durationSeconds.toString(),
                onValueChange = { value ->
                    val duration = value.toIntOrNull() ?: scene.durationSeconds
                    onChange(scene.copy(durationSeconds = duration))
                },
                label = { Text("المدة بالثواني") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = { onDelete(scene) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "حذف المشهد")
            }
        }
    }
}
