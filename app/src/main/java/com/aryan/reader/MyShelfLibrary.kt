package com.aryan.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aryan.reader.data.RecentFileItem
import com.aryan.reader.shared.ui.SharedAndroidHomeWidthClass
import com.aryan.reader.shared.ui.sharedMobileWidthClassForWidth
import com.aryan.reader.ui.theme.MyShelfLayout

internal enum class MyShelfDestination { HOME, LIBRARY, COLLECTIONS }

/** All three primary destinations drive the existing unified section/filter state. */
@Composable
internal fun MyShelfNavigation(
    destination: MyShelfDestination?,
    useRail: Boolean,
    onNavigate: (MyShelfDestination) -> Unit,
) {
    val labels = listOf(
        stringResource(R.string.myshelf_home),
        stringResource(R.string.myshelf_library),
        stringResource(R.string.myshelf_collections),
    )
    val icons = listOf(Icons.Default.Home, Icons.AutoMirrored.Filled.LibraryBooks, Icons.Default.Folder)
    if (useRail) {
        NavigationRail(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
            MyShelfDestination.entries.forEachIndexed { index, item ->
                NavigationRailItem(
                    selected = destination == item,
                    onClick = { onNavigate(item) },
                    icon = { Icon(icons[index], null) },
                    label = { Text(labels[index]) },
                    modifier = Modifier.testTag("MyShelfNav_${item.name}"),
                )
            }
        }
    } else {
        NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
            MyShelfDestination.entries.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = destination == item,
                    onClick = { onNavigate(item) },
                    icon = { Icon(icons[index], null) },
                    label = { Text(labels[index], maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    modifier = Modifier.testTag("MyShelfNav_${item.name}"),
                )
            }
        }
    }
}

@Composable
internal fun MyShelfTopBar(
    title: String,
    showingShelf: Boolean,
    showBookActions: Boolean,
    searchExpanded: Boolean,
    query: String,
    onMenu: () -> Unit,
    onBackFromShelf: () -> Unit,
    onSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onQueryChange: (String) -> Unit,
    onImport: () -> Unit,
    onAccount: () -> Unit,
    accountAvatar: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(searchExpanded, showBookActions) {
        if (searchExpanded && showBookActions) focusRequester.requestFocus()
    }
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().statusBarsPadding()) {
            Row(
                Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = if (showingShelf) onBackFromShelf else onMenu) {
                    Icon(
                        if (showingShelf) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                        stringResource(if (showingShelf) R.string.action_back else R.string.myshelf_more),
                    )
                }
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (showBookActions) {
                    IconButton(onClick = onSearch) { Icon(Icons.Default.Search, stringResource(R.string.unified_library_search_books)) }
                    FilledTonalIconButton(onClick = onImport, modifier = Modifier.testTag("UnifiedLibraryImport")) {
                        Icon(Icons.Default.Add, stringResource(R.string.fab_add_books))
                    }
                }
                IconButton(onClick = onAccount, modifier = Modifier.testTag("UnifiedLibraryProfile")) { accountAvatar() }
            }
            if (searchExpanded && showBookActions) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = MyShelfLayout.pagePadding)
                        .padding(bottom = 12.dp).focusRequester(focusRequester).testTag("UnifiedLibrarySearch"),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text(stringResource(R.string.unified_library_search_books)) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (query.isNotEmpty()) onQueryChange("") else {
                                focusManager.clearFocus()
                                onCloseSearch()
                            }
                        }) {
                            Icon(Icons.Default.Close, stringResource(if (query.isNotEmpty()) R.string.content_desc_clear_query else R.string.myshelf_close_search))
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    singleLine = true,
                )
            }
        }
    }
}

