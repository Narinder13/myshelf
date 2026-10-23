package com.aryan.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aryan.reader.data.RecentFileItem
import com.aryan.reader.shared.ui.matchesMobileUnifiedLibraryFilter
import com.aryan.reader.ui.theme.MyShelfLayout

private val homeStatusFilters = listOf(
    UnifiedLibraryFilter.READING,
    UnifiedLibraryFilter.UNREAD,
    UnifiedLibraryFilter.FINISHED,
)

/** Counts use the Library's exact predicate, without decoding reader annotations. */
internal fun myShelfHomeStatusCounts(books: List<RecentFileItem>): Map<UnifiedLibraryFilter, Int> {
    val counts = homeStatusFilters.associateWith { 0 }.toMutableMap()
    books.asSequence().filterNot { it.isDeleted }.forEach { item ->
        val book = item.toSharedProjectionBookItem()
        homeStatusFilters.forEach { filter ->
            if (book.matchesMobileUnifiedLibraryFilter(filter, normalizedQuery = "")) {
                counts[filter] = counts.getValue(filter) + 1
            }
        }
    }
    return counts
}

/** Pins already persist in the library; this is only a convenient Home projection. */
internal fun pinnedMyShelfBooks(books: List<RecentFileItem>, pinnedBookIds: Set<String>): List<RecentFileItem> =
    books.asSequence()
        .filter { !it.isDeleted && it.bookId in pinnedBookIds }
        .sortedWith(compareByDescending<RecentFileItem> { it.timestamp }.thenBy { it.bookId })
        .toList()

@Composable
internal fun MyShelfHomeQuickAccess(
    counts: Map<UnifiedLibraryFilter, Int>,
    onBrowseFilter: (UnifiedLibraryFilter) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    LazyRow(
        contentPadding = PaddingValues(horizontal = MyShelfLayout.pagePadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("MyShelfHomeQuickAccess"),
    ) {
        items(homeStatusFilters, key = { it.name }) { filter ->
            val (container, foreground) = when (filter) {
                UnifiedLibraryFilter.READING -> colors.primaryContainer to colors.onPrimaryContainer
                UnifiedLibraryFilter.UNREAD -> colors.secondaryContainer to colors.onSecondaryContainer
                else -> colors.tertiaryContainer to colors.onTertiaryContainer
            }
            val icon = when (filter) {
                UnifiedLibraryFilter.READING -> Icons.AutoMirrored.Filled.MenuBook
                UnifiedLibraryFilter.UNREAD -> Icons.Default.BookmarkBorder
                else -> Icons.Default.Check
            }
            AssistChip(
                onClick = { onBrowseFilter(filter) },
                label = {
                    Text(stringResource(R.string.myshelf_home_status_count, stringResource(filter.labelRes), counts[filter] ?: 0))
                },
                leadingIcon = { Icon(icon, null, Modifier.size(18.dp)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = container,
                    labelColor = foreground,
                    leadingIconContentColor = foreground,
                ),
                border = null,
                modifier = Modifier.heightIn(min = 48.dp).testTag("MyShelfHomeStatus_${filter.name}"),
            )
        }
    }
}
