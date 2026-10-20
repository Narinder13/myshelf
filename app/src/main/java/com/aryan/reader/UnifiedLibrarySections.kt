package com.aryan.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aryan.reader.data.RecentFileItem
import com.aryan.reader.shared.CloudFolderSyncSelection
import com.aryan.reader.shared.SortOrder
import com.aryan.reader.shared.ui.AppIcon
import com.aryan.reader.shared.ui.SharedAndroidHomeWidthClass
import com.aryan.reader.shared.ui.sharedMobileWidthClassForWidth
import com.aryan.reader.ui.theme.MyShelfLayout
import kotlin.math.roundToInt

/**
 * Section-level composables for Library Beta.
 *
 * Split out of [UnifiedLibraryScreen] so the screen owns destination wiring while these
 * own their own layout. Every shared collaborator is injected, so the Android
 * implementation stays the benchmark for iOS parity.
 */

@Composable
internal fun unifiedShelfCountLabel(shelf: Shelf): String {
    val isFolder = shelf.type == ShelfType.FOLDER
    val folderCount = pluralStringResource(R.plurals.folder_count, shelf.childShelfCount, shelf.childShelfCount)
    val directCount = pluralStringResource(R.plurals.book_count, shelf.directBookCount, shelf.directBookCount)
    return when {
        isFolder && shelf.childShelfCount > 0 && shelf.directBookCount > 0 -> "$folderCount · $directCount"
        isFolder && shelf.childShelfCount > 0 -> folderCount
        isFolder -> directCount
        else -> pluralStringResource(R.plurals.book_count, shelf.bookCount, shelf.bookCount)
    }
}

