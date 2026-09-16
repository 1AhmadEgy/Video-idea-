package com.example.feature.storyboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.Scene

@Composable
fun SceneCard(
    scene: Scene,
    onUpdate: (String, String, Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مشهد ${scene.position + 1}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف المشهد"
                    )
                }
            }

            OutlinedTextField(
                value = scene.narration,
                onValueChange = { onUpdate(it, scene.visualPrompt, scene.durationSeconds) },
                label = { Text("التعليق الصوتي") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = scene.visualPrompt,
                onValueChange = { onUpdate(scene.narration, it, scene.durationSeconds) },
                label = { Text("وصف الصورة") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text(
                text = "المدة: ${scene.durationSeconds} ثوانٍ",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
