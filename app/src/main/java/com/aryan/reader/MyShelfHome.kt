package com.aryan.reader

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aryan.reader.data.RecentFileItem
import com.aryan.reader.shared.ui.SharedAndroidHomeWidthClass
import com.aryan.reader.shared.ui.sharedMobileWidthClassForWidth
import com.aryan.reader.ui.theme.AppTheme
import com.aryan.reader.ui.theme.MyShelfLayout
import com.aryan.reader.ui.theme.MyShelfLibraryTheme

/** A projection of the existing library rows, never a separate source of library state. */
internal fun recentlyAddedMyShelfBooks(books: List<RecentFileItem>, limit: Int = 12): List<RecentFileItem> =
    books.asSequence()
        .filterNot { it.isDeleted }
        .sortedWith(compareByDescending<RecentFileItem> { it.dateAddedTimestamp }.thenBy { it.bookId })
        .take(limit.coerceAtLeast(0))
        .toList()

/** Android's reading-room entry point; the complete library remains a separate browse view. */
@Composable
internal fun MyShelfHome(
    allBooks: List<RecentFileItem>,
    continueReading: RecentFileItem?,
    shelves: List<Shelf>,
    isLoading: Boolean,
    downloadingBookIds: Set<String>,
    usePdfFileNameAsDisplayName: Boolean,
    onBookClick: (RecentFileItem) -> Unit,
    onBookLongClick: (RecentFileItem) -> Unit,
    onViewLibrary: () -> Unit,
    onShelfSelected: (Shelf) -> Unit,
    onCreateShelf: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
    selectedBookIds: Set<String> = emptySet(),
    pinnedBookIds: Set<String> = emptySet(),
    openTabsContent: @Composable () -> Unit = {},
) {
    val recentBooks = remember(allBooks) { recentlyAddedMyShelfBooks(allBooks) }
    val collections = remember(shelves) {
        shelves.filter { it.type == ShelfType.MANUAL && it.parentShelfId == null }
    }
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val widthClass = sharedMobileWidthClassForWidth(maxWidth)
        val gutter = MyShelfLayout.pagePadding
        val bookWidth = if (widthClass == SharedAndroidHomeWidthClass.COMPACT) MyShelfLayout.compactBookWidth else MyShelfLayout.largeBookWidth
        LazyColumn(
            modifier = Modifier.widthIn(max = MyShelfLayout.contentMaxWidth).fillMaxWidth().testTag("MyShelfHome"),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item(key = "welcome") {
                Column(Modifier.padding(horizontal = gutter).padding(top = 16.dp)) {
                    Text(
                        stringResource(R.string.myshelf_home_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontFamily = FontFamily.Serif,
                    )
                    Text(
                        stringResource(R.string.myshelf_home_subtitle),
                        modifier = Modifier.padding(top = 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    openTabsContent()
                }
            }
            if (isLoading) {
                item(key = "loading") {
                    Column(Modifier.padding(horizontal = gutter)) {
                        Text(stringResource(R.string.myshelf_loading), style = MaterialTheme.typography.bodyMedium)
                        LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
                    }
                }
            }
            if (continueReading != null) {
                item(key = "continue") {
                    UnifiedContinueReadingCard(
                        item = continueReading,
                        onClick = { onBookClick(continueReading) },
                        modifier = Modifier.padding(horizontal = gutter),
                        usePdfFileNameAsDisplayName = usePdfFileNameAsDisplayName,
                    )
                }
            }
            if (recentBooks.isEmpty() && !isLoading) {
                item(key = "empty_library") {
                    MyShelfEmptyLibrary(onImport, Modifier.padding(horizontal = gutter))
                }
            } else if (recentBooks.isNotEmpty()) {
                item(key = "recently_added") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MyShelfSectionHeading(
                            title = stringResource(R.string.myshelf_recently_added),
                            actionLabel = stringResource(R.string.myshelf_view_all),
                            onAction = onViewLibrary,
                            modifier = Modifier.padding(horizontal = gutter),
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = gutter),
                            horizontalArrangement = Arrangement.spacedBy(MyShelfLayout.bookSpacing),
                        ) {
                            items(recentBooks, key = { it.bookId }) { item ->
                                RecentFileCard(
                                    item = item,
                                    isSelected = item.bookId in selectedBookIds,
                                    isPinned = item.bookId in pinnedBookIds,
                                    modifier = Modifier.width(bookWidth),
                                    onClick = { onBookClick(item) },
                                    onLongClick = { onBookLongClick(item) },
                                    isDownloading = item.bookId in downloadingBookIds,
                                    usePdfFileNameAsDisplayName = usePdfFileNameAsDisplayName,
                                )
                            }
                        }
                    }
                }
            }
            item(key = "collections") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MyShelfSectionHeading(
                        title = stringResource(R.string.myshelf_your_collections),
                        actionLabel = if (collections.isNotEmpty()) stringResource(R.string.myshelf_new_collection) else null,
                        onAction = onCreateShelf,
                        modifier = Modifier.padding(horizontal = gutter),
                    )
                    if (collections.isEmpty()) {
                        MyShelfEmptyCollections(onCreateShelf, Modifier.padding(horizontal = gutter))
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = gutter),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(collections, key = { it.id }) { shelf ->
                                MyShelfCollectionCard(
                                    shelf = shelf,
                                    onClick = { onShelfSelected(shelf) },
                                    modifier = Modifier.width(252.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** The root collection surface also keeps folder/series shelves that existed upstream. */
@Composable
internal fun MyShelfCollections(
    shelves: List<Shelf>,
    onShelfSelected: (Shelf) -> Unit,
    onCreateShelf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleShelves = remember(shelves) {
        shelves.filter { it.type != ShelfType.TAG && it.parentShelfId == null }
    }
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val gutter = MyShelfLayout.pagePadding
        LazyVerticalGrid(
            columns = GridCells.Adaptive(240.dp),
            modifier = Modifier.widthIn(max = MyShelfLayout.contentMaxWidth).fillMaxWidth().testTag("MyShelfCollections"),
            contentPadding = PaddingValues(start = gutter, end = gutter, top = 16.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(MyShelfLayout.bookSpacing),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "collection_heading", span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.myshelf_your_collections),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = FontFamily.Serif,
                        )
                        if (visibleShelves.isNotEmpty()) {
                            TextButton(onClick = onCreateShelf) { Text(stringResource(R.string.myshelf_new_collection)) }
                        }
                    }
                    Text(
                        stringResource(R.string.myshelf_collections_subtitle),
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (visibleShelves.isEmpty()) {
                item(key = "empty_collections", span = { GridItemSpan(maxLineSpan) }) {
                    MyShelfEmptyCollections(onCreateShelf)
                }
            } else {
                items(visibleShelves, key = { it.id }) { shelf ->
                    MyShelfCollectionCard(shelf, { onShelfSelected(shelf) })
                }
            }
        }
    }
}

@Composable
private fun MyShelfSectionHeading(title: String, actionLabel: String?, onAction: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif,
        )
        if (actionLabel != null) TextButton(onClick = onAction) { Text(actionLabel) }
    }
}

@Composable
private fun MyShelfEmptyLibrary(onImport: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.AutoMirrored.Filled.LibraryBooks, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.myshelf_empty_title), style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
            Text(stringResource(R.string.myshelf_empty_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onImport) {
                Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.fab_add_books))
            }
        }
    }
}

