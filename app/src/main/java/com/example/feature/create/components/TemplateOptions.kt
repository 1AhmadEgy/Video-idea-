package com.example.feature.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun TemplateOptions(
    selected: String,
    enabled: Boolean,
    onSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "educational" to "تعليمي",
            "story" to "قصة قصيرة",
            "advertisement" to "إعلان"
        ).forEach { (value, label) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelected(value) },
                enabled = enabled,
                label = { Text(label) }
            )
        }
    }
}
