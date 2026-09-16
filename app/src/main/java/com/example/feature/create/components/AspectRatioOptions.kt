package com.example.feature.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun AspectRatioOptions(
    selected: String,
    enabled: Boolean,
    onSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "9:16" to "عمودي",
            "1:1" to "مربع",
            "16:9" to "أفقي"
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
