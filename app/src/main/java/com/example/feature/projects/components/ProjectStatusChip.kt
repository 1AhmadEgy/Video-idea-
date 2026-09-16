package com.example.feature.projects.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.ProjectStatus

@Composable
fun ProjectStatusChip(
    status: ProjectStatus,
    modifier: Modifier = Modifier
) {
    val style = status.toChipStyle()

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(style.containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(style.dotColor)
        )

        Text(
            text = style.label,
            color = style.contentColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

private data class StatusChipStyle(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val dotColor: Color
)

@Composable
private fun ProjectStatus.toChipStyle(): StatusChipStyle {
    val colors = MaterialTheme.colorScheme

    return when (this) {
        ProjectStatus.DRAFT -> StatusChipStyle(
            label = "مسودة",
            containerColor = colors.surfaceVariant,
            contentColor = colors.onSurfaceVariant,
            dotColor = colors.outline
        )
        ProjectStatus.SCRIPT_GENERATING -> StatusChipStyle(
            label = "كتابة السيناريو...",
            containerColor = colors.secondaryContainer,
            contentColor = colors.onSecondaryContainer,
            dotColor = colors.secondary
        )
        ProjectStatus.SCRIPT_READY -> StatusChipStyle(
            label = "السيناريو جاهز",
            containerColor = colors.tertiaryContainer,
            contentColor = colors.onTertiaryContainer,
            dotColor = colors.tertiary
        )
        ProjectStatus.READY_TO_RENDER -> StatusChipStyle(
            label = "جاهز للرندر",
            containerColor = colors.primaryContainer.copy(alpha = 0.7f),
            contentColor = colors.onPrimaryContainer,
            dotColor = colors.primary
        )
        ProjectStatus.RENDERING -> StatusChipStyle(
            label = "جارٍ الرندر...",
            containerColor = colors.secondaryContainer,
            contentColor = colors.onSecondaryContainer,
            dotColor = colors.secondary
        )
        ProjectStatus.COMPLETED -> StatusChipStyle(
            label = "مكتمل",
            containerColor = Color(0xFFE8F5E9),
            contentColor = Color(0xFF1B5E20),
            dotColor = Color(0xFF2E7D32)
        )
        ProjectStatus.FAILED -> StatusChipStyle(
            label = "فشل",
            containerColor = colors.errorContainer,
            contentColor = colors.onErrorContainer,
            dotColor = colors.error
        )
    }
}