@Composable
internal fun UnifiedFoldersSection(
    modifier: Modifier,
    folders: List<SyncedFolder>,
    allRecentFiles: List<RecentFileItem>,
    isLoading: Boolean,
    onAddFolder: () -> Unit,
    onScan: () -> Unit,
    onSyncMetadata: () -> Unit,
    onToggleLocalSync: (SyncedFolder, Boolean, Boolean) -> Unit,
    onEditFolderFilters: (SyncedFolder, Set<FileType>) -> Unit,
    onRemove: (SyncedFolder) -> Unit,
    cloudFolderSelection: CloudFolderSyncSelection? = null,
    cloudSyncEnabled: Boolean = false,
    isProUser: Boolean = false,
    onCloudFolderSettings: (() -> Unit)? = null,
    onIncomingCloudFolder: ((String) -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxSize()) {
        FolderSyncScreen(
            syncedFolders = folders,
            allRecentFiles = allRecentFiles,
            onAddFolderClick = onAddFolder,
            onRemoveFolderClick = onRemove,
            onFolderLocalSyncChange = onToggleLocalSync,
            onEditFolderFiltersClick = onEditFolderFilters,
            onScanNowClick = onScan,
            onSyncMetadataClick = onSyncMetadata,
            isLoading = isLoading,
            cloudFolderSelection = cloudFolderSelection,
            cloudSyncEnabled = cloudSyncEnabled,
            isProUser = isProUser,
            onCloudFolderSettingsClick = onCloudFolderSettings,
            onIncomingCloudFolderClick = onIncomingCloudFolder,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UnifiedLibraryControlsSheet(currentFilter: UnifiedLibraryFilter, currentSortOrder: SortOrder, onFilterChanged: (UnifiedLibraryFilter) -> Unit, onSortChanged: (SortOrder) -> Unit, onAdvancedFiltersClick: () -> Unit, onDismiss: () -> Unit) {
    com.aryan.reader.shared.ui.SharedAndroidUnifiedLibraryControlsSheet(
        currentFilter = currentFilter,
        currentSortOrder = currentSortOrder,
        strings = com.aryan.reader.shared.ui.SharedAndroidUnifiedControlsStrings(
            title = stringResource(R.string.unified_library_sort_filter),
            readStatus = stringResource(R.string.filter_read_status),
            sort = stringResource(R.string.content_desc_sort),
            advancedFilters = stringResource(R.string.filter_library),
            filterLabels = UnifiedLibraryFilter.entries.associateWith { stringResource(it.labelRes) },
            sortLabels = SortOrder.entries.associateWith { stringResource(it.labelRes) },
        ),
        onFilterChanged = onFilterChanged,
        onSortChanged = onSortChanged,
        onAdvancedFilters = onAdvancedFiltersClick,
        onDismiss = onDismiss,
    )
}

@Composable
internal fun UnifiedCreateShelfDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    com.aryan.reader.shared.ui.SharedAndroidUnifiedCreateShelfDialog(
        title = stringResource(R.string.create_new_shelf),
        nameLabel = stringResource(R.string.shelf_name_hint),
        createLabel = stringResource(R.string.action_create),
        cancelLabel = stringResource(R.string.action_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

// Hoisted: shape allocation + clip without the offscreen shadow pass the
// scrolling continue card used to pay per recomposition.
internal val ContinueReadingCoverShape = RoundedCornerShape(8.dp)

@Composable
internal fun UnifiedContinueReadingCard(
    item: RecentFileItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    usePdfFileNameAsDisplayName: Boolean = false,
) {
    val progress = item.knownReadingProgress()
    val author = item.knownCardAuthor()
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().testTag("UnifiedLibraryContinueReading"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(MyShelfLayout.pagePadding),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemedBookCover(
                item = item,
                modifier = Modifier.size(88.dp, 132.dp)
                    .clip(ContinueReadingCoverShape)
                    .testTag("UnifiedLibraryContinueReadingCover"),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(1f).testTag("UnifiedLibraryContinueReadingContent"),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    stringResource(R.string.unified_library_continue_reading),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    item.cardTitle(usePdfFileNameAsDisplayName),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (author != null) {
                    Text(
                        author,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (progress != null) {
                    Text(stringResource(R.string.progress_complete, progress.roundToInt()), style = MaterialTheme.typography.labelMedium)
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth().testTag("UnifiedLibraryContinueReadingProgress"),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        trackColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.18f),
                    )
                }
                Button(onClick = onClick) {
                    Text(stringResource(R.string.action_continue_reading))
                }
            }
        }
    }
}

@Composable
internal fun UnifiedProfileAvatar(uiState: ReaderScreenState) {
    val user = uiState.currentUser
    when {
        BuildConfig.FLAVOR != "pro" -> AppIcon(
            contentDescription = stringResource(R.string.content_desc_app_icon),
            size = 32.dp,
            shape = CircleShape,
        )
        user != null -> AndroidAccountAvatar(
            user = user,
            modifier = Modifier.size(32.dp),
            contentDescription = stringResource(R.string.content_desc_profile_picture),
        )
        else -> Icon(
            Icons.Outlined.AccountCircle,
            contentDescription = stringResource(R.string.content_desc_profile),
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun UnifiedShelvesSection(
    modifier: Modifier,
    shelves: List<Shelf>,
    selectedShelfId: String?,
    selectedBookIds: Set<String>,
    downloadingBookIds: Set<String>,
    usePdfFileNameAsDisplayName: Boolean,
    widthSizeClass: WindowWidthSizeClass,
    onShelfSelected: (Shelf) -> Unit,
    onBreadcrumbNavigate: (String?) -> Unit,
    onRenameShelf: (String) -> Unit,
    onDeleteShelf: (String) -> Unit,
    onAddBooks: (String) -> Unit,
    onBookClick: (RecentFileItem) -> Unit,
    onBookLongClick: (RecentFileItem) -> Unit,
) {
    val selectedShelf = shelves.find { it.id == selectedShelfId }
    val childShelves = remember(shelves, selectedShelf) {
        selectedShelf?.childShelfIds?.mapNotNull { childId -> shelves.find { it.id == childId } } ?: emptyList()
    }
    val breadcrumbEntries = remember(shelves, selectedShelfId) {
        com.aryan.reader.shared.ui.genericShelfBreadcrumbPath(
            currentShelfId = selectedShelfId,
            lookup = { id -> shelves.find { it.id == id } },
            idOf = { it.id },
            nameOf = { it.name },
            parentIdOf = { it.parentShelfId },
        )
    }
    if (selectedShelf == null) {
        Column(
            modifier.fillMaxSize().padding(MyShelfLayout.pagePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(stringResource(R.string.unified_library_no_shelves), color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = { onBreadcrumbNavigate(null) }) {
                Text(stringResource(R.string.unified_library_back_to_shelves))
            }
        }
        return
    }
    val gridState = rememberLazyGridState()
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val minimumCoverWidth = if (sharedMobileWidthClassForWidth(maxWidth) == SharedAndroidHomeWidthClass.COMPACT) {
            MyShelfLayout.compactBookWidth
        } else MyShelfLayout.largeBookWidth
        Column(Modifier.widthIn(max = MyShelfLayout.contentMaxWidth).fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                com.aryan.reader.shared.ui.SharedMobileShelfBreadcrumb(
                    entries = breadcrumbEntries,
                    onNavigate = { entry -> onBreadcrumbNavigate(entry.id) },
                    homeContentDescription = stringResource(R.string.tab_shelves),
                    modifier = Modifier.weight(1f).padding(start = MyShelfLayout.pagePadding),
                )
                UnifiedShelfActionsMenu(
                    canMutate = selectedShelf.type == ShelfType.MANUAL &&
                        com.aryan.reader.shared.SharedLibraryEditor.canMutateShelf(selectedShelf.id),
                    onAddBooks = { onAddBooks(selectedShelf.id) },
                    onRename = { onRenameShelf(selectedShelf.id) },
                    onDelete = { onDeleteShelf(selectedShelf.id) },
                )
            }
            Text(
                unifiedShelfCountLabel(selectedShelf),
                modifier = Modifier.padding(horizontal = MyShelfLayout.pagePadding, vertical = 12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (childShelves.isEmpty() && selectedShelf.directBooks.isEmpty()) {
                Column(
                    Modifier.weight(1f).fillMaxWidth().padding(MyShelfLayout.pagePadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(R.string.shelf_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { onAddBooks(selectedShelf.id) }) {
                        Text(stringResource(R.string.fab_add_books))
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minimumCoverWidth),
                    state = gridState,
                    modifier = Modifier.weight(1f).fillMaxWidth().testTag("MyShelfSelectedCollection"),
                    contentPadding = PaddingValues(MyShelfLayout.pagePadding, 4.dp, MyShelfLayout.pagePadding, 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(MyShelfLayout.bookSpacing),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    if (childShelves.isNotEmpty()) {
                        item(key = "folders_label", span = { GridItemSpan(maxLineSpan) }) {
                            Text(stringResource(R.string.section_folders), style = MaterialTheme.typography.titleMedium)
                        }
                        items(childShelves, key = { "shelf:${it.id}" }, span = { GridItemSpan(maxLineSpan) }) { child ->
                            MyShelfChildShelfRow(child, { onShelfSelected(child) })
                        }
                        if (selectedShelf.directBooks.isNotEmpty()) {
                            item(key = "books_label", span = { GridItemSpan(maxLineSpan) }) {
                                Text(stringResource(R.string.section_files), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                    items(selectedShelf.directBooks, key = { "book:${it.bookId}" }) { item ->
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                            RecentFileCard(
                                item = item,
                                isSelected = item.bookId in selectedBookIds,
                                modifier = Modifier.widthIn(max = MyShelfLayout.maxBookWidth).fillMaxWidth(),
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
    }
}

@Composable
private fun MyShelfChildShelfRow(shelf: Shelf, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Folder, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(shelf.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(unifiedShelfCountLabel(shelf), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/**
 * Shelf-scoped actions for the shelf currently open in Library Beta.
 *
 * Add books is always offered; rename and delete apply only to manual shelves, since
 * folder-derived shelves are owned by their synced folder.
 */
@Composable
internal fun UnifiedShelfActionsMenu(
    canMutate: Boolean,
    onAddBooks: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.testTag("UnifiedShelfActions"),
        ) { Icon(Icons.Default.MoreVert, stringResource(R.string.content_desc_more_options)) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.fab_add_books)) },
                onClick = { expanded = false; onAddBooks() },
            )
            if (canMutate) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.menu_rename_shelf)) },
                    onClick = { expanded = false; onRename() },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.menu_delete_shelf)) },
                    onClick = { expanded = false; onDelete() },
                )
            }
        }
    }
}