@Composable
internal fun MyShelfLibrary(
    books: List<RecentFileItem>,
    totalBookCount: Int,
    filter: UnifiedLibraryFilter,
    sortOrder: SortOrder,
    advancedFilterCount: Int,
    useListView: Boolean,
    isLoading: Boolean,
    selectedBookIds: Set<String>,
    pinnedBookIds: Set<String>,
    downloadingBookIds: Set<String>,
    usePdfFileNameAsDisplayName: Boolean,
    onFilterChange: (UnifiedLibraryFilter) -> Unit,
    onControls: () -> Unit,
    onAdvancedFilters: () -> Unit,
    onListViewChange: (Boolean) -> Unit,
    onBookClick: (RecentFileItem) -> Unit,
    onBookLongClick: (RecentFileItem) -> Unit,
    onImport: () -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Save both positions independently, including when the grid/list preference changes.
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val minimumCoverWidth = if (sharedMobileWidthClassForWidth(maxWidth) == SharedAndroidHomeWidthClass.COMPACT) {
            MyShelfLayout.compactBookWidth
        } else MyShelfLayout.largeBookWidth
        Column(Modifier.widthIn(max = MyShelfLayout.contentMaxWidth).fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(start = MyShelfLayout.pagePadding, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    pluralStringResource(R.plurals.book_count, books.size, books.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                BadgedBox(badge = { if (advancedFilterCount > 0) Badge { Text(advancedFilterCount.toString()) } }) {
                    IconButton(onClick = onAdvancedFilters) { Icon(Icons.Default.FilterList, stringResource(R.string.content_desc_filter)) }
                }
                IconButton(onClick = { onListViewChange(!useListView) }) {
                    Icon(
                        if (useListView) Icons.AutoMirrored.Filled.LibraryBooks else Icons.Default.FormatListNumbered,
                        stringResource(if (useListView) R.string.unified_library_grid_view else R.string.unified_library_list_view),
                    )
                }
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = MyShelfLayout.pagePadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                item(key = "sort") {
                    AssistChip(
                        onClick = onControls,
                        label = { Text(stringResource(sortOrder.labelRes)) },
                        leadingIcon = { Icon(Icons.Default.Sort, null, Modifier.size(18.dp)) },
                    )
                }
                items(UnifiedLibraryFilter.entries, key = { it.name }) { option ->
                    FilterChip(
                        selected = filter == option,
                        onClick = { onFilterChange(option) },
                        label = { Text(stringResource(option.labelRes)) },
                        leadingIcon = {
                            Icon(
                                when (option) {
                                    UnifiedLibraryFilter.ALL -> Icons.AutoMirrored.Filled.LibraryBooks
                                    UnifiedLibraryFilter.READING -> Icons.Default.PlayArrow
                                    UnifiedLibraryFilter.UNREAD -> Icons.Default.BookmarkBorder
                                    UnifiedLibraryFilter.FINISHED -> Icons.Default.Check
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                }
            }
            when {
                isLoading && books.isEmpty() -> MyShelfLibraryLoading(Modifier.weight(1f))
                books.isEmpty() -> LazyColumn(
                    Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item { Icon(Icons.AutoMirrored.Filled.LibraryBooks, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary) }
                    item { Text(stringResource(if (totalBookCount == 0) R.string.myshelf_library_empty_title else R.string.myshelf_no_matches), style = MaterialTheme.typography.titleLarge) }
                    if (totalBookCount == 0) {
                        item { Text(stringResource(R.string.myshelf_library_empty_body), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        item { Button(onClick = onImport) { Text(stringResource(R.string.fab_add_books)) } }
                    } else item { TextButton(onClick = onResetFilters) { Text(stringResource(R.string.myshelf_reset_filters)) } }
                }
                useListView -> LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(MyShelfLayout.pagePadding, 12.dp, MyShelfLayout.pagePadding, 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(books, key = { it.bookId }) { item ->
                        LibraryListItem(
                            item = item,
                            isSelected = item.bookId in selectedBookIds,
                            isPinned = item.bookId in pinnedBookIds,
                            onItemClick = { onBookClick(item) },
                            onItemLongClick = { onBookLongClick(item) },
                            isDownloading = item.bookId in downloadingBookIds,
                            usePdfFileNameAsDisplayName = usePdfFileNameAsDisplayName,
                        )
                    }
                }
                else -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(minimumCoverWidth),
                    state = gridState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(MyShelfLayout.pagePadding, 16.dp, MyShelfLayout.pagePadding, 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(MyShelfLayout.bookSpacing),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    items(books, key = { it.bookId }) { item ->
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                            RecentFileCard(
                                item = item,
                                isSelected = item.bookId in selectedBookIds,
                                isPinned = item.bookId in pinnedBookIds,
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
private fun MyShelfLibraryLoading(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(stringResource(R.string.myshelf_loading), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
