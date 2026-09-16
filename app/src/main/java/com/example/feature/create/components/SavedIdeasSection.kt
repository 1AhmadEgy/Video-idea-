package com.example.feature.create.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.Idea
import com.example.domain.model.IdeaCategory

@Composable
fun SavedIdeasSection(
    ideas: List<Idea>,
    filterOnlyFavorites: Boolean,
    selectedFilterTag: String,
    onToggleFavoritesFilter: (Boolean) -> Unit,
    onSelectTagFilter: (String) -> Unit,
    onOpenIdeaDetails: (Idea) -> Unit,
    onSelectIdea: (Idea) -> Unit,
    onToggleFavorite: (Idea) -> Unit,
    onShareIdea: (Idea) -> Unit,
    onDeleteIdea: (Idea) -> Unit,
    onExportBackupJson: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter ideas by favorites and tag
    val displayedIdeas = ideas.filter { idea ->
        val matchesFavorite = !filterOnlyFavorites || idea.isFavorite
        val matchesTag = selectedFilterTag == "الكل" || selectedFilterTag.equals("All", ignoreCase = true) ||
                idea.tag.equals(selectedFilterTag, ignoreCase = true)
        matchesFavorite && matchesTag
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Title & Export Backup Button
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
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "الأفكار المحفوظة (${ideas.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Export JSON Backup Button
            OutlinedButton(
                onClick = onExportBackupJson,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
                Text("نسخة احتياطية (JSON)", style = MaterialTheme.typography.labelSmall)
            }
        }

        // Tag Filters & Favorites Filter Bar (Horizontally scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // All Tag
            FilterChip(
                selected = selectedFilterTag == "الكل" && !filterOnlyFavorites,
                onClick = {
                    onSelectTagFilter("الكل")
                    if (filterOnlyFavorites) onToggleFavoritesFilter(false)
                },
                label = { Text("الكل") }
            )

            // Favorites Only Chip
            FilterChip(
                selected = filterOnlyFavorites,
                onClick = { onToggleFavoritesFilter(!filterOnlyFavorites) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(16.dp)
                    )
                },
                label = { Text("المفضلة") }
            )

            // Tag categories: Shorts, Tutorial, Vlog, Story, Promo
            IdeaCategory.availableTags.forEach { category ->
                FilterChip(
                    selected = selectedFilterTag.equals(category.key, ignoreCase = true),
                    onClick = { onSelectTagFilter(category.key) },
                    label = { Text(category.labelAr) }
                )
            }
        }

        if (displayedIdeas.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (filterOnlyFavorites) Icons.Default.Favorite else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = when {
                            filterOnlyFavorites -> "لم يتم العثور على أفكار في المفضلة لهذا التصنيف"
                            selectedFilterTag != "الكل" -> "لا توجد أفكار محفوظة في تصنيف '${IdeaCategory.getDisplayName(selectedFilterTag)}'"
                            else -> "لا توجد أفكار محفوظة بعد، ستُحفظ أفكارك هنا تلقائياً"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                displayedIdeas.forEach { idea ->
                    SavedIdeaCard(
                        idea = idea,
                        onOpenDetails = { onOpenIdeaDetails(idea) },
                        onSelect = { onSelectIdea(idea) },
                        onToggleFavorite = { onToggleFavorite(idea) },
                        onShare = { onShareIdea(idea) },
                        onDelete = { onDeleteIdea(idea) }
                    )
                }
            }
        }
    }
}