@Composable
private fun MyShelfEmptyCollections(onCreateShelf: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.myshelf_collections_empty_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.myshelf_collections_empty_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = onCreateShelf) { Text(stringResource(R.string.myshelf_new_collection)) }
        }
    }
}

@Composable
private fun MyShelfCollectionCard(shelf: Shelf, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val previewBooks = remember(shelf.books) { shelf.books.take(3) }
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().testTag("MyShelfCollection_${shelf.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().height(88.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (previewBooks.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                        Icon(Icons.Default.Folder, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    previewBooks.forEachIndexed { index, book ->
                        ThemedBookCover(
                            item = book,
                            modifier = Modifier.width(52.dp).height(if (index == 1) 88.dp else 80.dp).clip(RoundedCornerShape(4.dp)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
            Text(
                shelf.name,
                modifier = Modifier.padding(top = 14.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(unifiedShelfCountLabel(shelf), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(name = "Home · compact", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(name = "Home · unfolded", showBackground = true, widthDp = 840, heightDp = 900)
@Preview(name = "Home · dark", showBackground = true, widthDp = 360, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Home · large text", showBackground = true, widthDp = 360, heightDp = 900, fontScale = 1.5f)
@Composable
private fun MyShelfHomePreview() {
    val books = remember {
        listOf(
            RecentFileItem("sample-1", null, FileType.PDF, "A sample book with a long title", 2L, progressPercentage = 36f),
            RecentFileItem("sample-2", null, FileType.EPUB, "Another sample book", 1L),
        )
    }
    AppTheme(dynamicColor = false) {
        MyShelfLibraryTheme {
            Surface {
                MyShelfHome(
                    allBooks = books,
                    continueReading = books.first(),
                    shelves = listOf(Shelf("preview-shelf", "Weekend reads", ShelfType.MANUAL, books)),
                    isLoading = false,
                    downloadingBookIds = emptySet(),
                    usePdfFileNameAsDisplayName = false,
                    onBookClick = {},
                    onBookLongClick = {},
                    onViewLibrary = {},
                    onShelfSelected = {},
                    onCreateShelf = {},
                    onImport = {},
                )
            }
        }
    }
}
