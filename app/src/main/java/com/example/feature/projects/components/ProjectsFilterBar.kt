package com.example.feature.projects.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.ProjectStatus

enum class ProjectSortType(val label: String) {
    LAST_UPDATED("آخر تعديل"),
    CREATED_DATE("تاريخ الإنشاء"),
    NAME("الاسم")
}

@Composable
fun ProjectsFilterBar(
    selectedStatus: ProjectStatus?,
    sortType: ProjectSortType,
    onStatusSelect: (ProjectStatus?) -> Unit,
    onSortSelect: (ProjectSortType) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusSelect(null) },
                label = { Text("الكل") }
            )

            ProjectStatus.entries.forEach { status ->
                val label = when (status) {
                    ProjectStatus.DRAFT -> "مسودة"
                    ProjectStatus.SCRIPT_GENERATING -> "كتابة السيناريو"
                    ProjectStatus.SCRIPT_READY -> "جاهز للتعديل"
                    ProjectStatus.READY_TO_RENDER -> "جاهز للرندر"
                    ProjectStatus.RENDERING -> "قيد الرندر"
                    ProjectStatus.COMPLETED -> "مكتمل"
                    ProjectStatus.FAILED -> "فشل"
                }

                FilterChip(
                    selected = selectedStatus == status,
                    onClick = {
                        onStatusSelect(if (selectedStatus == status) null else status)
                    },
                    label = { Text(label) }
                )
            }
        }

        // Sort Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "ترتيب",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "ترتيب حسب:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ProjectSortType.entries.forEach { type ->
                    SuggestionChip(
                        onClick = { onSortSelect(type) },
                        label = {
                            Text(
                                text = type.label,
                                color = if (sortType == type) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    )
                }
            }

            if (selectedStatus != null) {
                TextButton(onClick = onClearFilters) {
                    Text(
                        text = "مسح الفلتر",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
