package com.example.feature.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun DurationOptions(
    selected: Int,
    enabled: Boolean,
    onSelected: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(15, 30, 60).forEach { duration ->
            FilterChip(
                selected = selected == duration,
                onClick = { onSelected(duration) },
                enabled = enabled,
                label = { Text("${duration}ث") }
            )
        }
    }
}
